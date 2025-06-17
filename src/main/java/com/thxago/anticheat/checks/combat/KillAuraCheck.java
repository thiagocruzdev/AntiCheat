package com.thxago.anticheat.checks.combat;

import com.thxago.anticheat.checks.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class KillAuraCheck extends Check {
    
    public KillAuraCheck(AntiCheat plugin) {
        super(plugin, "KillAura", "Detecta ataques impossíveis e ângulos inválidos", 10);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        
        Player player = (Player) event.getDamager();
        
        // Verificar ângulo de ataque
        Vector playerDirection = player.getLocation().getDirection();
        Vector toEntity = event.getEntity().getLocation().toVector().subtract(player.getLocation().toVector());
        
        double angle = playerDirection.angle(toEntity);
        if (angle > Math.PI / 2) { // Ângulo maior que 90 graus
            flag(player, 2);
            return;
        }
        
        // Verificar velocidade de ataque
        long currentTime = System.currentTimeMillis();
        long lastAttack = plugin.getPlayerDataManager().getLastAttack(player);
        
        if (currentTime - lastAttack < 50) { // Ataques muito rápidos
            flag(player, 3);
        }
        
        plugin.getPlayerDataManager().setLastAttack(player, currentTime);
    }
} 