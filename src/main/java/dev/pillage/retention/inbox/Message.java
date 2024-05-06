package dev.pillage.retention.inbox;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Message {

    // For the MESSAGE type
    public String getContent() {return "";}
    public String getTitle() {return "";}
    public String getSender() {return "";}

    // For the ITEM type
    public ItemStack getItem() {return null;}
    public String getItemContent() {return "";}
    public String getItemTitle() {return "";}
    public String getItemSender() {return "";}

    // For the MONEY type
    public double getMoney() {return 0.0;}
    public String getMoneyContent() {return "";}
    public String getMoneyTitle() {return "";}
    public String getMoneySender() {return "";}

    public void send(MessageType type, Object... args) {}

}
