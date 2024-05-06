package dev.pillage.retention.rewards.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pillage.retention.rewards.RegionReward;
import dev.pillage.retention.rewards.RewardType;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MoneyReward extends RegionReward {

    public MoneyReward(double money, int seconds) {
        this.money = money;
        this.seconds = seconds;
    }

    @JsonProperty("type")
    private String type = "money";
    @JsonProperty("money")
    private double money;
    @JsonProperty("seconds")
    private int seconds;

    @JsonIgnore
    @Override
    public void giveReward(Player player) {
        // TODO: give the player money through vault api
    }

    @JsonIgnore
    @Override
    public int getTime() {
        return seconds;
    }

    @JsonIgnore
    @Override
    public RewardType getType() {
        return RewardType.MONEY;
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
