package dev.pillage.retention.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.pillage.retention.PluginSettings;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.guis.HomeGui;
import dev.pillage.retention.guis.RegionsGui;
import dev.pillage.retention.listeners.WandClickEvent;
import dev.pillage.retention.utils.Cuboid;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

@CommandAlias("afk")
@Description("Setup afk regions")
public class AfkCommand extends BaseCommand {

    @Default
    @Description("stuff")
    public void afk(Player sender) {
        new HomeGui().open(sender);
    }

    @CommandAlias("afk")
    @Subcommand("regions")
    @Description("Commands for managing regions")
    public static class RegionsCommand extends BaseCommand {
        public RegionsCommand() {
        }

        @Subcommand("setup")
        @Description("Create an afk region")
        @Syntax("/afk regions create <name>")
        public void create(Player sender, String[] args) {
            if (WandClickEvent.playerSelectionNames.containsKey(sender)) {
                sender.sendMessage("You already have a region creation in progress, please finish it or cancel it with /afk cancel");
                return;
            }

            if (args.length == 0) {
                sender.sendMessage("You must provide a name for the region");
                return;
            }

            sender.sendMessage("Region creation started. To cancel, run /afk regions cancel. To finalize, run /afk regions complete. Name: " + args[0]);
            WandClickEvent.playerSelectionNames.put(sender, args[0]);
        }

        @Subcommand("cancel")
        @Description("Cancel the current region creation")
        public void cancel(Player sender) {
            if (!WandClickEvent.playerSelectionNames.containsKey(sender)) {
                sender.sendMessage("You do not have a region creation in progress");
                return;
            }
            WandClickEvent.playerSelectionNames.remove(sender);
            WandClickEvent.playerSelections.remove(sender);
            sender.sendMessage("Region creation cancelled");
        }

        @Subcommand("complete")
        @Description("Complete the current region creation")
        public void complete(Player sender) {
            if (WandClickEvent.playerSelections.get(sender).complete()) {
                Location loc1 = WandClickEvent.playerSelections.get(sender).loc1;
                Location loc2 = WandClickEvent.playerSelections.get(sender).loc2;
                Cuboid cuboid = new Cuboid(loc1, loc2);
                Region region = new Region(cuboid, WandClickEvent.playerSelectionNames.get(sender));
                RegionCache.addRegion(region);

                WandClickEvent.playerSelectionNames.remove(sender);
                WandClickEvent.playerSelections.remove(sender);

                sender.sendMessage("Region created with name " + region.getName());
            } else {
                sender.sendMessage("You must select two locations to create a region");
            }
        }

        @Subcommand("delete")
        @Description("Delete an afk region")
        @CommandCompletion("@regions")
        public void delete(Player sender, String[] args) {
            if (args.length == 0) {
                sender.sendMessage("You must provide a name for the region");
                return;
            }

            if (!RegionCache.isRegion(args[0])) {
                sender.sendMessage("Region not found");
                return;
            }

            RegionCache.removeRegion(args[0]);
            sender.sendMessage("Region removed");
        }

        @Subcommand("manage")
        @Description("View the afk regions menu")
        public void manage(Player sender) {
            new RegionsGui().open(sender.getPlayer());
        }
    }

    @Subcommand("wand")
    @Description("Get the afk region creator")
    public void wand(Player sender) {
        ItemStack wand = new ItemStack(Material.valueOf(PluginSettings.WAND_ITEM.getString().toUpperCase()));
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName(PluginSettings.WAND_NAME.getString().replace("&", "§"));
        wand.setItemMeta(meta);
        sender.getInventory().addItem(wand);
        sender.sendMessage("You have been given the afk region creator");
    }
}
