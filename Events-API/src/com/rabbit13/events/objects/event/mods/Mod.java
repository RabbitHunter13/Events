package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.objects.event.EventMods;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface Mod extends InventoryHolder {
    boolean isEnabled();

    void setEnabled(boolean enabled);

    @NotNull EventMods getMods();

    void chatUpdate(int slot, String text, CommandSender sender);
}
