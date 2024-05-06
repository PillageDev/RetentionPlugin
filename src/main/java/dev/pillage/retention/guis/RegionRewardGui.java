package dev.pillage.retention.guis;

import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RegionRewardGui extends RGui {

    private Player player;
    private final Gui gui;
    private Region region;

    public RegionRewardGui() {
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
                .setName("§aAdd new reward")
                .setLore("§fCreate a new afk reward for the region")
                .asGuiItem(event -> new RegionRewardCreateGui().open(player, region.getId()));

        GuiItem viewReward = ItemBuilder.from(Material.ENDER_PEARL)
                .setName("§aView rewards")
                .setLore("§fView all the current rewards for the region")
                .asGuiItem(event -> new ViewRegionRewardsGui().open(player, region.getId()));

        gui.setItem(2, 4, newReward);
        gui.setItem(2, 6, viewReward);

        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                    .setName(" ")
                    .asGuiItem();

        gui.getFiller().fill(filler);
    }

}
