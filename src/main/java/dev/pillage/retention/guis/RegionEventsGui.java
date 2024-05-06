package dev.pillage.retention.guis;

import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RegionEventsGui extends RGui {

    private Player player;
    private final Gui gui;
    private Region region;

    public RegionEventsGui() {
        gui = Gui.gui()
                .title(Component.text("Region rewards"))
                .rows(3)
                .create();

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
    }

    @Override
    public void open(Player player, int regionId) {
        this.player = player;
        this.region = RegionCache.getRegionFromId(regionId);

        setupItems();
        gui.open(player);
    }

    @Override
    public void close(Player player) {
        gui.close(player);
    }

    @Override
    public void setupItems() {
        GuiItem newReward = ItemBuilder.from(Material.COMPARATOR)
                .setName("§aAdd new action")
                .setLore("§fCreate new enter/leave action for the region")
                .asGuiItem(event -> new RegionEventActionCreateGui().open(player, region.getId()));

        GuiItem viewReward = ItemBuilder.from(Material.ENDER_PEARL)
                .setName("§aView actions")
                .setLore("§fView the current actions of the region")
                .asGuiItem(event -> new RegionEventViewGui().open(player, region.getId()));

        gui.setItem(2, 4, newReward);
        gui.setItem(2, 6, viewReward);

        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .asGuiItem();

        gui.getFiller().fill(filler);
    }

}
