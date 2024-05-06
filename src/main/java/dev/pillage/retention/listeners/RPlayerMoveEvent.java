package dev.pillage.retention.listeners;

import dev.pillage.retention.cache.PlayerCache;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.RPlayer;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.listeners.custom.EnterRegionEvent;
import dev.pillage.retention.listeners.custom.LeaveRegionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class RPlayerMoveEvent implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (e.getTo().distanceSquared(e.getFrom()) == 0) return; // Checks if they move a full block

        for (Region region : RegionCache.getAllRegions()) {
            Location newLocation = e.getTo();
            Location oldLocation = e.getFrom();

            boolean isNewLocationInside = region.getCuboid().contains(newLocation);
            boolean isOldLocationInside = region.getCuboid().contains(oldLocation);

            if (isNewLocationInside && !isOldLocationInside) {
                Bukkit.getPluginManager().callEvent(new EnterRegionEvent(player, region));
            } else if (!isNewLocationInside && isOldLocationInside) {
                Bukkit.getPluginManager().callEvent(new LeaveRegionEvent(player, region));
            }
        }
    }
}
