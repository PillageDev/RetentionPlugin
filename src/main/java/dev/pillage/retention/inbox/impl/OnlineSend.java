package dev.pillage.retention.inbox.impl;

import dev.pillage.retention.cache.PlayerCache;
import dev.pillage.retention.cache.entities.RPlayer;
import dev.pillage.retention.inbox.Message;
import dev.pillage.retention.inbox.MessageType;
import dev.pillage.retention.inbox.PlayerInbox;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class OnlineSend extends Message {

    /**
     * Used to broadcast something for all ONLINE player's inbox. The format for args param is as follows:
     * For all messages sent through this pipeline, they must follow this format<br>
     * args[0] = title<br>
     * args[1] = content<br>
     * args[2] = sender<br>
     * Custom args can be added for different message types
     * @param type
     * @param args For a type of MESSAGE, follow the global pipeline format. For a message type of ITEM, args[3] = {@link ItemStack},<br>
     *             for a type of MONEY, args[3] = {@link Double}
     */
    @Override
    public void send(MessageType type, Object... args) {
        Message message = switch (type) {
            case MESSAGE -> new TextMessage((String) args[1], (String) args[0], (String) args[2]);
            case ITEM -> new ItemMessage((ItemStack) args[3], (String) args[1], (String) args[0], (String) args[2]);
            case MONEY -> new MoneyMessage((double) args[3], (String) args[1], (String) args[0], (String) args[2]);
        };
        for (RPlayer player : PlayerCache.getOnlinePlayers()) {
            PlayerInbox inbox = player.getInbox();
            if (inbox == null) {
                player.setInbox(new PlayerInbox(player.getUuid()));
            }
            assert inbox != null;
            if (inbox.getUnreadMessages() == null) {
                inbox.setUnreadMessages(new ArrayList<>());
            }
            inbox.addMessage(message);
        }
    }
}
