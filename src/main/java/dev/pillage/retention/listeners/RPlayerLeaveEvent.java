package dev.pillage.retention.listeners;

import dev.pillage.retention.cache.PlayerCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class RPlayerLeaveEvent implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        PlayerCache.savePlayer(e.getPlayer().getUniqueId());
    }
}
