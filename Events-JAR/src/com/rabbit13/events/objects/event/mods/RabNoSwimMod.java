package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.event.EventMods;
import org.bukkit.Bukkit;

public class RabNoSwimMod extends RabMod implements NoSwimMod {

    public RabNoSwimMod(EventMods mods) {
        super(mods);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public RabNoSwimMod(EventMods mods, boolean enabled) {
        super(mods);
        setEnabled(enabled);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }
}
