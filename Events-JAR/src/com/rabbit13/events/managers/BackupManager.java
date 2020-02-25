package com.rabbit13.events.managers;

import com.rabbit13.events.objects.Backup;
import com.rabbit13.events.objects.PlayerData;
import lombok.Getter;
import lombok.val;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.rabbit13.events.main.Main.getInstance;
import static com.rabbit13.events.main.Misc.debugMessage;

public class BackupManager {
    @Getter private static final File path = new File(getInstance().getDataFolder().getPath() +
                                                              File.separator + "data" +
                                                              File.separator + "inventory_backups");
    @Getter private static Map<String, Backup> backups = new HashMap<>();


    public BackupManager() {
        if (!path.exists()) {
            //noinspection ResultOfMethodCallIgnored
            path.mkdirs();
        }
        initializeBackups();
    }

    public static void initializeBackups() {
        for (String path : Objects.requireNonNull(path.list())) {
            path = path.replace(".yml", "");
            val temp = BackupManager.getBackups().put(path, new Backup(path));
            debugMessage("initializteBackups#path: " + path);
            debugMessage("backup rewritten?: " + (temp != null));
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void createBackup(@NotNull Player player, @NotNull PlayerData data) {
        YamlConfiguration backup = new YamlConfiguration();
        //<editor-fold desc="Level">
        backup.set("level", data.getLevel());
        backup.set("exp", data.getExp());
        //</editor-fold>
        //<editor-fold desc="Items">
        if (data.getItems().length > 0) {
            List<ItemStack> items = new ArrayList<>();
            for (ItemStack item : data.getItems()) {
                if (item != null) {
                    items.add(item);
                }
            }
            backup.set("items", items);
        }
        //</editor-fold>
        //<editor-fold desc="Ender Chest">
        if (data.getEnderChest().length > 0) {
            List<ItemStack> ender = new ArrayList<>();
            for (ItemStack item : data.getEnderChest()) {
                if (item != null) {
                    ender.add(item);
                }
            }
            backup.set("ender", ender);

        }
        //</editor-fold>
        //<editor-fold desc="Save to file">
        try {
            File backupFile = new File(path, player.getName() + ".yml");
            debugMessage("Backup Path:" + path.getPath());
            path.mkdir();
            if (!backupFile.exists()) {
                backupFile.createNewFile();
            }
            backup.save(backupFile);
            Backup temp = backups.put(player.getName(), new Backup(player.getName(), data));
            debugMessage("backup rewritten?: " + (temp != null));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //</editor-fold>
    }
}
