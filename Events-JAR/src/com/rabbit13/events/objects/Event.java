package com.rabbit13.events.objects;

import com.rabbit13.events.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.rabbit13.events.main.Misc.*;

public class Event implements InventoryHolder {
    private final List<String> checkpoints;
    private final List<String> banned;
    private String name;
    private String owner;
    private EventMods mods;
    private Location teleport;
    private boolean lockedTeleport;
    private Inventory modification;
    public int modificator;

    /**
     * Creates a new event instance.<br>
     * If checkpoints or banned are set to null, a new empty lists of them will be created
     *
     * @param name        name of this event
     * @param owner       owner of this event
     * @param teleport    defines a 3.dimensional point in event world, where player will be teleported after command /e
     * @param checkpoints if player steps on checkpoint, it will save that location to /e [ch]eckpoint
     * @param banned      lists of player names that are banned to come to an event
     */
    public Event(String name, String owner, Location teleport, @Nullable String[] checkpoints, @Nullable String[] banned, ConfigurationSection confMods) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
        this.lockedTeleport = false;
        if (checkpoints != null) {
            this.checkpoints = Arrays.asList(checkpoints);
        }
        else {
            this.checkpoints = new ArrayList<>();
        }
        if (banned != null) {
            this.banned = Arrays.asList(banned);
        }
        else {
            this.banned = new ArrayList<>();
        }

        this.mods = new EventMods(confMods);
        this.modification = Bukkit.createInventory(this, 9, name);
        initializeItems();
    }

    /**
     * Creates a new event instance. This Constructor is uded for command creation<br>
     * If checkpoints or banned are set to null, a new empty lists of them will be created
     *
     * @param name        name of this event
     * @param owner       owner of this event
     * @param teleport    defines a 3.dimensional point in event world, where player will be teleported after command /e
     * @param checkpoints if player steps on checkpoint, it will save that location to /e [ch]eckpoint
     * @param banned      lists of player names that are banned to come to an event
     */
    public Event(String name, String owner, Location teleport, @Nullable String[] checkpoints, @Nullable String[] banned) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
        this.lockedTeleport = false;
        if (checkpoints != null) {
            this.checkpoints = Arrays.asList(checkpoints);
        }
        else {
            this.checkpoints = new ArrayList<>();
        }
        if (banned != null) {
            this.banned = Arrays.asList(banned);
        }
        else {
            this.banned = new ArrayList<>();
        }

        mods = new EventMods();
        this.modification = Bukkit.createInventory(this, 9, "Event Settings");
        initializeItems();
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return modification;
    }

    @SuppressWarnings("deprecation")
    private void initializeItems() {
        modification.addItem(
                getSpecifiedItem(Material.GRASS, 1, name),
                getPlayerSkull(owner),
                getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Teleport", "World: " + teleport.getWorld().getName(), "&fx: " + teleport.getX(), "&fy: " + teleport.getY(), "&fz: " + teleport.getZ()),
                getSpecifiedItem(Material.CHEST,1,"Mods")
        );
    }

    /**
     * Set and update values of event thru ChatListener
     *
     * @param slot   slot of {@link org.bukkit.inventory.ItemStack ItemStack} that is being updated - holds old modified info
     * @param data   data that is being loaded into value
     * @param player player who clicked
     */
    @SuppressWarnings("deprecation")
    public void updateItems(int slot, String data, Player player) {
        switch (slot) {
            case 0:
                name = data;
                modification.setItem(slot, getSpecifiedItem(Material.GRASS, 1, name));
                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                                .replace("%key%", "name")
                                .replace("%value%", name)
                        , true
                        , player
                );
                break;
            case 1:
                owner = data;
                modification.setItem(slot, getPlayerSkull(owner));
                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                                .replace("%key%", "owner")
                                .replace("%value%", owner)
                        , true
                        , player
                );
                break;
        }
    }

    /**
     * Update without ChatListener
     *
     * @param slot   index of clicked slot
     * @param player player who clicked
     */
    public void updateItems(int slot, Player player) {
        if (slot == 2) {
            teleport = player.getLocation();
            modification.setItem(slot, getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Teleport", "World: " + teleport.getWorld().getName(), "&fx: " + teleport.getX(), "&fy: " + teleport.getY(), "&fz: " + teleport.getZ()));
            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                            .replace("%key%", "name")
                            .replace("%value%", "&8[&6x:&e" + (int) teleport.getX() + " &6y:&e" + (int) teleport.getY() + " &6z:&e" + (int) teleport.getZ() + "&8]")
                    , true
                    , player
            );
            player.closeInventory();
        }
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public Location getTeleport() {
        return teleport;
    }

    public boolean isLocked() {
        return lockedTeleport;
    }

    public void setLocked(boolean locked) {
        this.lockedTeleport = locked;
    }

    public EventMods getMods() {
        return mods;
    }

    public List<String> getCheckpoints() {
        return checkpoints;
    }

    public List<String> getBanned() {
        return banned;
    }
}
