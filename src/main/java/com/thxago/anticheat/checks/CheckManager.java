package com.thxago.anticheat.checks;

import com.thxago.anticheat.AntiCheat;
import com.thxago.anticheat.checks.combat.*;
import com.thxago.anticheat.checks.movement.*;
import com.thxago.anticheat.checks.player.*;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CheckManager {
    
    private final AntiCheat plugin;
    private final List<Check> checks;
    
    public CheckManager(AntiCheat plugin) {
        this.plugin = plugin;
        this.checks = new ArrayList<>();
        registerChecks();
    }
    
    private void registerChecks() {
        // Combat Checks
        registerCheck(new KillAuraCheck(plugin));
        registerCheck(new ReachCheck(plugin));
        registerCheck(new AutoClickerCheck(plugin));
        registerCheck(new CriticalsCheck(plugin));
        
        // Movement Checks
        registerCheck(new SpeedCheck(plugin));
        registerCheck(new FlyCheck(plugin));
        registerCheck(new NoFallCheck(plugin));
        registerCheck(new JesusCheck(plugin));
        registerCheck(new StepCheck(plugin));
        
        // Player Checks
        registerCheck(new FastEatCheck(plugin));
        registerCheck(new FastPlaceCheck(plugin));
        registerCheck(new FastBreakCheck(plugin));
        registerCheck(new InventoryMoveCheck(plugin));
    }
    
    private void registerCheck(Check check) {
        checks.add(check);
        plugin.getServer().getPluginManager().registerEvents(check, plugin);
    }
    
    public void flag(Player player, String checkName, int violationLevel) {
        plugin.getDatabaseManager().logViolation(
            player.getName(),
            player.getUniqueId().toString(),
            checkName,
            violationLevel
        );
    }
} 