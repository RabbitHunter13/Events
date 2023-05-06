package com.rabbit13.events.objects.event.mods;

import com.rabbit13.events.main.Main;
import com.rabbit13.events.objects.event.EventMods;
import org.bukkit.Bukkit;

public final class RabCheckpointsMod extends RabMod implements CheckpointsMod {

    public RabCheckpointsMod(EventMods mods) {
        super(mods);
        setEnabled(true);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

    public RabCheckpointsMod(EventMods mods, boolean active) {
        super(mods);
        setEnabled(active);
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
    }

}
