package dev.pillage.retention.guis;

import dev.pillage.retention.cache.PlayerCache;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class HomeGui extends RGui {
    private final Gui gui;
    private Player player;

    public HomeGui() {
        gui = Gui.gui()
                .title(Component.text("Home"))
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
        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem());

        gui.setItem(4,
                ItemBuilder.from(Material.PLAYER_HEAD)
                        .setSkullOwner(player)
                        .setName("§aYour Profile")
                        .setLore("§7- Points: ", "§7- Playtime: ", "§7- Daily Streak: ")
                        .asGuiItem());

        gui.setItem(10,
                ItemBuilder.from(Material.ITEM_FRAME)
                        .setName("§aDaily Reward Calendar")
                        .setLore("§7Claim your daily rewards")
                        .asGuiItem(event -> new DailyRewardGui().open(player)));

        gui.setItem(12,
                ItemBuilder.from(Material.GOLD_BLOCK)
                        .setName("§aStore")
                        .setLore("§7Buy items with playtime points")
                        .asGuiItem(e -> {
                            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

                            // Set the book's title and author
                            BookMeta meta = (BookMeta) book.getItemMeta();
                            meta.setTitle("My Custom Book");
                            meta.setAuthor("Your Name");

                            // Set the pages of the book
                            meta.addPage("This is the first page.");
                            meta.addPage("This is the second page.");
                            meta.addPage("And this is the third page.");

                            // Apply the meta to the book
                            book.setItemMeta(meta);

                            // Give the book to the player
                            player.openBook(book);
                        }));

        gui.setItem(14,
                ItemBuilder.from(Material.NAME_TAG)
                        .setName("§aInvites")
                        .setLore("§7View your invites")
                        .asGuiItem(event -> new InvitesGui().open(player)));

        gui.setItem(16,
                ItemBuilder.from(Material.CHEST)
                        .setName("§aInbox (§6" + PlayerCache.get(player.getUniqueId()).getInbox().getUnreadMessageCount() + "§a)")
                        .setLore("§7Check to see if you have any rewards to claim")
                        .asGuiItem(event -> {
                            new InboxGui().open(player);
                        }));

    }

}
