package com.rabbit13.events.objects;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.BackupManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.Objects;

import static com.rabbit13.events.main.Misc.error;
import static com.rabbit13.events.main.Misc.getSpecifiedItem;


public class Backup {
    @Getter private final InventoryHolder holder;
    @Getter private final Inventory enderInv;
    private Inventory inventory;
    @Getter private int level;
    @Getter private float exp;

    /**
     * When player connected to event and created backup
     *
     * @param name players name
     * @param data players data (inventories etc)
     */
    public Backup(String name, PlayerData data) {
        holder = () -> inventory;
        inventory = Bukkit.createInventory(holder, 54, name + "'s Inventory");
        enderInv = Bukkit.createInventory(holder, 27, name + "'s Ender Chest");
        inventory.setContents(data.getItems());
        enderInv.setContents(data.getEnderChest());
        level = data.getLevel();
        exp = data.getExp();
        initializeItems();
    }

    /**
     * new event loaded from yamlconfiguration with given name
     *
     * @param name players name
     */
    @SuppressWarnings("SuspiciousToArrayCall")
    public Backup(String name) {
        holder = () -> inventory;
        inventory = Bukkit.createInventory(holder, 54, name + "'s Inventory");
        enderInv = Bukkit.createInventory(holder, 27, name + "'s Ender Chest");
        File file = new File(BackupManager.getPath(), name + ".yml");
        if (file.exists()) {
            YamlConfiguration ymlBackup = YamlConfiguration.loadConfiguration(file);
            List<?> listItems = ymlBackup.getList("items");
            if (listItems != null) {
                ItemStack[] arrayItems = listItems.toArray(new ItemStack[0]);
                inventory.setContents(arrayItems);
            }
            initializeItems();
            List<?> listEnder = ymlBackup.getList("ender");
            if (listEnder != null) {
                ItemStack[] arrayEnder = listEnder.toArray(new ItemStack[0]);
                enderInv.setContents(arrayEnder);
            }
        }
        else {
            error(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("backup-not-found"))
                    .replace("%name%", name + ".yml"));
        }
    }

    private void initializeItems() {
        inventory.setItem(52, getSpecifiedItem(Material.GREEN_STAINED_GLASS_PANE, 1, "Retrieve all items",
                                               "Click to give player back his items " +
                                                       "&cHe must be online"));
        inventory.setItem(53, getSpecifiedItem(Material.ENDER_CHEST, 1, "Ender Chest",
                                               "&eClick to open backuped EnderChest"));
    }

}
