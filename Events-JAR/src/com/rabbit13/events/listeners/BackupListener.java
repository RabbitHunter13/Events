package com.rabbit13.events.listeners;

import com.rabbit13.events.managers.BackupManager;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.rabbit13.events.main.Main.getFilMan;
import static com.rabbit13.events.main.Main.getPrefix;
import static com.rabbit13.events.main.Misc.debugMessage;
import static com.rabbit13.events.main.Misc.sendLM;

public class BackupListener implements Listener {
    @SuppressWarnings("ConstantConditions") @EventHandler
    public void onItemPickup(InventoryClickEvent e) {
        for (val entry : BackupManager.getBackups().entrySet()) {
            val entryValue = entry.getValue();
            if (entryValue != null) {
                debugMessage("Backup#" + entry.getKey() + " Inventory equals: " + (e.getClickedInventory() == entryValue.getHolder().getInventory()));
                if (e.getClickedInventory() == entryValue.getHolder().getInventory()) {
                    if (e.getSlot() == 52) {
                        e.setCancelled(true);
                        val player = Bukkit.getPlayer(entry.getKey());
                        if (player != null) {
                            //<editor-fold desc="Player freespace count">
                            int freeSpaceInventory = 36;
                            for (val content : player.getInventory().getContents()) {
                                if (content != null) {
                                    freeSpaceInventory -= 1;
                                }
                            }
                            int freeSpaceEnder = 27;
                            for (val content : player.getEnderChest().getContents()) {
                                if (content != null) {
                                    freeSpaceEnder -= 1;
                                }
                            }
                            //</editor-fold>
                            //<editor-fold desc="Backup collect contents">
                            List<ItemStack> items = new ArrayList<>();
                            @NotNull ItemStack[] contents = entryValue.getHolder().getInventory().getContents();
                            for (int i = 0; i < contents.length - 2; i++) {
                                ItemStack content = contents[i];
                                if (content != null) {
                                    items.add(content);
                                }
                            }
                            List<ItemStack> ender = new ArrayList<>();
                            for (val content : entryValue.getEnder().getContents()) {
                                if (content != null) {
                                    ender.add(content);
                                }
                            }
                            //</editor-fold>
                            if (items.size() <= freeSpaceInventory && ender.size() <= freeSpaceEnder) {
                                player.getInventory().addItem(items.toArray(new ItemStack[0]));
                                player.getEnderChest().addItem(ender.toArray(new ItemStack[0]));
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("backup-returned"), true, e.getWhoClicked());
                            }
                            else {
                                sendLM(getPrefix() + " " + getFilMan().getWords().getString("backup-full-inventory"), true, e.getWhoClicked());
                            }
                        }
                        else {
                            sendLM(getPrefix() + " " + getFilMan().getWords().getString("player-not-found"), true, e.getWhoClicked());
                        }
                    }
                    else if (e.getSlot() == 53) {
                        e.setCancelled(true);
                        e.getWhoClicked().openInventory(entryValue.getEnder());
                    }
                    break;
                }
            }
        }
    }
}
