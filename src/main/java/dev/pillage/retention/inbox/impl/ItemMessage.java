package dev.pillage.retention.inbox.impl;

import dev.pillage.retention.inbox.Message;
import lombok.AllArgsConstructor;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class ItemMessage extends Message {

    private final ItemStack item;
    private final String content;
    private final String title;
    private final String sender;

    @Override
    public ItemStack getItem() {
        return item;
    }

    @Override
    public String getItemContent() {
        return content;
    }

    @Override
    public String getItemTitle() {
        return title;
    }

    @Override
    public String getItemSender() {
        return sender;
    }
}
