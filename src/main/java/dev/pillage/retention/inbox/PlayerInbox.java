package dev.pillage.retention.inbox;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerInbox {

    public PlayerInbox(UUID uuid) {
        this.uuid = uuid;
    }

    private final UUID uuid;
    private List<Message> unreadMessages;
    private List<Message> readMessages;

    @JsonIgnore
    public int getUnreadMessageCount() {
        if (unreadMessages == null)
            return 0;
        return unreadMessages.size();
    }

    public void addMessage(Message message) {
        unreadMessages.add(message);
    }
}
