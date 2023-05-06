package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.event.EventMods;
import org.bukkit.Bukkit;

public final class RabNoFallDamageMod extends RabMod implements FallDamageMod {

    public RabNoFallDamageMod(EventMods mods) {
        super(mods);
        setEnabled(true);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public RabNoFallDamageMod(EventMods mods, boolean active) {
        super(mods);
        setEnabled(active);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

}
