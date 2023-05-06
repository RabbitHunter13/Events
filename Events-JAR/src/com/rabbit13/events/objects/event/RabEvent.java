package com.rabbit13.events.objects.event;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.PlayerManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.rabbit13.events.main.Main.getFilMan;
import static com.rabbit13.events.main.Main.getPrefix;
import static com.rabbit13.events.main.Misc.*;

public class RabEvent implements InventoryHolder, Event, Listener {
    @Getter @Setter private int modificator;
    @Getter @Setter private boolean lockedTeleport;
    @Getter private Inventory modification;
    @Getter private String name;
    @Getter private String owner;
    @Getter private Location teleport;
    @Getter private final List<Location> checkpoints;
    @Getter private Location finish;
    @Getter private EventMods mods;
    @Getter private final List<String> banned;

    /**
     * Creates a new event instance.<br>
     * If checkpoints or banned are set to null, a new empty lists of them will be created
     *
     * @param name        name of this event
     * @param owner       owner of this event
     * @param teleport    defines a 3.dimensional point in event world, where player will be teleported after command /e
     * @param checkpoints if player steps on checkpoint, it will save that EventLocation to /e [ch]eckpoint
     * @param banned      lists of player names that are banned to come to an event
     */
    public RabEvent(@NotNull String name,
                    @NotNull String owner,
                    @NotNull Location teleport,
                    @Nullable Location finish,
                    @Nullable Location[] checkpoints,
                    @Nullable String[] banned,
                    @Nullable ConfigurationSection confMods) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
        this.lockedTeleport = false;
        this.finish = finish;
        //<editor-fold desc="Checkpoints">
        if (checkpoints != null) {
            this.checkpoints = new ArrayList<>(Arrays.asList(checkpoints));
        }
        else {
            this.checkpoints = new ArrayList<>();
        }
        //</editor-fold>
        //<editor-fold desc="Banned">
        if (banned != null) {
            this.banned = new ArrayList<>(Arrays.asList(banned));
        }
        else {
            this.banned = new ArrayList<>();
        }
        //</editor-fold>
        //<editor-fold desc="Mods">
        if (confMods != null) {
            this.mods = new RabEventMods(name, confMods);
        }
        else {
            this.mods = new RabEventMods(name);
        }
        //</editor-fold>
        this.modification = Bukkit.createInventory(this, 9, "Event Settings: " + name);
        initializeItems();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    /**
     * Creates a new event instance. This Constructor is used for command creation<br>
     * If checkpoints or banned are set to null, a new empty lists of them will be created
     *
     * @param name     name of this event
     * @param owner    owner of this event
     * @param teleport defines a 3.dimensional point in event world, where player will be teleported after command /e
     */
    public RabEvent(String name,
                    String owner,
                    Location teleport) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
        this.lockedTeleport = false;
        this.checkpoints = new ArrayList<>();
        this.banned = new ArrayList<>();
        this.mods = new RabEventMods(name);
        this.modification = Bukkit.createInventory(this, 18, "Event Settings: " + name);
        initializeItems();
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    /**
     * Initialize items in Modification Inventory
     */
    private void initializeItems() {
        assert teleport.getWorld() != null;
        modification.setItem(0, getSpecifiedItem(Material.GRASS_BLOCK, 1, name));
        modification.setItem(1, getPlayerSkull(owner));
        modification.setItem(2, getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Teleport",
                                                 "World: " + teleport.getWorld().getName(),
                                                 "&fx: " + teleport.getBlock().getX(),
                                                 "&fy: " + teleport.getBlock().getY(),
                                                 "&fz: " + teleport.getBlock().getZ()));
        modification.setItem(3, getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Finish",
                                                 "World: " + teleport.getWorld().getName(),
                                                 "&fx: " + teleport.getBlock().getX(),
                                                 "&fy: " + teleport.getBlock().getY(),
                                                 "&fz: " + teleport.getBlock().getZ()));
        modification.setItem(8, getSpecifiedItem(Material.CHEST, 1, "Mods"));
    }

    /**
     * Set and update values of event thru ChatListener
     *
     * @param slot   slot of {@link org.bukkit.inventory.ItemStack ItemStack} that is being updated - holds old modified info
     * @param data   data that is being loaded into value
     * @param player player who clicked
     */
    public void updateItems(int slot, String data, Player player) {
        if (slot == 0) {
            name = data;
            modification.getViewers().forEach(HumanEntity::closeInventory);
            Inventory newI = Bukkit.createInventory(this, 9, "Event Settings: " + name);
            newI.setContents(modification.getContents());
            modification = newI;
            modification.setItem(slot, getSpecifiedItem(Material.GRASS_BLOCK, 1, name));
            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                           .replace("%key%", "name")
                           .replace("%value%", name)
                    , true
                    , player);
        }
        else if (slot == 1) {
            owner = data;
            modification.setItem(slot, getPlayerSkull(owner));
            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                           .replace("%key%", "owner")
                           .replace("%value%", owner)
                    , true
                    , player);
        }
    }

    /**
     * Update without ChatListener
     *
     * @param slot   index of clicked slot
     * @param player player who clicked
     */
    public void updateItems(int slot, Player player) {
        switch (slot) {
            case 2:
                teleport = player.getLocation();
                modification.setItem(slot, getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Teleport",
                                                            "World: " + Objects.requireNonNull(teleport.getWorld()).getName(),
                                                            "&fx: " + teleport.getBlock().getX(),
                                                            "&fy: " + teleport.getBlock().getY(),
                                                            "&fz: " + teleport.getBlock().getZ()));
                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                               .replace("%key%", "teleport")
                               .replace("%value%",
                                        "&8[&6x:&e" + teleport.getBlock().getX() +
                                                " &6y:&e" + teleport.getBlock().getY() +
                                                " &6z:&e" + teleport.getBlock().getZ() + "&8]")
                        , true
                        , player);
                player.closeInventory();
                break;
            case 3:
                finish = player.getLocation();
                modification.setItem(3, getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Finish",
                                                         "World: " + Objects.requireNonNull(finish.getWorld()).getName(),
                                                         "&fx: " + finish.getBlock().getX(),
                                                         "&fy: " + finish.getBlock().getY(),
                                                         "&fz: " + finish.getBlock().getZ()));
                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                               .replace("%key%", "finish")
                               .replace("%value%",
                                        "&8[&6x:&e" + teleport.getBlock().getX() +
                                                " &6y:&e" + teleport.getBlock().getY() +
                                                " &6z:&e" + teleport.getBlock().getZ() + "&8]")
                        , true
                        , player);
                break;
            case 8:
                player.openInventory(mods.getInventory());
                break;
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getWhoClicked().getOpenInventory().getTopInventory() == this.getInventory()) {
            if (e.getClickedInventory() == this.getInventory()) {
                if (e.getSlot() == 0 || e.getSlot() == 1) {
                    debugMessage("editing string values");
                    PlayerManager.getModifyingEvent().put((Player) e.getWhoClicked(), new AbstractMap.SimpleEntry<>(e.getSlot(), this));
                    e.getWhoClicked().closeInventory();
                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-init"))
                                   .replace("%value%", (e.getSlot() == 0) ? "Name" : "Owner"),
                           true,
                           e.getWhoClicked());
                }
                else {
                    debugMessage("editing someting else than string values, no chat listener needed");
                    this.updateItems(e.getSlot(), (Player) e.getWhoClicked());
                }
                debugMessage("Clicked slot: " + e.getSlot());
                debugMessage("Clicked type: " + e.getAction().toString());
                e.setCancelled(true);
            }
            else {
                e.setCancelled(true);
            }
        }
    }


    @NotNull
    @Override
    public Inventory getInventory() {
        return modification;
    }

    public static class RabType implements Type {
        @Getter private String type;
    }
}
