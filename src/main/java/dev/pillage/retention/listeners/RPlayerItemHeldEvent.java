package dev.pillage.retention.listeners;

import dev.pillage.retention.PluginSettings;
import dev.pillage.retention.Retention;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.utils.Cuboid;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class RPlayerItemHeldEvent implements Listener {

    private final Map<Player, BukkitRunnable> runnables = new HashMap<>();

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent e) {
        ItemStack item = e.getPlayer().getInventory().getItem(e.getNewSlot());
        Player player = e.getPlayer();
//        if (!(player.hasPermission("retention.visualizeRegions"))) {
//            stopSpawning();
//            return;
//        }
        if (item == null || !itemNameEqual(item) || !itemMaterialEqual(item)) {
            if (runnables.get(e.getPlayer()) != null) {
                runnables.get(e.getPlayer()).cancel();
            }
            return;
        }

        if (runnables.get(e.getPlayer()) != null) {
            runnables.get(e.getPlayer()).cancel();
        }

        runnables.put(e.getPlayer(),  new BukkitRunnable() {
            @Override
            public void run() {
            for (Region region : RegionCache.getAllRegions()) {
                Cuboid cuboid = region.getCuboid();
                for (Location coord : cuboid.getTopBottomFacePerimeter(0.1)) {
                    player.spawnParticle(Particle.REDSTONE, coord, 0, 0.001, 1, 0, 1, new Particle.DustOptions(Color.RED, 1));
                }
                for (Location coord : cuboid.getVerticalEdgeLocations(0.1)) {
                    player.spawnParticle(Particle.REDSTONE, coord, 0, 0.001, 1, 0, 1, new Particle.DustOptions(Color.RED, 1));
                }
            }
            }
        });
        runnables.get(e.getPlayer()).runTaskTimer(Retention.getInstance(), 0L, 10L);
    }

    private boolean itemNameEqual(ItemStack item) {
        return item.getItemMeta().getDisplayName().equals(PluginSettings.WAND_NAME.getString().replace("&", "§"));
    }

    private boolean itemMaterialEqual(ItemStack item) {
        return item.getType() == Material.valueOf(PluginSettings.WAND_ITEM.getString().toUpperCase());
    }
}
