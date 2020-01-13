package com.rabbit13.events.objects.event;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.event.mods.EventMods;
import com.rabbit13.events.objects.event.mods.RabEventMods;
import com.rabbit13.events.objects.event.tools.EventLocation;
import com.rabbit13.events.objects.event.tools.RabEventLocation;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

public class RabEvent implements InventoryHolder, Event {
    @Getter @Setter private int modificator;
    @Getter @Setter private boolean lockedTeleport;
    @Getter private Inventory modification;
    @Getter private String name;
    @Getter private String owner;
    @Getter private EventLocation teleport;
    @Getter private final List<Location> checkpoints;
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
    public RabEvent(String name,
                    String owner,
                    RabEventLocation teleport,
                    @Nullable Location[] checkpoints,
                    @Nullable String[] banned,
                    @Nullable ConfigurationSection confMods) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
        this.lockedTeleport = false;
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
            this.banned = Arrays.asList(banned);
        }
        else {
            this.banned = new ArrayList<>();
        }
        //</editor-fold>
        //<editor-fold desc="Mods">
        if (confMods != null) {
            this.mods = new RabEventMods(confMods);
        }
        else {
            this.mods = new RabEventMods();
        }
        //</editor-fold>
        this.modification = Bukkit.createInventory(this, 9, "Event Settings: " + name);
        initializeItems();
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
                    RabEventLocation teleport) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
        this.lockedTeleport = false;
        this.checkpoints = new ArrayList<>();
        this.banned = new ArrayList<>();
        this.mods = new RabEventMods();
        this.modification = Bukkit.createInventory(this, 18, "Event Settings: " + name);
        initializeItems();
    }

    /**
     * Initialize items in Modification Inventory
     */
    private void initializeItems() {
        assert teleport.getLocation().getWorld() != null;
        modification.setItem(0, getSpecifiedItem(Material.GRASS_BLOCK, 1, name));
        modification.setItem(1, getPlayerSkull(owner));
        modification.setItem(2, getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Teleport",
                                                 "World: " + teleport.getLocation().getWorld().getName(),
                                                 "&fx: " + teleport.getLocation().getBlock().getX(),
                                                 "&fy: " + teleport.getLocation().getBlock().getY(),
                                                 "&fz: " + teleport.getLocation().getBlock().getZ()));
        modification.setItem(8, getSpecifiedItem(Material.CHEST, 1, "Mods"));
    }

    /**
     * Set and update values of event thru ChatListenerd
     *
     * @param slot   slot of {@link org.bukkit.inventory.ItemStack ItemStack} that is being updated - holds old modified info
     * @param data   data that is being loaded into value
     * @param player player who clicked
     */
    public void updateItems(int slot, String data, Player player) {
        if (slot == 0) {
            name = data;
            modification.setItem(slot, getSpecifiedItem(Material.GRASS_BLOCK, 1, name));
            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                           .replace("%key%", "name")
                           .replace("%value%", name)
                    , true
                    , player
            );
        }
        else if (slot == 1) {
            owner = data;
            modification.setItem(slot, getPlayerSkull(owner));
            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                           .replace("%key%", "owner")
                           .replace("%value%", owner)
                    , true
                    , player
            );
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
                World world = teleport.getLocation().getWorld();
                assert world != null;
                teleport = new RabEventLocation(player.getLocation());
                modification.setItem(slot, getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Teleport",
                                                            "World: " + world.getName(),
                                                            "&fx: " + teleport.getLocation().getBlock().getX(),
                                                            "&fy: " + teleport.getLocation().getBlock().getY(),
                                                            "&fz: " + teleport.getLocation().getBlock().getZ()));
                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                               .replace("%key%", "name")
                               .replace("%value%",
                                        "&8[&6x:&e" + teleport.getLocation().getBlock().getX() +
                                                " &6y:&e" + teleport.getLocation().getBlock().getY() +
                                                " &6z:&e" + teleport.getLocation().getBlock().getZ() + "&8]")
                        , true
                        , player
                );
                player.closeInventory();
                break;
            case 8:
                player.openInventory(mods.getInventory());
                break;
        }
    }


    @NotNull
    @Override
    public Inventory getInventory() {
        return modification;
    }
}
