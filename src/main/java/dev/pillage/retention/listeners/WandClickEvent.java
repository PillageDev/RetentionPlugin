package dev.pillage.retention.listeners;

import dev.pillage.retention.PluginSettings;
import dev.pillage.retention.Retention;
import dev.pillage.retention.utils.Cuboid;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class WandClickEvent implements Listener {

    @Getter
    public static final WandClickEvent instance = new WandClickEvent();

    public static final HashMap<Player, String> playerSelectionNames = new HashMap<>();
    public static final HashMap<Player, PartialRegionSelection> playerSelections = new HashMap<>();
    private static final HashMap<Player, BukkitRunnable> playerVisualizers = new HashMap<>();

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        ItemStack item = e.getItem();
        Player player = e.getPlayer();
//        if (!(player.hasPermission("retention.visualizeRegions"))) {
//            stopSpawning();
//            return;
//        }
        if (item == null || !itemNameEqual(item) || !itemMaterialEqual(item)) {
            return;
        }

        e.setCancelled(true);

        if (!playerSelectionNames.containsKey(e.getPlayer())) {
            if (e.getClickedBlock() == null) {
                return;
            }
            e.getPlayer().sendMessage("You must run /afk regions setup <name> to select an afk region");
            return;
        }

        // Region selection
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Location loc1 = e.getClickedBlock().getLocation();
            loc1 = loc1.clone().add(0, 1, 0);

            if (playerSelections.get(player) == null) {
                playerSelections.put(player, new PartialRegionSelection(loc1, null));
            } else {
                playerSelections.get(player).loc1 = loc1;
            }

            e.getPlayer().sendMessage("First location set to " + loc1.getX() + ", " + loc1.getY() + ", " + loc1.getZ());
        } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Location loc2 = e.getClickedBlock().getLocation();
            loc2 = loc2.clone().add(0, 1, 0);

            if (playerSelections.get(player) == null) {
                playerSelections.put(player, new PartialRegionSelection(null, loc2));
            } else {
                playerSelections.get(player).loc2 = loc2;
            }

            e.getPlayer().sendMessage("Second location set to " + loc2.getX() + ", " + loc2.getY() + ", " + loc2.getZ());
        }

        // Show selection while creating
        if (playerSelections.containsKey(player) && playerSelections.get(player) != null && playerSelections.get(player).complete()) {
            playerVisualizers.put(e.getPlayer(), new BukkitRunnable() {
                @Override
                public void run() {
                    PartialRegionSelection selection = playerSelections.get(player);
                    if (selection == null || selection.loc1 == null || selection.loc2 == null) {
                        return;
                    }
                    Cuboid cuboid = new Cuboid(playerSelections.get(player).loc1, playerSelections.get(player).loc2);
                    for (Location coord : cuboid.getTopBottomFacePerimeter(0.1)) {
                        player.spawnParticle(Particle.REDSTONE, coord, 0, 0.001, 1, 0, 1, new Particle.DustOptions(Color.LIME, 1));
                    }
                    for (Location coord : cuboid.getVerticalEdgeLocations(0.1)) {
                        player.spawnParticle(Particle.REDSTONE, coord, 0, 0.001, 1, 0, 1, new Particle.DustOptions(Color.LIME, 1));
                    }
                }
            });
            playerVisualizers.get(player).runTaskTimer(Retention.getInstance(), 0L, 10L);
        }
    }

    private boolean itemNameEqual(ItemStack item) {
        return item.getItemMeta().getDisplayName().equals(PluginSettings.WAND_NAME.getString().replace("&", "§"));
    }

    private boolean itemMaterialEqual(ItemStack item) {
        return item.getType() == Material.valueOf(PluginSettings.WAND_ITEM.getString().toUpperCase());
    }

    public static class PartialRegionSelection {
        public Location loc1;
        public Location loc2;

        public PartialRegionSelection(Location loc1, Location loc2) {
            this.loc1 = loc1;
            this.loc2 = loc2;
        }

        public boolean complete() {
            return loc1 != null && loc2 != null;
        }
    }
}
