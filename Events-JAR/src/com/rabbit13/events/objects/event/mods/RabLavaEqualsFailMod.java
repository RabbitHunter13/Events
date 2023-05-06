package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.event.EventMods;
import org.bukkit.Bukkit;

public final class RabLavaEqualsFailMod extends RabMod implements LavaEqualFailMod {

    public RabLavaEqualsFailMod(EventMods mods) {
        super(mods);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }


    public RabLavaEqualsFailMod(EventMods mods, boolean active) {
        super(mods);
        setEnabled(active);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());

    }

}
