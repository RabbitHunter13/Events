package com.rabbit13.events.objects.event.mods;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface RewardItems extends Mod {

    int getMaxWins();

    int getWinnerIndex();

    void setWinnerIndex(int winnerIndex);

    ItemStack[] giveRewardToPlayer();

    Inventory getRewards();
}
