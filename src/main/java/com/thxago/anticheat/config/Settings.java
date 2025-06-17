package com.thxago.anticheat.config;

import com.thxago.anticheat.AntiCheat;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@Getter
public class Settings {
    
    private final AntiCheat plugin;
    private FileConfiguration config;
    private File configFile;
    
    // Configurações do banco de dados
    private String dbHost;
    private int dbPort;
    private String dbName;
    private String dbUser;
    private String dbPassword;
    
    // Configurações gerais
    private boolean debugMode;
    private int maxViolations;
    private boolean autoBan;
    
    public Settings(AntiCheat plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    private void loadConfig() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        
        configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                createDefaultConfig();
            } catch (IOException e) {
                plugin.getLogger().severe("Erro ao criar arquivo de configuração: " + e.getMessage());
            }
        }
        
        config = YamlConfiguration.loadConfiguration(configFile);
        loadValues();
    }
    
    private void createDefaultConfig() {
        config.set("database.host", "localhost");
        config.set("database.port", 3306);
        config.set("database.name", "anticheat");
        config.set("database.user", "root");
        config.set("database.password", "password");
        
        config.set("general.debug", false);
        config.set("general.max-violations", 10);
        config.set("general.auto-ban", true);
        
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar configuração padrão: " + e.getMessage());
        }
    }
    
    private void loadValues() {
        dbHost = config.getString("database.host");
        dbPort = config.getInt("database.port");
        dbName = config.getString("database.name");
        dbUser = config.getString("database.user");
        dbPassword = config.getString("database.password");
        
        debugMode = config.getBoolean("general.debug");
        maxViolations = config.getInt("general.max-violations");
        autoBan = config.getBoolean("general.auto-ban");
    }
    
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Erro ao salvar configuração: " + e.getMessage());
        }
    }
} 