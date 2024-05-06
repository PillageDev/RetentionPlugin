package dev.pillage.retention.cache.entities;

import co.aikar.idb.DB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pillage.retention.cache.PlayerCache;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.inbox.PlayerInbox;
import lombok.*;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RPlayer {

    private UUID uuid;
    /**
     * Stored in seconds
     */
    private long playtime;

    private int playtimePoints;

    private PlayerInbox inbox;

    public PlayerInbox getInbox() {
        if (inbox == null) {
            inbox = new PlayerInbox(uuid);
        }
        return inbox;
    }

    // Referrals
    private String ip;

    @JsonIgnore
    public boolean isIpGenerated() {
        return ip != null;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static RPlayer fromString(String data) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(data, RPlayer.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public void updateProperty(String fieldName, Object value) {
        Field field;
        try {
            field = this.getClass().getDeclaredField(fieldName);
            field.set(this, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        DB.executeUpdate("INSERT OR REPLACE INTO players (uuid, data) VALUES (?, ?)", uuid, this.toString());
        PlayerCache.updatePlayer(uuid, this);
    }
}
