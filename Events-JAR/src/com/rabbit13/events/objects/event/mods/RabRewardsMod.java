package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.PlayerManager;
import com.rabbit13.events.objects.event.EventMods;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.rabbit13.events.main.Main.getFilMan;
import static com.rabbit13.events.main.Main.getPrefix;
import static com.rabbit13.events.main.Misc.*;

public final class RabRewardsMod extends RabMod implements RewardItems, Listener {
    @Getter
    private Inventory rewards;
    @Getter
    private int maxWins;
    @Getter @Setter
    private int winnerIndex;

    public RabRewardsMod(EventMods mods) {
        super(mods);
        maxWins = 3;
        winnerIndex = 0;
        rewards = Bukkit.createInventory(this, 45, "Rewards");
        initialize();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }


    public RabRewardsMod(EventMods mods, int maxWins, ItemStack[] items, boolean active) {
        super(mods);
        this.maxWins = maxWins;
        winnerIndex = 0;
        rewards = Bukkit.createInventory(this, 45, "Rewards");
        initialize();
        if (items != null && items.length > 0) {
            rewards.setContents(items);
        }
        setEnabled(active);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    private void initialize() {
        getInventory().setItem(1, getSpecifiedItem(Material.CHEST, 1, "Rewards",
                                                   "&eClick to open inventory"));
        getInventory().setItem(2, getSpecifiedItem(Material.BOOK, 1, "Max wins",
                                                   "&eClick to change max winners",
                                                   "&eMax winners: &6" + maxWins));
        for (int i = 0; i < 9; i++) {
            rewards.setItem(i, getSpecifiedItem(Material.YELLOW_STAINED_GLASS_PANE, 1, "&6" + (i + 1) + ". Misto"));
        }
        rewards.setItem(44, getSpecifiedItem(Material.ARROW, 1, "&cBack"));
    }

    public List<ItemStack> getRewardRow(int row) {
        List<ItemStack> items = new ArrayList<>();
        debugMessage("getRewardRow row: " + row);
        if (row >= 0 && row <= 8) {
            for (int i = 9 + row; i < 45; i += 9) {
                ItemStack item = rewards.getItem(i);

                if (item != null)
                    items.add(rewards.getItem(i));
            }
        }
        return items;
    }

    /**
     * controls the whole reward process
     *
     * @return rewards
     */
    public ItemStack[] giveRewardToPlayer() {
        val items = getRewardRow(winnerIndex).toArray(new ItemStack[0]);
        winnerIndex++;
        if (winnerIndex > maxWins) {
            winnerIndex = 0;
        }
        return items;
    }

    @EventHandler
    public void modify(InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory() == getInventory()) {
            if (e.getClickedInventory() == getInventory()) {
                if (e.getSlot() == 1) {
                    e.getWhoClicked().openInventory(rewards);
                }
                else if (e.getSlot() == 2) {
                    PlayerManager.getModifyingMods().put((Player) e.getWhoClicked(), new AbstractMap.SimpleEntry<>(e.getSlot(), this));
                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-init"))
                            .replace("%value%", "Max_Wins"), true, e.getWhoClicked());
                    e.getWhoClicked().closeInventory();
                }
            }
        }
        else if (e.getWhoClicked().getOpenInventory().getTopInventory() == rewards) {
            if (e.getClickedInventory() == rewards) {
                if (e.getSlot() >= 0 && e.getSlot() <= 8) {
                    e.setCancelled(true);
                }
                if (e.getSlot() == 44) {
                    e.getWhoClicked().openInventory(super.getInventory());
                    e.setCancelled(true);
                }
            }
        }
    }

    @Override public void chatUpdate(int slot, String text, CommandSender sender) {
        try {
            int maxWins = Integer.parseInt(text);
            if (maxWins < 1) {
                maxWins = 1;
            }
            else if (maxWins > 8) {
                maxWins = 8;
            }
            this.maxWins = maxWins;
            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-finished"))
                           .replace("%key%", this.getMods().getOwner() + "/" + "Max_Wins")
                           .replace("%value%", Integer.toString(maxWins))
                    , true, sender);
        } catch (NumberFormatException e) {
            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-modification-number-error"), true, sender);
        }
        //noinspection SuspiciousMethodCalls
        PlayerManager.getModifyingMods().remove(sender);
    }
}
