package dev.pillage.retention;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.idb.DB;
import co.aikar.idb.Database;
import co.aikar.idb.DatabaseOptions;
import co.aikar.idb.PooledDatabaseOptions;
import dev.pillage.retention.cache.PlayerCache;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.listeners.RPlayerItemHeldEvent;
import dev.pillage.retention.utils.SQLInit;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class Retention extends JavaPlugin {
    private CommandManager manager;
    @Getter
    private static Retention instance;

    private boolean verbose;

    @Override
    public void onEnable() {
        instance = this;
        verbose = true;
        this.saveDefaultConfig();

        // Database setup
        DatabaseOptions options;
        if (PluginSettings.LOCAL_DB.getBoolean()) {
            File databaseFile = new File(getDataFolder(), "data.db");
            if (!databaseFile.exists()) {
                try {
                    databaseFile.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            getLogger().info(databaseFile.getAbsolutePath());
            options = DatabaseOptions.builder()
                    .sqlite(databaseFile.getAbsolutePath()).build();
        } else {
            options = DatabaseOptions.builder()
                    .mysql(PluginSettings.REMOTE_SQL_USERNAME.getString(), PluginSettings.REMOTE_SQL_PASSWORD.getString(), PluginSettings.REMOTE_SQL_DB.getString(), PluginSettings.REMOTE_SQL_HOST.getString() + ":" + PluginSettings.REMOTE_SQL_PORT.getInt()).build();
        }
        Database db = PooledDatabaseOptions.builder().options(options).createHikariDatabase();
        DB.setGlobalDatabase(db);

        SQLInit.initSql();

        // Cache Timers
        PlayerCache.startUpdateTask();
        RegionCache.startUpdateTask();
        PlayerCache.playtimeUpdateTask();

        // Register commands
        manager = new CommandManager(this);
        manager.registerCommands();

        // Register listeners
        ListenerManager listenerManager = new ListenerManager();
        listenerManager.registerListeners();

        RegionCache.updateRegionAutocomplete();
        RegionCache.loadRegions();

    }

    @Override
    public void onDisable() {
        // Save all cached regions
        RegionCache.updateRegionCache();
    }

    public BukkitCommandManager getManager() {
        return manager;
    }
}
