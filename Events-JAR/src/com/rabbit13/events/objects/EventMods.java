package com.rabbit13.events.objects;

import com.rabbit13.events.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.rabbit13.events.main.Misc.getSpecifiedItem;
import static com.rabbit13.events.main.Misc.sendLM;

public final class EventMods implements InventoryHolder {
    private Inventory modsHolder;
    private boolean fallDamage;
    private boolean lavaEqualsFail;
    private boolean moreHP;
    private boolean rapidDamage;

    public EventMods() {
        fallDamage = true;
        lavaEqualsFail = true;
        moreHP = false;
        rapidDamage = false;

        initializeItems();
    }

    public EventMods(ConfigurationSection section) {
        fallDamage = section.getBoolean("fall-damage");
        lavaEqualsFail = section.getBoolean("lava-equals-fail");
        moreHP = section.getBoolean("more-hp");
        rapidDamage = section.getBoolean("rapid-dmg");

        initializeItems();
    }

    private void initializeItems() {
        modsHolder = Bukkit.createInventory(this, 18, "Mods");
        modsHolder.addItem(
                getSpecifiedItem(Material.WHITE_WOOL, 1, "Fall Damage Mod"
                        , "&fIf Enabled, Fall Damage is not present at this event"
                        , "&fFall damage Enabled: " + (fallDamage ? "&a" + true : "&c" + false)
                ),
                getSpecifiedItem(Material.ORANGE_WOOL, 1, "Lava Equals Fail Mod"
                        , "If Enabled, Lava automatically kick players out of event"
                        , "&fLava Equals Fail Enabled: " + (lavaEqualsFail ? "&a" + true : "&c" + false)
                ),
                getSpecifiedItem(Material.YELLOW_WOOL, 1, "More HP Mod"
                        , "&fGive Players on event little bit more hp than general maximum "
                        , "&fMore HP Mod Enabled: " + (lavaEqualsFail ? "&a" + true : "&c" + false)
                ),
                getSpecifiedItem(Material.RED_WOOL, 1, "Rapid Damage Mod"
                        , "&fNo delay when hitting enemy"
                        , "&fRapid Damage Mod Enabled: " + (lavaEqualsFail ? "&a" + true : "&c" + false)
                )
        );
    }

    /**
     * Updates values in mods inventory. All of them are boolean values.
     *
     * @param slot   slot that being clicked and updated
     * @param player player who updated value
     */
    public void updateItems(int slot, Player player) {
        switch (slot) {
            case 0:
                setFallDamage(!fallDamage);
                modsHolder.setItem(slot, getSpecifiedItem(Material.WHITE_WOOL, 1, "Fall Damage Mod"
                        , "&fIf Enabled, Fall Damage is not present at this event"
                        , "&fFall damage Enabled: " + (fallDamage ? "&a" + true : "&c" + false))
                );

                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                                .replace("%key%", "Fall_Damage")
                                .replace("%value%", Boolean.toString(fallDamage))
                        , true
                        , player);
                break;
            case 1:
                setLavaEqualsFail(!lavaEqualsFail);
                modsHolder.setItem(slot, getSpecifiedItem(Material.ORANGE_WOOL, 1, "Lava Equals Fail Mod"
                        , "If Enabled, Lava automatically kick players out of event"
                        , "&fLava Equals Fail Enabled: " + (lavaEqualsFail ? "&a" + true : "&c" + false))
                );

                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                                .replace("%key%", "Lava_Equals_Fail")
                                .replace("%value%", Boolean.toString(lavaEqualsFail))
                        , true
                        , player);
                break;
            case 2:
                setMoreHP(!moreHP);
                modsHolder.setItem(slot, getSpecifiedItem(Material.YELLOW_WOOL, 1, "More HP Mod"
                        , "&fGive Players on event little bit more hp than general maximum "
                        , "&fMore HP Mod Enabled: " + (moreHP ? "&a" + true : "&c" + false))
                );

                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                                .replace("%key%", "More_HP")
                                .replace("%value%", Boolean.toString(moreHP))
                        , true
                        , player);
            case 3:
                setRapidDamage(!rapidDamage);
                modsHolder.setItem(slot, getSpecifiedItem(Material.RED_WOOL, 1, "Rapid Damage Mod"
                        , "&fNo delay when hitting enemy"
                        , "&fRapid Damage Mod Enabled: " + (rapidDamage ? "&a" + true : "&c" + false))
                );

                sendLM(Main.getPrefix() + " " + Objects.requireNonNull(Main.getFilMan().getWords().getString("event-modification-finished"))
                                .replace("%key%", "Rapid_Damage")
                                .replace("%value%", Boolean.toString(rapidDamage))
                        , true
                        , player);
        }
    }
    @Override
    public @NotNull Inventory getInventory() {
        return modsHolder;
    }

    //Fall Damage Mod
    public boolean getFallDamage() {
        return fallDamage;
    }

    public void setFallDamage(boolean fallDamage) {
        this.fallDamage = fallDamage;
    }

    //Lava Equals Fail Mod
    public boolean getLavaEqualsFail() {
        return lavaEqualsFail;
    }

    public void setLavaEqualsFail(boolean lavaEqualsFail) {
        this.lavaEqualsFail = lavaEqualsFail;
    }

    //More HP Mod
    public boolean getMoreHP() {
        return moreHP;
    }

    public void setMoreHP(boolean moreHP) {
        this.moreHP = moreHP;
    }

    //Rapid Damage Mod
    public boolean getRapidDamage() {
        return rapidDamage;
    }

    public void setRapidDamage(boolean rapidDamage) {
        this.rapidDamage = rapidDamage;
    }

}
