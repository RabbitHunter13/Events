package com.rabbit13.events.objects;

import com.rabbit13.events.main.Main;
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

public class eEvent implements InventoryHolder, Event {
    private final List<Location> checkpoints;
    private final List<String> banned;
    private String name;
    private String owner;
    private eEventMods mods;
    private eEventLocation teleport;
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
     * @param checkpoints if player steps on checkpoint, it will save that EventLocation to /e [ch]eckpoint
     * @param banned      lists of player names that are banned to come to an event
     */
    public eEvent(String name, String owner, eEventLocation teleport, @Nullable String[] checkpoints, @Nullable String[] banned, ConfigurationSection confMods) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
        this.lockedTeleport = false;
        if (checkpoints != null) {
            List<Location> locCheckpoints = new ArrayList<>();
            for (String checkpoint : checkpoints) {
                try {
                    assert checkpoint != null;
                    String[] splitted = checkpoint.split(",");
                    double[] splittedParsed = {Double.parseDouble(splitted[0]), Double.parseDouble(splitted[1]), Double.parseDouble(splitted[2])};
                    locCheckpoints.add(new Location(teleport.getLocation().getWorld(),splittedParsed[0], splittedParsed[1], splittedParsed[2]));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            this.checkpoints = locCheckpoints;
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

        if (confMods != null) {
            this.mods = new eEventMods(confMods);
        }
        else {
            this.mods = new eEventMods();
        }

        this.modification = Bukkit.createInventory(this, 9, "Event Settings: " + name);
        initializeItems();
    }

    /**
     * Creates a new event instance. This Constructor is used for command creation<br>
     * If checkpoints or banned are set to null, a new empty lists of them will be created
     *
     * @param name        name of this event
     * @param owner       owner of this event
     * @param teleport    defines a 3.dimensional point in event world, where player will be teleported after command /e
     */
    public eEvent(String name, String owner, eEventLocation teleport) {
        this.name = name;
        this.owner = owner;
        this.teleport = teleport;
        this.lockedTeleport = false;
        this.checkpoints = new ArrayList<>();
        this.banned = new ArrayList<>();

        mods = new eEventMods();
        this.modification = Bukkit.createInventory(this, 9, "Event Settings: " + name);
        initializeItems();
    }

    @SuppressWarnings("deprecation")
    private void initializeItems() {
        assert teleport.getWorld() != null;
        modification.addItem(
                getSpecifiedItem(Material.GRASS_BLOCK, 1, name),
                getPlayerSkull(owner),
                getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Teleport", "World: " + teleport.getWorld().getName(), "&fx: " + teleport.getX(), "&fy: " + teleport.getY(), "&fz: " + teleport.getZ()),
                getSpecifiedItem(Material.CHEST, 1, "Mods")
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
                modification.setItem(slot, getSpecifiedItem(Material.GRASS_BLOCK, 1, name));
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
            World world = teleport.getWorld();
            assert world != null;
            teleport = new eEventLocation(player.getLocation());
            modification.setItem(slot, getSpecifiedItem(Material.COMMAND_BLOCK, 1, "Teleport", "World: " + world.getName(), "&fx: " + teleport.getX(), "&fy: " + teleport.getY(), "&fz: " + teleport.getZ()));
            sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                           .replace("%key%", "name")
                           .replace("%value%", "&8[&6x:&e" + (int) teleport.getX() + " &6y:&e" + (int) teleport.getY() + " &6z:&e" + (int) teleport.getZ() + "&8]")
                    , true
                    , player
            );
            player.closeInventory();
        }
        else if (slot == 3) {
            player.openInventory(mods.getInventory());
        }
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public eEventLocation getTeleport() {
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

    public List<Location> getCheckpoints() {
        return checkpoints;
    }

    public List<String> getBanned() {
        return banned;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return modification;
    }
}
