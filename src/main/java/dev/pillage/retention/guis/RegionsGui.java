package dev.pillage.retention.guis;

import dev.pillage.retention.Retention;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegionsGui extends RGui {

    private final PaginatedGui gui;
    private Player player;

    public RegionsGui() {
        gui = Gui.paginated()
                .title(Component.text("All Regions"))
                .rows(4)
                .create();

        gui.setDefaultClickAction(event -> {
            event.setCancelled(true);
        });
    }

    @Override
    public void open(Player player) {
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
        List<GuiItem> regionItems = new ArrayList<>();

        for (Region r : RegionCache.getAllRegions()) {
            GuiItem item = ItemBuilder.from(Material.PAPER)
                    .setName("§6" + r.getName())
                    .setLore("§fClick to edit the region")
                    .asGuiItem(event -> {
                        if (RegionCache.getRegionFromId(r.getId()) == null) {
                            close(player);
                            player.sendMessage("This region has been deleted.");
                            return;
                        }
                        new RegionEditorGui().open(player, r.getId());
                    });
            regionItems.add(item);
        }

        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                        .setName(" ")
                        .asGuiItem();

        for (int i = 0; i < 9; i++) {
            gui.setItem(i, filler);
        }

        gui.setItem(2, 1, filler);
        gui.setItem(2, 9, filler);
        gui.setItem(3, 1, filler);
        gui.setItem(3, 9, filler);

        for (int i = 27; i <= 35; i++) {
            if (i == 29 || i == 33) continue;
            gui.setItem(i, filler);
        }

        GuiItem back = ItemBuilder.from(Material.ARROW)
                        .setName("§aBack")
                        .asGuiItem(event -> {
                            gui.previous();
                        });

        GuiItem next = ItemBuilder.from(Material.ARROW)
                        .setName("§aNext")
                        .asGuiItem(event -> {
                            gui.next();
                        });

        gui.setItem(29, back);
        gui.setItem(33, next);
        gui.addItem(regionItems.toArray(new GuiItem[0]));

        GuiItem newRegion = ItemBuilder.from(Material.GREEN_WOOL)
                .setName("§aCreate new region")
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, str) -> str);

                    builder.onFinish((p, str) -> {
                        player.performCommand("afk regions setup " + str);
                    });

                    builder.onCancel((p) -> open(player));

                    builder.toCancel("cancel");

                    builder.sendValueMessage("§7Please enter the name for the region. Type 'cancel' to cancel.");

                    PlayerChatInput<String> in = builder.build();
                    in.start();

                });
        gui.addItem(newRegion);
    }
}
