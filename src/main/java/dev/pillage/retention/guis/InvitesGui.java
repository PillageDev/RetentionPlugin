package dev.pillage.retention.guis;

import dev.pillage.retention.cache.PlayerCache;
import dev.pillage.retention.cache.entities.RPlayer;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class InvitesGui extends RGui {
    private final PaginatedGui gui;
    private Player player;

    public InvitesGui() {
        gui = Gui.paginated()
                .title(Component.text("Invites"))
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
        List<GuiItem> inviteItems = new ArrayList<>();

        for (int i = 1; i < 21; i++) {
            GuiItem invite = ItemBuilder.from(Material.PLAYER_HEAD)
                    .setName("§aInvite §6#" + i)
                    .setLore("§7- Player Invited: ", "§7- Date Invited: ", "§7- Reward: ")
                    .asGuiItem();

            inviteItems.add(invite);
        }

        // Filler
        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .asGuiItem();

        for (int i = 0; i < 9; i++) {
            gui.setItem(i, filler);
        }

        for (int i = 27; i < 36; i++) {
            gui.setItem(i, filler);
        }

        gui.setItem(2, 1, filler);
        gui.setItem(2, 9, filler);
        gui.setItem(3, 1, filler);
        gui.setItem(3, 9, filler);

        GuiItem back = ItemBuilder.from(Material.ARROW)
                .setName("§aBack")
                .asGuiItem(event -> gui.previous());

        GuiItem next = ItemBuilder.from(Material.ARROW)
                .setName("§aNext")
                .asGuiItem(event -> gui.next());

        RPlayer rPlayer = PlayerCache.get(player.getUniqueId());
        assert rPlayer != null;
        String ipConditional = rPlayer.isIpGenerated() ? "§7Your referral ip is: §6" + rPlayer.getIp() : "§cYou don't have a referral ip! Click to generate";

        GuiItem help = ItemBuilder.from(Material.BOOK)
                .setName("§aHelp")
                .setLore(ipConditional, "§7Both you and your friend will receive rewards!")
                .asGuiItem(event -> {
                    if (!rPlayer.isIpGenerated()) {
                        rPlayer.updateProperty("ip", player.getName().toLowerCase() + ".hypixel.net");
                        player.sendMessage("§aReferral ip generated! Give this ip to a friend: §6" + rPlayer.getIp());
                        setupItems();
                    }
                });

        gui.updateItem(4, help);

        gui.setItem(29, back);
        gui.setItem(33, next);

        gui.addItem(inviteItems.toArray(new GuiItem[0]));
    }
}
