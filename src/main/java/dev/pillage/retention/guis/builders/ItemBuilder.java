package dev.pillage.retention.guis.builders;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    private ItemStack item;
    private ItemMeta meta;

    public ItemBuilder() {
    }

    public static ItemBuilder from(Material material) {
        ItemBuilder itemBuilder = new ItemBuilder();
        itemBuilder.item = new ItemStack(material);
        itemBuilder.meta = itemBuilder.item.getItemMeta();
        return itemBuilder;
    }

    public ItemBuilder setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public GuiItem asGuiItem() {
        return new GuiItem(build());
    }

    public GuiItem asGuiItem(GuiAction<InventoryClickEvent> action) {
        return new GuiItem(build(), action);
    }

    private ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }

    private ItemMeta getMeta() {
        return meta;
    }

    public ItemBuilder setSkullOwner(OfflinePlayer player) {
        SkullMeta skullMeta = (SkullMeta) meta;
        skullMeta.setOwningPlayer(player);
        this.meta = skullMeta;

        return this;
    }

    public ItemBuilder setLore(String... lore) {
        meta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setLore(Iterable<String> lore) {
        meta.setLore((List<String>) lore);
        return this;
    }
}
