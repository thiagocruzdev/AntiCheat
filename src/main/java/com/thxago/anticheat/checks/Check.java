package com.thxago.anticheat.checks;

import com.thxago.anticheat.AntiCheat;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

@Getter
public abstract class Check implements Listener {
    
    protected final AntiCheat plugin;
    protected final String name;
    protected final String description;
    protected final int maxViolations;
    
    public Check(AntiCheat plugin, String name, String description, int maxViolations) {
        this.plugin = plugin;
        this.name = name;
        this.description = description;
        this.maxViolations = maxViolations;
    }
    
    protected void flag(Player player, int violationLevel) {
        plugin.getCheckManager().flag(player, name, violationLevel);
    }
    
    protected void flag(Player player) {
        flag(player, 1);
    }
} 