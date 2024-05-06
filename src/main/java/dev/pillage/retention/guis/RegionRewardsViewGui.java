package dev.pillage.retention.guis;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.pillage.retention.rewards.RegionReward;
import dev.pillage.retention.rewards.RewardType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class RegionRewardsViewGui extends RGui {

    private Player player;
    private Region region;
    private final PaginatedGui gui;

    public RegionRewardsViewGui() {
        gui = Gui.paginated()
                .title(Component.text("All Region Rewards"))
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
                .asGuiItem(event -> {
                    gui.previous();
                });

        GuiItem next = ItemBuilder.from(Material.ARROW)
                .setName("§aNext")
                .asGuiItem(event -> {
                    gui.next();
                });

        gui.setItem(37, back);
        gui.setItem(42, next);

        if (!(region.getRewards().isEmpty()) && !(region.getRewards() == null)) {
            for (RegionReward reward : region.getRewards()) {
                Map<String, String> fieldValues = Maps.newHashMap();

                for (Field field : reward.getClass().getDeclaredFields()) {
                    if (field.getName().equalsIgnoreCase("type")) continue;
                    field.setAccessible(true);
                    try {
                        fieldValues.put(field.getName().substring(0, 1).toUpperCase() + field.getName().toLowerCase().substring(1), field.get(reward).toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                List<String> lore = Lists.newArrayList();
                for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                    lore.add("§7" + entry.getKey() + ": §f" + entry.getValue());
                }
                lore.add("§cClick to remove reward");

                GuiItem rewardItem = ItemBuilder.from(getMaterial(reward.getType()))
                        .setName("§a" + reward.getType().name().toLowerCase().substring(0, 1).toUpperCase() + reward.getType().name().toLowerCase().substring(1))
                        .setLore(lore)
                        .asGuiItem(event -> {
                            // remove reward
                            region.removeReward(reward);
                            close(player);
                            player.sendMessage("§aSuccessfully removed reward");
                        });
                gui.addItem(rewardItem);
            }
        } else {
            GuiItem noRewards = ItemBuilder.from(Material.BARRIER)
                    .setName("§cNo rewards found")
                    .setLore("§fThis region has no rewards, how about make some?")
                    .asGuiItem();

            gui.setItem(3, 5, noRewards);
        }

    }

    private Material getMaterial(RewardType type) {
        switch (type) {
            case MONEY -> {
                return Material.GOLD_INGOT;
            }
            case COMMAND -> {
                return Material.COMMAND_BLOCK;
            }
            case MESSAGE -> {
                return Material.PAPER;
            }
            case TITLE -> {
                return Material.NAME_TAG;
            }
            case ITEM -> {
                return Material.CHEST;
            }
        }
        return null;
    }
}
