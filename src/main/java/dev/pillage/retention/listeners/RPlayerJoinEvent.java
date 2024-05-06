package dev.pillage.retention.listeners;

import dev.pillage.retention.cache.PlayerCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class RPlayerJoinEvent implements Listener {

    @EventHandler
    public void onPlayerJoin(AsyncPlayerPreLoginEvent e) {
        PlayerCache.loadPlayer(e.getUniqueId());
    }
}
