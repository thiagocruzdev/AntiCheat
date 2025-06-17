package com.thxago.anticheat.checks.movement;

import com.thxago.anticheat.checks.Check;
import com.thxago.anticheat.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpeedCheck extends Check {
    
    private static final double MAX_SPEED = 0.36; // Velocidade máxima permitida
    private static final double MAX_SPEED_SPRINT = 0.36; // Velocidade máxima com sprint
    private static final double MAX_SPEED_SPRINT_JUMP = 0.42; // Velocidade máxima com sprint e pulo
    
    public SpeedCheck(AntiCheat plugin) {
        super(plugin, "Speed", "Detecta movimentação mais rápida que o normal", 15);
    }
    
    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        // Ignorar teleportes e mudanças de mundo
        if (event.getFrom().getWorld() != event.getTo().getWorld()) return;
        
        // Calcular distância percorrida
        double xDiff = event.getTo().getX() - event.getFrom().getX();
        double zDiff = event.getTo().getZ() - event.getFrom().getZ();
        double distance = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
        
        // Ignorar movimentos muito pequenos
        if (distance < 0.001) return;
        
        // Calcular velocidade máxima permitida
        double maxSpeed = MAX_SPEED;
        if (player.isSprinting()) {
            maxSpeed = MAX_SPEED_SPRINT;
            if (!player.isOnGround()) {
                maxSpeed = MAX_SPEED_SPRINT_JUMP;
            }
        }
        
        // Aplicar multiplicadores baseados no estado do jogador
        if (player.isFlying() || player.isInsideVehicle()) return;
        
        // Verificar velocidade
        if (distance > maxSpeed) {
            data.setViolationLevel(data.getViolationLevel() + 1);
            if (data.getViolationLevel() > 5) {
                flag(player, data.getViolationLevel());
            }
        } else {
            // Reduzir violações quando o jogador está se movendo normalmente
            if (data.getViolationLevel() > 0) {
                data.setViolationLevel(Math.max(0, data.getViolationLevel() - 1));
            }
        }
    }
} 