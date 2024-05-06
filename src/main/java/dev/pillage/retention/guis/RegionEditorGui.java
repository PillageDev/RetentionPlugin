package dev.pillage.retention.guis;

import dev.pillage.retention.Retention;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class RegionEditorGui extends RGui {

    private final Gui gui;
    private Region region;
    private Player player;

    public RegionEditorGui() {
        gui = Gui.gui()
                .title(Component.text("Region Editor"))
                .rows(3)
                .create();

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
    }

    @Override
    public void open(Player player, int regionId) {
        this.region = RegionCache.getRegionFromId(regionId);
        this.player = player;
        setupItems();
        gui.open(player);
    }

    @Override
    public void close(Player player) {
        gui.close(player);
    }

    @Override
    public void setupItems() {
        GuiItem rename = ItemBuilder.from(Material.PAPER)
                .setName("§aRename")
                .setLore("§fRename the region", "§fCurrent value: §e" + region.getName())
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, str) -> str);

                    builder.onFinish((p, str) -> {
                        region.updateProperty("name", str);
                        open(player, region.getId());
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");

                    builder.sendValueMessage("§7Please enter the new name for the region. Type 'cancel' to cancel.");

                    PlayerChatInput<String> in = builder.build();
                    in.start();
                });

        GuiItem tp = ItemBuilder.from(Material.COMPASS)
                        .setName("§aTeleport")
                        .setLore("§fTeleport to the center of the region")
                        .asGuiItem(event -> player.teleport(region.getCuboid().getCenter()));

        GuiItem rewards = ItemBuilder.from(Material.CHEST)
                        .setName("§aRewards")
                        .setLore("§fSet the afk rewards for the region")
                        .asGuiItem(event -> new RegionRewardGui().open(player, region.getId()));

        GuiItem eventActions = ItemBuilder.from(Material.REDSTONE)
                        .setName("§aEvent Actions")
                        .setLore("§fSet the leave/enter actions for the region")
                        .asGuiItem(event -> new RegionEventsGui().open(player, region.getId()));

        gui.setItem(2, 2, tp);
        gui.setItem(2, 4, rename);
        gui.setItem(2, 6, eventActions);
        gui.setItem(2, 8, rewards);

        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ").asGuiItem();

        gui.getFiller().fill(filler);
    }
}
