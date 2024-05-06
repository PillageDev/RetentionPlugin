package dev.pillage.retention.listeners;

import dev.pillage.retention.actions.Action;
import dev.pillage.retention.actions.ExecutionTime;
import dev.pillage.retention.listeners.custom.LeaveRegionEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaveRegionListener implements Listener {

    @EventHandler
    public void onRegionLeave(LeaveRegionEvent e) {
        Player player = e.getPlayer();

        if (EnterRegionListener.tasks.containsKey(player)) {
            for (BukkitRunnable task : EnterRegionListener.tasks.get(player)) {
                task.cancel();
            }
            EnterRegionListener.tasks.remove(player);
        }

        if (e.getRegion().getActions() == null) {
            return;
        }
        for (Action a : e.getRegion().getActions()) {
            if (a.getExecutionTime() == ExecutionTime.ENTER) continue;
            a.execute(player);
        }
    }
}
