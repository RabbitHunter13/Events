package com.rabbit13.events.objects.event;

import com.rabbit13.events.objects.event.mods.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public interface EventMods extends InventoryHolder {


    /**
     * Updates values in mods inventory. All of them are boolean values.
     *
     * @param slot   slot that being clicked and updated
     * @param player player who updated value
     */
    void openMods(int slot, Player player);

    String getOwner();

    Inventory getModsHolder();

    CheckpointsMod getCheckpointsMod();

    EffectsMod getEffectsMod();

    FallDamageMod getFallDamageMod();

    LavaEqualFailMod getLavaEqualFailMod();

    MoreHPMod getMoreHPMod();

    RewardItemsMod getRewardItemsMod();

    StartingItemsMod getStartingItemsMod();

    NoSwimMod getNoSwimMod();
}
