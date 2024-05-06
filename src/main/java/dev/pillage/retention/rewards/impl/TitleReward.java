package dev.pillage.retention.rewards.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pillage.retention.rewards.RegionReward;
import dev.pillage.retention.rewards.RewardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TitleReward extends RegionReward {

    public TitleReward(String title, String subtitle, int duration, int seconds) {
        this.title = title;
        this.subtitle = subtitle;
        this.duration = duration;
        this.seconds = seconds;
    }

    @JsonProperty("type")
    private String type = "title";
    @JsonProperty("title")
    private String title;
    @JsonProperty("subtitle")
    private String subtitle;
    @JsonProperty("duration")
    private int duration;
    @JsonProperty("seconds")
    private int seconds;

    @JsonIgnore
    @Override
    public void giveReward(Player player) {
        player.sendTitle(title.replace("&", "§"), subtitle.replace("&", "§"), 1, duration, 1);
    }

    @JsonIgnore
    @Override
    public int getTime() {
        return seconds;
    }

    @JsonIgnore
    @Override
    public RewardType getType() {
        return RewardType.TITLE;
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
