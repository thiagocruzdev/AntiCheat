package com.thxago.anticheat.checks.combat;

import com.thxago.anticheat.checks.Check;
import com.thxago.anticheat.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class AutoClickerCheck extends Check {
    
    private static final int MAX_SAMPLES = 20; // Número de amostras para análise
    private static final double MAX_CPS = 15.0; // CPS máximo permitido
    private static final double MAX_CONSISTENCY = 0.95; // Consistência máxima permitida
    
    public AutoClickerCheck(AntiCheat plugin) {
        super(plugin, "AutoClick", "Detecta padrões de cliques automatizados", 10);
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        
        Player player = (Player) event.getDamager();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        
        long currentTime = System.currentTimeMillis();
        
        // Inicializar lista de tempos de clique se necessário
        if (data.getClickTimes() == null) {
            data.setClickTimes(new ArrayList<>());
        }
        
        List<Long> clickTimes = data.getClickTimes();
        clickTimes.add(currentTime);
        
        // Manter apenas as últimas MAX_SAMPLES amostras
        while (clickTimes.size() > MAX_SAMPLES) {
            clickTimes.remove(0);
        }
        
        // Verificar apenas quando tivermos amostras suficientes
        if (clickTimes.size() >= MAX_SAMPLES) {
            // Calcular CPS (Clicks Per Second)
            double cps = calculateCPS(clickTimes);
            
            // Calcular consistência dos intervalos
            double consistency = calculateConsistency(clickTimes);
            
            // Verificar CPS
            if (cps > MAX_CPS) {
                data.setViolationLevel(data.getViolationLevel() + 2);
                if (data.getViolationLevel() > 5) {
                    flag(player, data.getViolationLevel());
                }
                return;
            }
            
            // Verificar consistência (padrão muito consistente pode indicar bot)
            if (consistency > MAX_CONSISTENCY) {
                data.setViolationLevel(data.getViolationLevel() + 1);
                if (data.getViolationLevel() > 3) {
                    flag(player, data.getViolationLevel());
                }
            } else {
                // Reduzir violações quando o padrão é natural
                if (data.getViolationLevel() > 0) {
                    data.setViolationLevel(Math.max(0, data.getViolationLevel() - 1));
                }
            }
        }
    }
    
    private double calculateCPS(List<Long> clickTimes) {
        if (clickTimes.size() < 2) return 0;
        
        long timeSpan = clickTimes.get(clickTimes.size() - 1) - clickTimes.get(0);
        if (timeSpan == 0) return 0;
        
        return (clickTimes.size() - 1) * 1000.0 / timeSpan;
    }
    
    private double calculateConsistency(List<Long> clickTimes) {
        if (clickTimes.size() < 3) return 0;
        
        List<Long> intervals = new ArrayList<>();
        for (int i = 1; i < clickTimes.size(); i++) {
            intervals.add(clickTimes.get(i) - clickTimes.get(i - 1));
        }
        
        // Calcular média dos intervalos
        double average = intervals.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);
        
        if (average == 0) return 0;
        
        // Calcular desvio padrão
        double variance = intervals.stream()
                .mapToDouble(interval -> Math.pow(interval - average, 2))
                .average()
                .orElse(0);
        
        double standardDeviation = Math.sqrt(variance);
        
        // Quanto menor o desvio padrão em relação à média, mais consistente é o padrão
        return 1 - (standardDeviation / average);
    }
} 