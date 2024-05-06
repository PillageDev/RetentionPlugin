package dev.pillage.retention.guis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.pillage.retention.actions.Action;
import dev.pillage.retention.actions.ActionType;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class RegionEventViewGui extends RGui {

    private final PaginatedGui gui;
    private Player player;
    private Region region;

    public RegionEventViewGui() {
        gui = Gui.paginated()
                .title(Component.text("All Region Events"))
                .rows(5)
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));
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
        gui.setItem(4, 1, filler);
        gui.setItem(4, 9, filler);

        for (int i = 35; i <= 44; i++) {
            if (i == 37 || i == 42) continue;
            gui.setItem(i, filler);
        }

        GuiItem back = ItemBuilder.from(Material.ARROW)
                .setName("§aBack")
                .asGuiItem(event -> gui.previous());

        GuiItem next = ItemBuilder.from(Material.ARROW)
                .setName("§aNext")
                .asGuiItem(event -> gui.next());

        gui.setItem(37, back);
        gui.setItem(42, next);

        if (!region.getActions().isEmpty()) {
            for (Action action : region.getActions()) {
                Map<String, String> data = Maps.newHashMap();

                for (Field field : action.getClass().getDeclaredFields()) {
                    field.setAccessible(true);
                    try {
                        data.put(field.getName(), field.get(action).toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                List<String> lore = Lists.newArrayList();

                for (Map.Entry<String, String> entry : data.entrySet()) {
                    lore.add("§7" + entry.getKey() + ": §f" + entry.getValue());
                }

                lore.add("§cClick to remove the event");
                GuiItem actionItem = ItemBuilder.from(getMaterial(action.getType()))
                        .setName("§6" + action.getType())
                        .setLore(lore)
                        .asGuiItem();

                gui.addItem(actionItem);
            }
        } else {
            GuiItem noActions = ItemBuilder.from(Material.BARRIER)
                    .setName("§cNo rewards found")
                    .setLore("§fThis region has no actions, how about make some?")
                    .asGuiItem();

            gui.setItem(3, 5, noActions);
        }
    }

    private Material getMaterial(ActionType type) {
        return switch (type) {
            case COMMAND -> Material.COMMAND_BLOCK;
            case MESSAGE -> Material.PAPER;
            case TITLE -> Material.NAME_TAG;
        };
    }
}
