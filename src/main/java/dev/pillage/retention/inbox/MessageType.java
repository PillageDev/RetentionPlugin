package dev.pillage.retention.inbox;

public enum MessageType {
    MESSAGE,
    ITEM,
    MONEY;

    public static int getArgLengths(MessageType type) {
        switch (type) {
            case MESSAGE -> {
                return 4;
            }
            case ITEM, MONEY -> {
                return 5;
            }
        }
        return 0;
    }
}
