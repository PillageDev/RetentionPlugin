package dev.pillage.retention.listeners.custom;

import dev.pillage.retention.cache.entities.Region;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class EnterRegionEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Region region;

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public EnterRegionEvent(Player player, Region region) {
        this.player = player;
        this.region = region;
    }
}
