package dev.pillage.retention.guis;

import dev.pillage.retention.Retention;
import dev.pillage.retention.actions.Action;
import dev.pillage.retention.actions.ExecutionTime;
import dev.pillage.retention.actions.impl.CommandAction;
import dev.pillage.retention.actions.impl.MessageAction;
import dev.pillage.retention.actions.impl.TitleAction;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RegionEventActionCreateGui extends RGui {
    private final Gui gui;
    private Player player;
    private Region region;
    private boolean onExit = true; // On exit is diamond, On enter is gold
    private String interactionType = "Command";
    private String actionValue = null;

    public RegionEventActionCreateGui() {
        gui = Gui.gui()
                .title(Component.text("Create action"))
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
        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem());

        GuiItem onEnter = ItemBuilder.from(Material.GOLD_BLOCK)
                .setName("§aOn Enter")
                .setLore("§7Set the action for when a player enters the region", "§7Click to toggle")
                .asGuiItem(event -> {
                    this.onExit = true;
                    setupItems();
                });

        GuiItem onExit = ItemBuilder.from(Material.DIAMOND_BLOCK)
                .setName("§aOn Exit")
                .setLore("§7Set the action for when a player exits the region", "§7Click to toggle")
                .asGuiItem(event -> {
                    this.onExit = false;
                    setupItems();
                });

        if (this.onExit) {
            gui.updateItem(10, onExit);
        } else {
            gui.updateItem(10, onEnter);
        }

        GuiItem interactionType = ItemBuilder.from(Material.WRITABLE_BOOK)
                        .setName("§aInteraction Type: §6" + this.interactionType)
                        .setLore("§7Set the type of interaction", "§7Click to change")
                        .asGuiItem(event -> {
                            switch (this.interactionType) {
                                case "Command" -> this.interactionType = "Message";
                                case "Message" -> this.interactionType = "Title";
                                case "Title" -> this.interactionType = "Command";
                            }
                            this.actionValue = null;
                            setupItems();
                        });

        gui.updateItem(12, interactionType);

        String instructions = getInstructions();

        List<String> signLore = new ArrayList<>();
        signLore.add("§7Set the action for the interaction type §6" + this.interactionType.toLowerCase());
        if (this.actionValue != null) {
            signLore.add("§7Current value: §6" + this.actionValue);
        }

        gui.updateItem(15,
                ItemBuilder.from(Material.OAK_SIGN)
                        .setName("§aAction")
                        .setLore(signLore)
                        .asGuiItem(event -> {

                            close(player);
                            PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                            builder.setValue((p, str) -> str.replace("&", "§"));

                            builder.isValidInput((p, str) -> verifyInput(str));

                            builder.onFinish((p, str) -> {
                                this.actionValue = str;
                                open(player, region.getId());
                            });

                            builder.onCancel((p) -> open(player, region.getId()));

                            builder.toCancel("cancel");
                            builder.onInvalidInput((p, str) -> {
                                open(player, region.getId());
                                p.sendMessage("§cInvalid input, please try again.");
                                return true;
                            });
                            builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + instructions);

                            PlayerChatInput<String> in = builder.build();
                            in.start();
        }));


        // Orange box around sign
        GuiItem orangePane = ItemBuilder.from(Material.ORANGE_STAINED_GLASS_PANE)
                .setName(" ")
                .asGuiItem();

        gui.setItem(5, orangePane);
        gui.setItem(6, orangePane);
        gui.setItem(7, orangePane);
        gui.setItem(14, orangePane);
        gui.setItem(16, orangePane);
        gui.setItem(23, orangePane);
        gui.setItem(24, orangePane);
        gui.setItem(25, orangePane);

        GuiItem confirmCreation = ItemBuilder.from(Material.GREEN_WOOL)
                        .setName("§aConfirm action creation")
                        .setLore("§7Trigger: §6" + (this.onExit ? "Exit" : "Enter"), "§7Type: §6" + this.interactionType, "§7Action: §6" + (this.actionValue == null ? "None" : this.actionValue))
                        .asGuiItem(event -> {
                            if (region.getActions() == null) {
                                region.setActions(new ArrayList<>());
                            }
                            region.addAction(createAction());
                            close(player);
                            player.sendMessage("§aReward created successfully");
                        });

        GuiItem cannotCreate = ItemBuilder.from(Material.RED_WOOL)
                        .setName("§cCreation not complete")
                        .setLore("§7Please complete all fields before confirming")
                        .asGuiItem();

        gui.updateItem(26, this.actionValue == null ? cannotCreate : confirmCreation);

        gui.getFiller().fill(ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" ").asGuiItem());
    }

    private String getInstructions() {
        switch (this.interactionType.toLowerCase()) {
            case "command" -> {
                return "§7Enter the command to run when the player interacts with the region, no '/' needed";
            }
            case "message" -> {
                return "§7Enter the message to send to the player when they interact with the region";
            }
            case "title" -> {
                return "§7Use a , to separate the title and subtitle, don't put one for just title";
            }
            default -> {
                return "§cInvalid interaction type";
            }
        }
    }

    private boolean verifyInput(String input) {
        if (this.interactionType.equalsIgnoreCase("command")) {
            if (input.startsWith("/")) {
                player.sendMessage("§cCommands should not start with a /");
                return false;
            }
        }
        return true;
    }

    private Action createAction() {
        ExecutionTime time = this.onExit ? ExecutionTime.EXIT : ExecutionTime.ENTER;
        return switch (this.interactionType.toLowerCase()) {
            case "command" -> new CommandAction(this.actionValue, time);
            case "message" -> new MessageAction(this.actionValue, time);
            case "title" -> {
                String[] split = this.actionValue.split(",");
                if (split.length == 1) {
                    yield new TitleAction(split[0], time);
                } else {
                    yield new TitleAction(split[0], split[1], time);
                }
            }
            default -> null;
        };
    }
}
