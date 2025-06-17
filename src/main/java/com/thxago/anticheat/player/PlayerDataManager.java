package com.thxago.anticheat.player;

import com.thxago.anticheat.AntiCheat;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class PlayerDataManager {
    
    private final AntiCheat plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    
    public PlayerDataManager(AntiCheat plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
    }
    
    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), uuid -> new PlayerData());
    }
    
    public long getLastAttack(Player player) {
        return getPlayerData(player).getLastAttack();
    }
    
    public void setLastAttack(Player player, long time) {
        getPlayerData(player).setLastAttack(time);
    }
    
    public void removePlayerData(Player player) {
        playerDataMap.remove(player.getUniqueId());
    }
} 