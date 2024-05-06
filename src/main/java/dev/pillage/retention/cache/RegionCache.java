package dev.pillage.retention.cache;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.google.common.collect.Maps;
import dev.pillage.retention.Retention;
import dev.pillage.retention.cache.entities.RPlayer;
import dev.pillage.retention.cache.entities.Region;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;

import java.util.*;

public class RegionCache {

    static Map<Integer, Region> regionCache = Maps.newHashMap();
    static Map<Player, Region> playersInRegions = Maps.newHashMap();

    @SneakyThrows
    public static void addRegion(Region region) {
        regionCache.put(region.getId(), region);
        DB.executeUpdate("INSERT OR REPLACE INTO regions (id, data) VALUES (?, ?)", region.getId(), region.toString());
        updateRegionAutocomplete();
    }

    public static List<Region> getAllRegions() {
        return new ArrayList<>(regionCache.values());
    }

    @SneakyThrows
    public static void removeRegion(String name) {
        for (Region r : getAllRegions()) {
            if (r.getName().equals(name)) {
                regionCache.remove(r.getId());
                DB.executeUpdate("DELETE FROM regions WHERE id = ?", r.getId());
                updateRegionAutocomplete();
            }
        }
    }

    public static Region getRegionFromId(int id) {
        return regionCache.get(id);
    }

    public static void updateRegion(int id, Region region) {
        regionCache.put(id, region);
        updateRegionAutocomplete();
    }

    public static boolean isRegion(String name) {
        return regionCache.values().stream().anyMatch(region -> region.getName().equals(name));
    }

    public static void updateRegionAutocomplete() {
        List<Integer> regionIds = new ArrayList<>(regionCache.keySet());
        List<String> regionNames = new ArrayList<>();
        for (int id : regionIds) {
            regionNames.add(regionCache.get(id).getName());
        }

        Retention.getInstance().getManager().getCommandCompletions().registerAsyncCompletion("regions", c -> regionNames);
    }

    public static void startUpdateTask() {
        Timer timer = new Timer();
        TimerTask updateTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                updateRegionCache();
            }
        };
        timer.schedule(updateTask, 0, 5000 * 60 * 60);
    }

    @SneakyThrows
    public static void loadRegions() {
        List<DbRow> results = DB.getResults("SELECT * FROM regions");

        for (DbRow row : results) {
            Region region = Region.fromString(row.getString("data"));
            regionCache.put(region.getId(), region);
        }

        updateRegionAutocomplete();

        Retention.getInstance().getLogger().info("Loaded and cached " + regionCache.size() + " regions from the database");
    }

    @SneakyThrows
    public static void updateRegionCache() {
        for (Region region : getAllRegions()) {
            DB.executeUpdate("INSERT OR REPLACE INTO regions (id, data) VALUES (?, ?)", region.getId(), region.toString());
        }
    }

    // Player In Region

    public static void addNewPlayer(Player p, Region r) {
        playersInRegions.put(p, r);
    }

    public static boolean playerInRegion(Player p) {
        return playersInRegions.containsKey(p);
    }

    public static Region getRegionPlayer(Player p) {
        return playersInRegions.get(p);
    }
}
