package dev.pillage.retention.inbox;

import java.util.Objects;

public class MessagingService {

    public static void dispatchMessage(MessageSendType sendType, MessageType type, Object... args) {
        Objects.requireNonNull(MessageSendType.getDispatcher(sendType)).send(type, args);
    }

}
