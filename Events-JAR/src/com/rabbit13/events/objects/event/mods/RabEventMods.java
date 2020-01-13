package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.managers.PlayerManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;

import static com.rabbit13.events.main.Main.getFilMan;
import static com.rabbit13.events.main.Main.getPrefix;
import static com.rabbit13.events.main.Misc.getSpecifiedItem;
import static com.rabbit13.events.main.Misc.sendLM;

public final class RabEventMods implements InventoryHolder, EventMods {
    private Inventory modsHolder;
    @Getter @Setter
    private int modificator;

    @Getter @Setter private boolean fallDamageEnabled;
    @Getter @Setter private boolean lavaEqualsFailEnabled;
    @Getter @Setter private boolean activeCheckpointsEnabled;
    @Getter @Setter private boolean moreHPEnabled;
    @Getter @Setter private int moreHP;
    @Getter @Setter private boolean startingItemsEnabled;
    @Getter private StartingItems startingItems;
    @Getter @Setter private boolean effectSettingsEnabled;
    @Getter private EffectSettings effectSettings;

    public RabEventMods() {
        fallDamageEnabled = true;
        lavaEqualsFailEnabled = true;
        activeCheckpointsEnabled = true;
        moreHPEnabled = false;
        moreHP = 20;
        startingItemsEnabled = true;
        effectSettingsEnabled = true;
        this.startingItems = new RabStartingItems(this);
        this.effectSettings = new RabEffectSettings(this);
        initializeItems();
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    public RabEventMods(ConfigurationSection section) {
        this.fallDamageEnabled = section.getBoolean("fall-damage", false);
        this.lavaEqualsFailEnabled = section.getBoolean("lava-equals-fail", true);
        this.activeCheckpointsEnabled = section.getBoolean("active-checkpoints", true);
        this.moreHPEnabled = section.getBoolean("more-hp", false);
        this.moreHP = section.getInt("more-hp-value", 20);
        this.startingItemsEnabled = section.getBoolean("starting-items", true);
        this.effectSettingsEnabled = section.getBoolean("effect-settings", true);
        //<editor-fold desc="Starting Items">
        ItemStack[] startingItems = null;
        List<?> startingList = section.getList("starting-items-value");
        if (startingList != null) {
            startingItems = startingList.toArray(new ItemStack[0]);
        }

        this.startingItems = new RabStartingItems(this);
        if (startingItems != null) {
            for (ItemStack item : startingItems) {
                if (item != null)
                    this.startingItems.getInventory().addItem(item);
            }
        }
        //</editor-fold>
        //<editor-fold desc="Effect Settings">
        ItemStack[] effectSettingsArr = null;
        List<?> potionList = section.getList("effect-settings-value");
        if (potionList != null) {
            effectSettingsArr = potionList.toArray(new ItemStack[0]);
        }
        this.effectSettings = new RabEffectSettings(this);
        if (effectSettingsArr != null) {
            for (ItemStack item : effectSettingsArr) {
                if (item != null)
                    this.effectSettings.getInventory().addItem(item);
            }
        }
        //</editor-fold>
        initializeItems();
    }

    private void initializeItems() {
        modsHolder = Bukkit.createInventory(this, 18, "Mods");
        modsHolder.setItem(0, getSpecifiedItem(Material.FEATHER, 1, "&fNo-Fall Damage",
                                               "If Enabled, Fall Damage is not present at this event",
                                               "&fEnabled: " + (fallDamageEnabled ? "&a" + true : "&c" + false)
        ));
        modsHolder.setItem(1, getSpecifiedItem(Material.LAVA_BUCKET, 1, "&eLava Equals Fail",
                                               "If Enabled, Lava automatically kick players out of event",
                                               "&fEnabled: " + (lavaEqualsFailEnabled ? "&a" + true : "&c" + false)
        ));
        modsHolder.setItem(2, getSpecifiedItem(Material.EXPERIENCE_BOTTLE, 1, "&aCheckpoints Activated",
                                               "If activated, checkpoints will work",
                                               "&fEnabled: " + (activeCheckpointsEnabled ? "&a" + true : "&c" + false)
        ));
        modsHolder.setItem(3, getSpecifiedItem(Material.GOLDEN_APPLE, 1, "&6More HP Mod",
                                               "Set player hp to given value. &6Minimum is 1",
                                               "&ePress Shift+Click to set value.",
                                               "&fValue: &a" + moreHP,
                                               "&fEnabled: " + (moreHPEnabled ? "&a" + true : "&c" + false))
        );
        modsHolder.setItem(8, getSpecifiedItem(Material.CHEST_MINECART, 1, "&bStarting Items",
                                               "If active, player will get starting items",
                                               "that are in this event's \"Starting items\" inventory.",
                                               "&ePress Shift+Click to open inventory.",
                                               "&fEnabled: " + (startingItemsEnabled ? "&a" + true : "&c" + false)
        ));
        modsHolder.setItem(17, getSpecifiedItem(Material.POTION, 1, "Effect Settings",
                                                "If active, player will get infinite potion effects of type",
                                                "that are in this event's \"Effect Settings\" inventory.",
                                                "&ePress Shift+Click to open inventory.",
                                                "&fEnabled: " + (effectSettingsEnabled ? "&a" + true : "&c" + false)

        ));

    }

    /**
     * Updates values in mods inventory. All of them are boolean values.
     *
     * @param slot   slot that being clicked and updated
     * @param player player who updated value
     */
    public void updateItems(int slot, Player player, InventoryAction action) {
        switch (slot) {
            case 0: {
                this.setFallDamageEnabled(!fallDamageEnabled);
                modsHolder.setItem(slot, getSpecifiedItem(Material.FEATHER, 1, "&fNo-Fall Damage"
                        , "If Enabled, Fall Damage is not present at this event"
                        , "&fEnabled: " + (fallDamageEnabled ? "&a" + true : "&c" + false))
                );

                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-finished"))
                               .replace("%key%", "Fall_Damage")
                               .replace("%value%", Boolean.toString(fallDamageEnabled)),
                       true,
                       player
                );
                break;
            }
            case 1: {
                this.setLavaEqualsFailEnabled(!lavaEqualsFailEnabled);
                modsHolder.setItem(slot, getSpecifiedItem(Material.LAVA_BUCKET, 1, "&eLava Equals Fail",
                                                          "If Enabled, Lava automatically kick players out of event",
                                                          "&fEnabled: " + (lavaEqualsFailEnabled ? "&a" + true : "&c" + false))
                );

                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-finished"))
                               .replace("%key%", "Lava_Equals_Fail")
                               .replace("%value%", Boolean.toString(lavaEqualsFailEnabled)),
                       true,
                       player
                );
                break;
            }
            case 2: {
                this.setActiveCheckpointsEnabled(!activeCheckpointsEnabled);
                modsHolder.setItem(slot, getSpecifiedItem(Material.EXPERIENCE_BOTTLE, 1, "&aCheckpoints Activated",
                                                          "If activated, checkpoints will work",
                                                          "&fEnabled: " + (activeCheckpointsEnabled ? "&a" + true : "&c" + false))
                );

                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-finished"))
                               .replace("%key%", "Active Checkpoints")
                               .replace("%value%", Boolean.toString(activeCheckpointsEnabled)),
                       true,
                       player
                );
                break;
            }
            case 3: {
                if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), player::closeInventory);
                    PlayerManager.getModifyingMods().put(player, new AbstractMap.SimpleEntry<>(slot, this));
                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-init"))
                                   .replace("%value%", "moreHP"),
                           true,
                           player
                    );
                }
                else {
                    this.setMoreHPEnabled(!moreHPEnabled);
                    modsHolder.setItem(slot, getSpecifiedItem(Material.GOLDEN_APPLE, 1, "&6More HP Mod",
                                                              "Set player hp to given value. &6Minimum is 1",
                                                              "&ePress Shift+Click to set value.",
                                                              "&fValue: &a" + moreHP,
                                                              "&fEnabled: " + (moreHPEnabled ? "&a" + true : "&c" + false))
                    );

                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-finished"))
                                   .replace("%key%", "More_HP")
                                   .replace("%value%", Boolean.toString(moreHPEnabled)),
                           true,
                           player
                    );
                }
                break;
            }
            case 8: {
                if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
                    player.openInventory(startingItems.getInventory());
                }
                else {
                    this.setStartingItemsEnabled(!startingItemsEnabled);
                    modsHolder.setItem(slot, getSpecifiedItem(Material.CHEST_MINECART, 1, "&bStarting Items ",
                                                              "If active, player will get starting items that are in",
                                                              "this event's \"Starting items\" inventory.",
                                                              "&ePress Shift+Click to open inventory.",
                                                              "&fEnabled: " + (startingItemsEnabled ? "&a" + true : "&c" + false))
                    );

                    sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-finished"))
                                   .replace("%key%", "Rapid_Damage")
                                   .replace("%value%", Boolean.toString(startingItemsEnabled)),
                           true,
                           player
                    );
                }
                break;
            }
            case 17: {
                if ((action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))) {
                    player.openInventory(effectSettings.getInventory());
                }
                else {
                    this.setEffectSettingsEnabled(!effectSettingsEnabled);
                    modsHolder.setItem(slot, getSpecifiedItem(Material.POTION, 1, "Effect Settings",
                                                              "If active, player will get infinite potion effects of type",
                                                              "that are in this event's \"Effect Settings\" inventory.",
                                                              "&ePress Shift+Click to open inventory.",
                                                              "&fEnabled: " + (effectSettingsEnabled ? "&a" + true : "&c" + false)));
                }
                break;
            }
        }
    }

    public void updateItemsWithAction(int slot, Player player, String text) {
        if (slot == 3) {
            try {
                moreHP = Integer.parseInt(text);
                if (moreHP < 1) {
                    moreHP = 1;
                }
                sendLM(getPrefix() + " " + Objects.requireNonNull(getFilMan().getWords().getString("event-modification-finished"))
                               .replace("%key%", "moreHP")
                               .replace("%value%", text),
                       true,
                       player);
                modsHolder.setItem(slot, getSpecifiedItem(Material.GOLDEN_APPLE, 1, "&6More HP Mod",
                                                          "Set player hp to given value. &6Minimum is 1",
                                                          "&ePress Shift+Click to set value.",
                                                          "&fValue: &a" + moreHP,
                                                          "&fEnabled: " + (moreHPEnabled ? "&a" + true : "&c" + false))
                );
            } catch (NumberFormatException e) {
                sendLM(getPrefix() + " " + getFilMan().getWords().getString("event-modification-number-error"), true, player);
            }
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return modsHolder;
    }
}
