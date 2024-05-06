package dev.pillage.retention.guis;

import dev.pillage.retention.cache.PlayerCache;
import dev.pillage.retention.cache.entities.RPlayer;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.pillage.retention.inbox.Message;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class InboxGui extends RGui {

    private final PaginatedGui gui;
    private Player player;

    public InboxGui() {
        gui = Gui.paginated()
                .title(Component.text("Inbox"))
                .rows(5)
                .create();

        gui.setDefaultClickAction(event -> event.setCancelled(true));
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
            if (i == 38 || i == 42) continue;
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

        gui.setItem(38, back);
        gui.setItem(42, next);

        RPlayer rPlayer = PlayerCache.get(player.getUniqueId());

        if ((rPlayer.getInbox().getUnreadMessages() != null && !rPlayer.getInbox().getUnreadMessages().isEmpty()) ||
                (rPlayer.getInbox().getReadMessages() != null && !rPlayer.getInbox().getReadMessages().isEmpty())) {
            for (Message message : rPlayer.getInbox().getUnreadMessages()) {
                GuiItem messageItem = ItemBuilder.from(Material.PAPER)
                        .setName("§a" + message.getTitle())
                        .setLore("§7From: §e" + message.getSender(), "§fClick to view the message")
                        .asGuiItem(event -> {

                        });

                gui.addItem(messageItem);
            }
            for (Message message : rPlayer.getInbox().getReadMessages()) {
                GuiItem messageItem = ItemBuilder.from(Material.PAPER)
                        .setName("§a" + message.getTitle())
                        .setLore("§fFrom: §e" + message.getSender(), "§fClick to view the message")
                        .asGuiItem(event -> {

                        });

                gui.addItem(messageItem);
            }
        } else {
            GuiItem noMessage = ItemBuilder.from(Material.BARRIER)
                    .setName("§cNo messages")
                    .setLore("§fYou have no messages")
                    .asGuiItem();

            gui.setItem(3, 5, noMessage);
        }
    }
}
