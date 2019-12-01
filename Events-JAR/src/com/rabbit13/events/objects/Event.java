package com.rabbit13.events.objects;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.EventManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    private EventLocation teleport;
    private boolean lockedTeleport;
    private boolean fallDamage;
    private boolean lavaEqualsFail;
    private Inventory modification;
    public int modificator;

    /**
     * Creates a new event instance.<br>
     * If checkpoints or banned are set to null, a new empty lists of them will be created
     *
     * @param name           name of this event
     * @param owner          owner of this event
     * @param teleport       defines a 3.dimensional point in event world, where player will be teleported after command /e
     * @param checkpoints    if player steps on checkpoint, it will save that location to /e [ch]eckpoint
     * @param banned         lists of player names that are banned to come to an event
     * @param fallDamage     defines if fall damage players (true if fall damage is disabled)
     * @param lavaEqualsFail defines if falling into lava means disqualifying player
     */
    public Event(String name, String owner, EventLocation teleport, @Nullable String[] checkpoints, @Nullable String[] banned, boolean fallDamage, boolean lavaEqualsFail) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
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
        this.lockedTeleport = false;
        this.fallDamage = fallDamage;
        this.lavaEqualsFail = lavaEqualsFail;
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
    public Event(String name, String owner, EventLocation teleport, @Nullable String[] checkpoints, @Nullable String[] banned) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
        this.fallDamage = true;
        this.lavaEqualsFail = true;
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
        this.lockedTeleport = false;
        this.modification = Bukkit.createInventory(this, 9, "Event Settings");
        initializeItems();
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return modification;
    }

    private void initializeItems() {
        modification.addItem(
                getSpecifiedItem(Material.GRASS, 1, name),
                getPlayerSkull(owner),
                getSpecifiedItem(Material.COMMAND, 1, "Teleport", "&fx: " + teleport.getX(), "&fy: " + teleport.getY(), "&fz: " + teleport.getZ()),
                getSpecifiedItem(Material.FEATHER, 1, "Fall Damage Setting", "Fall damage enabled: " + (fallDamage ? "&a" + true : "&c" + false)),
                getSpecifiedItem(Material.LAVA_BUCKET, 1, "Lava Setting", "Lava means Event fail: " + (lavaEqualsFail ? "&a" + true : "&c" + false))

        );
    }

    /**
     * Set and update values of event
     *
     * @param slot   slot of {@link org.bukkit.inventory.ItemStack ItemStack} that is being updated - holds old modified info
     * @param data   data that is being loaded into value
     * @param player player who clicked
     */
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
        switch (slot) {
            case 2:
                teleport = new EventLocation(player.getLocation());
                modification.setItem(slot, getSpecifiedItem(Material.COMMAND, 1, "Teleport", "&fx: " + teleport.getX(), "&fy: " + teleport.getY(), "&fz: " + teleport.getZ()));
                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                                .replace("%key%", "name")
                                .replace("%value%", "&8[&6x:&e" + (int) teleport.getX() + " &6y:&e" + (int) teleport.getY() + " &6z:&e" + (int) teleport.getZ() + "&8]")
                        , true
                        , player
                );
                break;
            case 3:
                fallDamage = (!fallDamage);
                modification.setItem(slot, getSpecifiedItem(Material.FEATHER, 1, "Fall Damage Setting", "Fall damage enabled: " + (fallDamage ? "&a" + true : "&c" + false)));
                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                                .replace("%key%", "Fall_Damage")
                                .replace("%value%", Boolean.toString(fallDamage))
                        , true
                        , player);
                break;
            case 4:
                lavaEqualsFail = (!lavaEqualsFail);
                modification.setItem(slot, getSpecifiedItem(Material.LAVA_BUCKET, 1, "Fall Damage Setting", "Lava equals fail enabled: " + (lavaEqualsFail ? "&a" + true : "&c" + false)));
                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                                .replace("%key%", "Fall_Damage")
                                .replace("%value%", Boolean.toString(lavaEqualsFail))
                        , true
                        , player);
                break;
        }
    }

    public boolean getFallDamage() {
        return fallDamage;
    }

    public boolean getLavaEqualsFail() {
        return lavaEqualsFail;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public EventLocation getTeleport() {
        return teleport;
    }

    public boolean isLocked() {
        return lockedTeleport;
    }

    public void setLocked(boolean locked) {
        this.lockedTeleport = locked;
    }

    public List<String> getCheckpoints() {
        return checkpoints;
    }

    public List<String> getBanned() {
        return banned;
    }
}
