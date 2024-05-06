package dev.pillage.retention.inbox.impl;

import dev.pillage.retention.cache.PlayerCache;
import dev.pillage.retention.cache.entities.RPlayer;
import dev.pillage.retention.inbox.Message;
import dev.pillage.retention.inbox.MessageType;
import dev.pillage.retention.inbox.PlayerInbox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class PrivateSend extends Message {

    /**
     * Used to broadcast something to a specific player's inbox. The format for args param is as follows:
     * For all messages sent through this pipeline, they must follow this format<br>
     * args[0] = recipient<br>
     * args[1] = title<br>
     * args[2] = content<br>
     * args[3] = sender<br>
     * Custom args can be added for different message types
     * @param type
     * @param args For a type of MESSAGE, follow the global pipeline format. For a message type of ITEM, args[4] = {@link ItemStack},<br>
     *             for a type of MONEY, args[4] = {@link Double}
     */
    @Override
    public void send(MessageType type, Object... args) {
        Message message = switch (type) {
            case MESSAGE -> new TextMessage((String) args[2], (String) args[1], (String) args[3]);
            case ITEM -> new ItemMessage((ItemStack) args[4], (String) args[2], (String) args[1], (String) args[3]);
            case MONEY -> new MoneyMessage((double) args[4], (String) args[2], (String) args[1], (String) args[3]);
        };
        Player player = (Player) args[0];
        RPlayer rPlayer = PlayerCache.getPlayerBothOnline(player.getUniqueId());
        PlayerInbox inbox = rPlayer.getInbox();
        if (inbox == null) {
            rPlayer.setInbox(new PlayerInbox(rPlayer.getUuid()));
        }
        assert inbox != null;
        if (inbox.getUnreadMessages() == null) {
            inbox.setUnreadMessages(new ArrayList<>());
        }
        inbox.addMessage(message);
    }
}
