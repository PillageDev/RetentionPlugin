package dev.pillage.retention.guis;

import dev.pillage.retention.Retention;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.cache.entities.Region;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.pillage.retention.rewards.RegionReward;
import dev.pillage.retention.rewards.impl.*;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.nemo_64.spigotutilities.playerinputs.chatinput.PlayerChatInput;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegionRewardCreateGui extends RGui {
    private final Gui gui;
    private Player player;
    private Region region;

    private String rewardType = "Command";
    private int secondRequirement = -1;

    // Item reward
    private String name = null;
    private Material material = null;
    private int amount = -1;
    private List<String> lore = null;

    // Money
    private double money = -1;

    // Command
    private String command = null;

    // Message
    private String message = null;

    // Title
    private String title = null;
    private String subtitle = null;
    private int duration = -1;

    public RegionRewardCreateGui() {
        gui = Gui.gui()
                .title(Component.text("Create reward"))
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
        GuiItem filler = ItemBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
                .setName(" ")
                .asGuiItem();

        gui.getFiller().fill(filler);


        // All slots used:
        // 10, 12, 13, 14, 16, 20, 22, 24, 26
        gui.updateItem(10, filler);
        gui.updateItem(12, filler);
        gui.updateItem(13, filler);
        gui.updateItem(14, filler);
        gui.updateItem(16, filler);
        gui.updateItem(20, filler);
        gui.updateItem(22, filler);
        gui.updateItem(24, filler);
        gui.updateItem(26, filler);

        GuiItem commandRewardType = ItemBuilder.from(Material.COMMAND_BLOCK)
                .setName("§aCommand")
                .setLore("§7Run a command when the player enters the region")
                .asGuiItem(event -> {
                    this.rewardType = "Item";
                    setupItems();
                });

        GuiItem itemRewardType = ItemBuilder.from(Material.DIAMOND_SWORD)
                .setName("§aItem")
                .setLore("§7Give the player an item when they enter the region")
                .asGuiItem(event -> {
                    this.rewardType = "Message";
                    setupItems();
                });

        GuiItem messageRewardType = ItemBuilder.from(Material.PAPER)
                .setName("§aMessage")
                .setLore("§7Send a message to the player when they enter the region")
                .asGuiItem(event -> {
                    this.rewardType = "Money";
                    setupItems();
                });

        GuiItem moneyRewardType = ItemBuilder.from(Material.GOLD_INGOT)
                .setName("§aMoney")
                .setLore("§7Give the player money when they enter the region")
                .asGuiItem(event -> {
                    this.rewardType = "Title";
                    setupItems();
                });

        GuiItem titleRewardType = ItemBuilder.from(Material.NAME_TAG)
                .setName("§aTitle")
                .setLore("§7Send a title to the player when they enter the region")
                .asGuiItem(event -> {
                    this.rewardType = "Command";
                    setupItems();
                });

        GuiItem setSecondsItem = ItemBuilder.from(Material.CLOCK)
                .setName("§aSet seconds")
                .setLore("§7Set the amount of seconds the player must be in the region to trigger the reward", "§7Current: §6" + this.secondRequirement)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<Integer> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, i) -> Integer.parseInt(i));

                    builder.onFinish((p, i) -> {
                        this.secondRequirement = i;
                        open(player, region.getId());
                    });

                    builder.isValidInput((p, i) -> {
                        try {
                            return Integer.parseInt(i) > 0;
                        } catch (Exception ignored) {
                            return false;
                        }
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the amount of seconds the player must be in the region to trigger the reward.");

                    PlayerChatInput<Integer> in = builder.build();
                    in.start();
                });

        switch (this.rewardType) {
            case "Item" -> {
                gui.updateItem(10, itemRewardType);
                handleItemReward();
                gui.updateItem(24, setSecondsItem);
            }
            case "Money" -> {
                gui.updateItem(10, moneyRewardType);
                handleMoneyReward();
                gui.updateItem(16, setSecondsItem);
            }
            case "Command" -> {
                gui.updateItem(10, commandRewardType);
                handleCommandReward();
                gui.updateItem(16, setSecondsItem);
            }
            case "Message" -> {
                gui.updateItem(10, messageRewardType);
                handleMessageReward();
                gui.updateItem(16, setSecondsItem);
            }
            case "Title" -> {
                gui.updateItem(10, titleRewardType);
                handleTitleReward();
                gui.updateItem(22, setSecondsItem);
            }
        }

        GuiItem confirmCreation = ItemBuilder.from(Material.GREEN_WOOL)
                        .setName("§aConfirm action creation")
                        .asGuiItem(event -> {
                            if (region.getRewards() == null) {
                                region.setRewards(new ArrayList<>());
                            }
                            region.addReward(createReward());
                            close(player);
                            player.sendMessage("§aReward created successfully");
                        });

        GuiItem cannotCreate = ItemBuilder.from(Material.RED_WOOL)
                        .setName("§cCreation not complete")
                        .setLore("§7Please complete all fields before confirming")
                        .asGuiItem();

        gui.updateItem(26, readyToCreate() ? confirmCreation : cannotCreate);
    }

    private void handleItemReward() { // Slots: 10, 12, 14, 16, 20, 24
        List<String> nameItemLore = new ArrayList<>();
        nameItemLore.add("§7The name of the item");

        if (this.name != null) {
            nameItemLore.add("§7Current: §6" + this.name);
        }

        GuiItem nameItem = ItemBuilder.from(Material.NAME_TAG)
                .setName("§aName")
                .setLore(nameItemLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, str) -> str.replace("&", "§"));

                    builder.onFinish((p, str) -> {
                        this.name = str;
                        open(player, region.getId());
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the name of the item.");

                    PlayerChatInput<String> in = builder.build();
                    in.start();
                });

        gui.updateItem(12, nameItem);

        List<String> materialItemLore = new ArrayList<>();
        materialItemLore.add("§7The material of the item");

        Material itemMaterial;
        if (this.material != null) {
            materialItemLore.add("§7Current: §6" + this.material.name().replace("_", " "));
            itemMaterial = this.material;
        } else {
            itemMaterial = Material.GRASS_BLOCK;
        }

        GuiItem materialItem = ItemBuilder.from(itemMaterial)
                .setName("§aMaterial")
                .setLore(materialItemLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<Material> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, mat) -> Material.valueOf(mat.toUpperCase()));

                    builder.onFinish((p, mat) -> {
                        this.material = mat;
                        open(player, region.getId());
                    });

                    builder.isValidInput((p, mat) -> {
                        try {
                            Material.valueOf(mat.toUpperCase());
                        } catch (Exception ignored) {
                            return false;
                        }
                        return true;
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the material of the item, using _");

                    PlayerChatInput<Material> in = builder.build();
                    in.start();
                });

        gui.updateItem(14, materialItem);

        List<String> amountItemLore = new ArrayList<>();
        amountItemLore.add("§7The amount of the item");

        if (this.amount != -1) {
            amountItemLore.add("§7Current: §6" + this.amount);
        }

        GuiItem amountItem = ItemBuilder.from(Material.ANVIL)
                .setName("§aAmount")
                .setLore(amountItemLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<Integer> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, i) -> Integer.valueOf(i));

                    builder.onFinish((p, i) -> {
                        this.amount = i;
                        open(player, region.getId());
                    });

                    builder.isValidInput((p, i) -> {
                        try {
                            return Integer.parseInt(i) > 0;
                        } catch (Exception ignored) {
                            return false;
                        }
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the amount of the item.");

                    PlayerChatInput<Integer> in = builder.build();
                    in.start();
                });

        gui.updateItem(16, amountItem);

        List<String> loreItemLore = new ArrayList<>();
        loreItemLore.add("§7The lore of the item");

        if (this.lore != null) {
            for (int i = 0; i < this.lore.size(); i++) {
                int realLine = i + 1;
                loreItemLore.add("§7Line " + realLine + ": §6" + this.lore.get(i));
            }
        }

        GuiItem loreItem = ItemBuilder.from(Material.PAPER)
                .setName("§aLore")
                .setLore(loreItemLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, str) -> str.replace("&", "§"));

                    builder.onFinish((p, str) -> {
                        if (this.lore == null) {
                            this.lore = new ArrayList<>();
                        }
                        this.lore.clear();
                        String[] loreLines = str.split(",");
                        // remove leading space
                        for (String line : loreLines) {
                            line = line.replaceFirst("^\\s+", "");
                            lore.add(line);
                        }
                        open(player, region.getId());
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the lore of the item, comma seperated");

                    PlayerChatInput<String> in = builder.build();
                    in.start();
                });

        gui.updateItem(20, loreItem);
    }

    private void handleMoneyReward() { // Slots: 10, 13, 16
        List<String> moneyLore = new ArrayList<>();
        moneyLore.add("§7The amount of money to give the player");

        if (this.money != -1) {
            moneyLore.add("§7Current: §6" + this.money);
        }

        GuiItem setMoneyItem = ItemBuilder.from(Material.GOLD_INGOT)
                .setName("§aMoney")
                .setLore(moneyLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<Double> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, d) -> Double.parseDouble(d));

                    builder.onFinish((p, d) -> {
                        this.money = d;
                        open(player, region.getId());
                    });

                    builder.isValidInput((p, d) -> {
                        try {
                            return Double.parseDouble(d) > 0;
                        } catch (Exception ignored) {
                            return false;
                        }
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the amount of money to give the player.");

                    PlayerChatInput<Double> in = builder.build();
                    in.start();
                });

        gui.updateItem(13, setMoneyItem);
    }

    private void handleCommandReward() { // Slots: 10, 13, 16
        List<String> commandLore = new ArrayList<>();
        commandLore.add("§7The command to run when the player gets the reward");

        if (this.command != null) {
            commandLore.add("§7Current: §6" + this.command);
        }

        GuiItem setCommandItem = ItemBuilder.from(Material.PAPER)
                .setName("§aCommand")
                .setLore(commandLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, d) -> d);

                    builder.onFinish((p, d) -> {
                        this.command = d;
                        open(player, region.getId());
                    });

                    builder.isValidInput((p, d) -> !d.startsWith("/"));

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the command to run when the player gets the reward.");

                    PlayerChatInput<String> in = builder.build();
                    in.start();
                });

        gui.updateItem(13, setCommandItem);
    }

    private void handleMessageReward() { // Slots: 10, 13, 16
        List<String> messageLore = new ArrayList<>();
        messageLore.add("§7The message to send to the player");

        if (this.command != null) {
            messageLore.add("§7Current: §6" + this.money);
        }

        GuiItem setMessageItem = ItemBuilder.from(Material.WRITABLE_BOOK)
                .setName("§aMessage")
                .setLore(messageLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, d) -> d);

                    builder.onFinish((p, d) -> {
                        this.message = d;
                        open(player, region.getId());
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the message to send to the player.");

                    PlayerChatInput<String> in = builder.build();
                    in.start();
                });

        gui.updateItem(13, setMessageItem);
    }

    private void handleTitleReward() {// Slots: 10, 12, 14, 16, 22
        List<String> titleLore = new ArrayList<>();
        titleLore.add("§7The title to send to the player");

        if (this.title != null) {
            titleLore.add("§7Current: §6" + this.title);
        }

        GuiItem titleItem = ItemBuilder.from(Material.PAPER)
                .setName("§aTitle")
                .setLore(titleLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, d) -> d);

                    builder.onFinish((p, d) -> {
                        this.title = d;
                        open(player, region.getId());
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the title to send to the player.");

                    PlayerChatInput<String> in = builder.build();
                    in.start();
                });

        gui.updateItem(12, titleItem);

        List<String> subtitleLore = new ArrayList<>();
        subtitleLore.add("§7The subtitle to send to the player");

        if (this.subtitle != null) {
            subtitleLore.add("§7Current: §6" + this.subtitle);
        }

        GuiItem subtitleItem = ItemBuilder.from(Material.PAPER)
                .setName("§aSubtitle")
                .setLore(subtitleLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<String> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, d) -> d);

                    builder.onFinish((p, d) -> {
                        this.subtitle = d;
                        open(player, region.getId());
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the subtitle to send to the player.");

                    PlayerChatInput<String> in = builder.build();
                    in.start();
                });

        gui.updateItem(14, subtitleItem);

        List<String> durationLore = new ArrayList<>();
        durationLore.add("§7The duration of the title");

        if (this.duration != -1) {
            durationLore.add("§7Current: §6" + this.duration);
        }

        GuiItem durationItem = ItemBuilder.from(Material.COMPASS)
                .setName("§aDuration")
                .setLore(durationLore)
                .asGuiItem(event -> {
                    close(player);
                    PlayerChatInput.PlayerChatInputBuilder<Integer> builder = new PlayerChatInput.PlayerChatInputBuilder<>(Retention.getInstance(), player);

                    builder.setValue((p, i) -> Integer.parseInt(i) * 20); // to ticks

                    builder.onFinish((p, i) -> {
                        this.duration = i;
                        open(player, region.getId());
                    });

                    builder.isValidInput((p, i) -> {
                        try {
                            return Integer.parseInt(i) > 0;
                        } catch (Exception ignored) {
                            return false;
                        }
                    });

                    builder.onCancel((p) -> open(player, region.getId()));

                    builder.toCancel("cancel");
                    builder.sendValueMessage("§7Type 'cancel' to cancel. \n" + "§7Please enter the duration of the title.");

                    PlayerChatInput<Integer> in = builder.build();
                    in.start();
                });

        gui.updateItem(16, durationItem);
    }

    private boolean readyToCreate() {
        switch (this.rewardType.toLowerCase()) {
            case "item" -> {
                return this.name != null && this.material != null && this.amount != -1 && this.lore != null && this.secondRequirement != -1;
            }
            case "money" -> {
                return this.money != -1 && this.secondRequirement != -1;
            }
            case "command" -> {
                return this.command != null && this.secondRequirement != -1;
            }
            case "message" -> {
                return this.message != null && this.secondRequirement != -1;
            }
            case "title" -> {
                return this.title != null && this.subtitle != null && this.duration != -1 && this.secondRequirement != -1;
            }
            default -> {
                return false;
            }
        }
    }

    private RegionReward createReward() {
        switch (this.rewardType.toLowerCase()) {
            case "item" -> {
                return new ItemReward(this.name, this.material, this.amount, this.secondRequirement, this.lore);
            }
            case "money" -> {
                return new MoneyReward(this.money, this.secondRequirement);
            }
            case "command" -> {
                return new CommandReward(this.command, this.secondRequirement);
            }
            case "message" -> {
                return new MessageReward(this.message, this.secondRequirement);
            }
            case "title" -> {
                return new TitleReward(this.title, this.subtitle, this.duration, this.secondRequirement);
            }
            default -> {
                return null;
            }
        }
    }
}
