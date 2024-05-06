package dev.pillage.retention.inbox;

import dev.pillage.retention.inbox.impl.GlobalSend;
import dev.pillage.retention.inbox.impl.OnlineSend;
import dev.pillage.retention.inbox.impl.PrivateSend;

public enum MessageSendType {
    GLOBAL,
    ONLINE,
    PRIVATE;

    public static Message getDispatcher(MessageSendType type) {
        switch (type) {
            case GLOBAL -> {
                return new GlobalSend();
            }
            case ONLINE -> {
                return new OnlineSend();
            }
            case PRIVATE -> {
                return new PrivateSend();
            }
        }
        return null;
    }
}
