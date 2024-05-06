package dev.pillage.retention.rewards.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pillage.retention.guis.builders.ItemBuilder;
import dev.pillage.retention.rewards.RegionReward;
import dev.pillage.retention.rewards.RewardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageReward extends RegionReward {

    public MessageReward(String message, int seconds) {
        this.message = message;
        this.seconds = seconds;
    }

    @JsonProperty("type")
    private String type = "message";
    @JsonProperty("message")
    private String message;
    @JsonProperty("seconds")
    private int seconds;

    @JsonIgnore
    @Override
    public void giveReward(Player player) {
        player.sendMessage(message.replace("&", "§"));
    }

    @JsonIgnore
    @Override
    public int getTime() {
        return seconds;
    }

    @JsonIgnore
    @Override
    public RewardType getType() {
        return RewardType.MESSAGE;
    }

    @JsonIgnore
    @Override
    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
