package com.rabbit13.events.managers;

import com.rabbit13.events.objects.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.rabbit13.events.main.Main.*;
import static com.rabbit13.events.main.Misc.debugMessage;
import static com.rabbit13.events.main.Misc.sendLM;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class BackupItemsManager {
    static File path = new File(getInstance().getDataFolder().getPath() + File.separator + "data" + File.separator + "inventory_backups");

    public static void createBackup(Player player, @NotNull PlayerData data) {
        YamlConfiguration backup = new YamlConfiguration();

        if (data.getItems().length > 0) {
            List<ItemStack> items = new ArrayList<>();
            for (ItemStack item : data.getItems()) {
                if (item != null) {
                    items.add(item);

                }
            }
            backup.set("level", data.getLevel());
            backup.set("exp", data.getExp());
            backup.set("items", items);

        }
        try {
            File backupFile = new File(path, player.getName() + ".yml");
            debugMessage("Backup Path:" + path.getPath());
            path.mkdir();
            if (!backupFile.exists()) {
                backupFile.createNewFile();
            }
            backup.save(backupFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    public static Inventory getBackup(Player adminSender, String playerName) {
        Inventory backup = Bukkit.createInventory(adminSender, 45);
        YamlConfiguration ymlBackup;
        ymlBackup = YamlConfiguration.loadConfiguration(new File(path, playerName + ".yml"));
        List<?> itemList;
        if (!ymlBackup.getKeys(false).isEmpty()) {
            itemList = ymlBackup.getList("items");
            sendLM(getPrefix() + " " + "Level: " + ymlBackup.get("level", 0), true, adminSender);
            sendLM(getPrefix() + " " + "Exp: " + ymlBackup.get("exp", 0), true, adminSender);
            if (itemList != null && !itemList.isEmpty()) {
                ItemStack[] items = itemList.toArray(new ItemStack[0]);
                for (ItemStack item : items) {
                    if (item != null) {
                        backup.addItem(item);
                    }
                }
            }
            return backup;
        }
        else {
            sendLM(getPrefix() + " " + getFilMan().getWords().getString("backup-not-found"), true, adminSender);
        }
        return null;
    }

    public static File getPath() {
        return path;
    }
}
