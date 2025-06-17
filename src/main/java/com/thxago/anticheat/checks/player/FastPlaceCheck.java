package com.thxago.anticheat.checks.player;

import com.thxago.anticheat.checks.Check;
import com.thxago.anticheat.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class FastPlaceCheck extends Check {
    
    private static final long MIN_PLACE_DELAY = 100; // 100ms entre colocações
    
    public FastPlaceCheck(AntiCheat plugin) {
        super(plugin, "FastPlace", "Detecta colocação rápida de blocos", 10);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        long currentTime = System.currentTimeMillis();
        long lastPlace = data.getLastPlace();
        
        // Verificar se o jogador está colocando blocos muito rápido
        if (currentTime - lastPlace < MIN_PLACE_DELAY) {
            data.setViolationLevel(data.getViolationLevel() + 1);
            if (data.getViolationLevel() > 3) {
                flag(player, data.getViolationLevel());
                event.setCancelled(true);
            }
        } else {
            // Reduzir violações quando o jogador está colocando blocos normalmente
            if (data.getViolationLevel() > 0) {
                data.setViolationLevel(Math.max(0, data.getViolationLevel() - 1));
            }
        }
        
        data.setLastPlace(currentTime);
    }
} 