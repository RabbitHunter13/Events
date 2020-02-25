package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.PlayerManager;
import com.rabbit13.events.objects.event.EventMods;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.AbstractMap;
import java.util.Objects;

import static com.rabbit13.events.main.Main.getFilMan;
import static com.rabbit13.events.main.Main.getPrefix;
import static com.rabbit13.events.main.Misc.getSpecifiedItem;
import static com.rabbit13.events.main.Misc.sendLM;

public final class RabMoreHPMod extends RabMod implements MoreHP, Listener {
    @Getter @Setter
    private double health;

    public RabMoreHPMod(EventMods mods) {
        super(mods);
        initialize();
        health = 20;
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public RabMoreHPMod(EventMods mods, double health, boolean active) {
        super(mods);
        this.health = health;
        initialize();
        setEnabled(active);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public void initialize() {
        getInventory().setItem(1, getSpecifiedItem(Material.BOOK, 1, "Health",
                                                   "set how much health players should have",
                                                   "value: " + health));
    }

    @EventHandler
    public void modify(InventoryClickEvent e) {
        if (e.getClickedInventory() == getInventory()) {
            if (e.getSlot() == 1) {
                PlayerManager.getModifyingMods().put((Player) e.getWhoClicked(), new AbstractMap.SimpleEntry<>(1, this));
                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-init"))
                               .replace("%value%", "Health"),
                       true, e.getWhoClicked());
                e.getWhoClicked().closeInventory();
            }
        }
    }

    @Override
    public void chatUpdate(int slot, String text, CommandSender sender) {
        try {
            double rawHealth = Double.parseDouble(text);
            if (rawHealth < 1) {
                health = 1;
            }
            else {
                health = rawHealth;
            }
            sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-finished"))
                           .replace("%key%", this.getMods().getOwner() + "/" + "Health")
                           .replace("%value%", Double.toString(health))
                    , true, sender);
            getInventory().setItem(1, getSpecifiedItem(Material.BOOK, 1, "Health",
                                                       "set how much health players should have",
                                                       "value: " + health));
        } catch (NumberFormatException e) {
            sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-modification-number-error"), true, sender);
        }
        //noinspection SuspiciousMethodCalls
        PlayerManager.getModifyingMods().remove(sender);
    }
}
