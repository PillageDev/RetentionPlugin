package dev.pillage.retention.guis;

import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DailyRewardGui extends RGui {
    private final PaginatedGui gui;
    private Player player;

    public DailyRewardGui() {
        gui = Gui.paginated()
                .title(Component.text("Daily Rewards"))
                .rows(3)
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
        List<GuiItem> dailyItems = new ArrayList<>();

        GuiItem dailyClaimed = ItemBuilder.from(Material.MINECART)
                .setName("§aDay 1")
                .setLore("§7Claimed")
                .asGuiItem();

        dailyItems.add(dailyClaimed);

        for (int i = 1; i < 10; i++) {
            GuiItem dailyUnclaimed = ItemBuilder.from(Material.CHEST_MINECART)
                    .setName("§aDay " + (i + 1))
                    .setLore("§7Click to claim your reward")
                    .asGuiItem();

            dailyItems.add(dailyUnclaimed);
        }


        // Filler
        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .asGuiItem();

        for (int i = 0; i < 11; i++) {
            gui.setItem(i, filler);
        }

        for (int i = 16; i < 27; i++) {
            gui.setItem(i, filler);
        }

        GuiItem back = ItemBuilder.from(Material.ARROW)
                .setName("§aBack")
                .asGuiItem(event -> gui.previous());

        GuiItem next = ItemBuilder.from(Material.ARROW)
                .setName("§aNext")
                .asGuiItem(event -> gui.next());

        gui.setItem(9, back);
        gui.setItem(17, next);

        gui.addItem(dailyItems.toArray(new GuiItem[0]));
    }

}
