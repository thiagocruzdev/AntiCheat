package com.thxago.anticheat.database;

import com.thxago.anticheat.AntiCheat;
import com.thxago.anticheat.config.Settings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Getter
public class DatabaseManager {
    
    private final AntiCheat plugin;
    private HikariDataSource dataSource;
    
    public DatabaseManager(AntiCheat plugin) {
        this.plugin = plugin;
        setupDatabase();
        createTables();
    }
    
    private void setupDatabase() {
        Settings settings = plugin.getSettings();
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s",
                settings.getDbHost(),
                settings.getDbPort(),
                settings.getDbName()));
        config.setUsername(settings.getDbUser());
        config.setPassword(settings.getDbPassword());
        
        // Configurações do pool
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(10000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(30000);
        
        // Configurações específicas do MySQL
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        this.dataSource = new HikariDataSource(config);
    }
    
    private void createTables() {
        try (Connection conn = dataSource.getConnection()) {
            String createViolationsTable = "CREATE TABLE IF NOT EXISTS violations (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_name VARCHAR(16) NOT NULL," +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "check_name VARCHAR(50) NOT NULL," +
                    "violation_level INT NOT NULL," +
                    "server_name VARCHAR(50) NOT NULL," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            
            conn.createStatement().execute(createViolationsTable);
        } catch (SQLException e) {
            plugin.getLogger().severe("Erro ao criar tabelas: " + e.getMessage());
        }
    }
    
    public CompletableFuture<Void> logViolation(String playerName, String playerUUID, String checkName, int violationLevel) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                String sql = "INSERT INTO violations (player_name, player_uuid, check_name, violation_level, server_name) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, playerName);
                    stmt.setString(2, playerUUID);
                    stmt.setString(3, checkName);
                    stmt.setInt(4, violationLevel);
                    stmt.setString(5, Bukkit.getServer().getName());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Erro ao registrar violação: " + e.getMessage());
            }
        });
    }
    
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
} 