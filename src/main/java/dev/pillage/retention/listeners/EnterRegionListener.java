package dev.pillage.retention.listeners;

import com.google.common.collect.Maps;
import dev.pillage.retention.Retention;
import dev.pillage.retention.actions.Action;
import dev.pillage.retention.actions.ExecutionTime;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.listeners.custom.EnterRegionEvent;
import dev.pillage.retention.rewards.RegionReward;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnterRegionListener implements Listener {

    public static final Map<Player, List<BukkitRunnable>> tasks = Maps.newHashMap();

    @EventHandler
    public void onRegionEnter(EnterRegionEvent e) {
        Player player = e.getPlayer();

        if (EnterRegionListener.tasks.containsKey(player)) {
            for (BukkitRunnable task : tasks.get(player)) {
                task.cancel();
            }
            tasks.remove(player);
        }

        RegionCache.addNewPlayer(player, e.getRegion());

        for (RegionReward r : e.getRegion().getRewards()) {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    r.giveReward(player);
                }
            };
            task.runTaskTimer(Retention.getInstance(), 20L * r.getTime(), 20L * r.getTime());
            tasks.putIfAbsent(player, new ArrayList<>());
            tasks.get(player).add(task);
        }

        if (e.getRegion().getActions() == null) {
            return;
        }
        for (Action a : e.getRegion().getActions()) {
            if (a.getExecutionTime() == ExecutionTime.EXIT) continue;
            a.execute(player);
        }
    }
}
