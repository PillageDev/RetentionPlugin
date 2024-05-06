package dev.pillage.retention.cache;

import co.aikar.idb.DB;
import com.google.common.collect.Maps;
import dev.pillage.retention.Retention;
import dev.pillage.retention.cache.entities.RPlayer;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PlayerCache {

    static Map<UUID, RPlayer> cache = Maps.newHashMap();

    public static RPlayer get(UUID uuid) {
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        }

        return null;
    }

    public static void updatePlayer(UUID uuid, RPlayer player) {
        cache.put(uuid, player);
    }

    public static void startUpdateTask() {
        Timer timer = new Timer();
        TimerTask updateTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                for (RPlayer player : cache.values()) {
                    DB.executeUpdate("INSERT OR REPLACE INTO players (data) VALUES (?)", player.toString());
                }
            }
        };
        timer.schedule(updateTask, 0, 5000 * 60 * 60);
    }

    public static void playtimeUpdateTask() {
        BukkitRunnable updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    RPlayer rPlayer = get(player.getUniqueId());
                    assert rPlayer != null;
                    rPlayer.setPlaytime(rPlayer.getPlaytime() + 60);
                }
            }
        };
        updateTask.runTaskTimerAsynchronously(Retention.getInstance(), 0L, 1200L);
    }

    @SneakyThrows
    public static void loadPlayer(UUID uuid) {
        AtomicReference<RPlayer> rPlayer = new AtomicReference<>();
        DB.getResults("SELECT * FROM players WHERE uuid = ?", uuid).forEach(result -> {
            rPlayer.set(RPlayer.fromString(result.getString("data")));
        });

        if (rPlayer.get() == null) {
            rPlayer.set(new RPlayer());
            rPlayer.get().setUuid(uuid);
            DB.executeUpdate("INSERT INTO players (uuid, data) VALUES (?, ?)", uuid, rPlayer.get().toString());
        }

        cache.put(uuid, rPlayer.get());
    }

    @SneakyThrows
    public static void savePlayer(UUID uuid) {
        RPlayer rPlayer = cache.get(uuid);
        DB.executeUpdate("REPLACE INTO players (uuid, data) VALUES (?, ?)", uuid, rPlayer.toString());
        cache.remove(uuid);
    }

    public static List<RPlayer> getOnlinePlayers() {
        return new ArrayList<>(cache.values());
    }

    @SneakyThrows
    public static List<RPlayer> getAllPlayers() {
        List<RPlayer> players = new ArrayList<>();
        DB.getResults("SELECT * FROM players").forEach(result -> {
            players.add(RPlayer.fromString(result.get("data")));
        });
        return players;
    }

    @SneakyThrows
    public static RPlayer getPlayerBothOnline(UUID uuid) {
        AtomicReference<RPlayer> player = new AtomicReference<>();
        if (cache.containsKey(uuid)) {
            return cache.get(uuid);
        } else {
            DB.getResults("SELECT data FROM players WHERE id = ?", uuid).forEach(result -> {
                player.set(RPlayer.fromString(result.get("data")));
            });
            return player.get();
        }
    }

}
