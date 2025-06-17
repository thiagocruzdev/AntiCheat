package com.thxago.anticheat;

import com.thxago.anticheat.database.DatabaseManager;
import com.thxago.anticheat.checks.CheckManager;
import com.thxago.anticheat.config.Settings;
import com.thxago.anticheat.player.PlayerDataManager;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class AntiCheat extends JavaPlugin implements Listener {
    
    private static AntiCheat instance;
    private DatabaseManager databaseManager;
    private CheckManager checkManager;
    private Settings settings;
    private PlayerDataManager playerDataManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Inicializar configurações
        this.settings = new Settings(this);
        
        // Inicializar banco de dados
        this.databaseManager = new DatabaseManager(this);
        
        // Inicializar gerenciador de dados do jogador
        this.playerDataManager = new PlayerDataManager(this);
        
        // Inicializar sistema de checagens
        this.checkManager = new CheckManager(this);
        
        // Registrar eventos
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("AntiCheat ativado com sucesso!");
    }
    
    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("AntiCheat desativado!");
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerDataManager.getPlayerData(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDataManager.removePlayerData(event.getPlayer());
    }
    
    public static AntiCheat getInstance() {
        return instance;
    }
} 