package dev.pillage.retention.actions.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pillage.retention.actions.Action;
import dev.pillage.retention.actions.ActionType;
import dev.pillage.retention.actions.ExecutionTime;
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
public class TitleAction extends Action {

    public TitleAction(String title, String subtitle, ExecutionTime executionTime) {
        this.title = title;
        this.subtitle = subtitle;
        this.executionTime = executionTime;
    }

    public TitleAction(String title, ExecutionTime executionTime) {
        this.title = title;
        this.subtitle = "";
        this.executionTime = executionTime;
    }

    @JsonProperty("type")
    private String type = "title";
    @JsonProperty("title")
    private String title;
    @JsonProperty("subtitle")
    private String subtitle;
    @JsonProperty("executionTime")
    private ExecutionTime executionTime;

    @JsonIgnore
    @Override
    public void execute(Player player) {
        if (subtitle.isEmpty())
            player.sendTitle(title.replace("&", "§"), null, 1, 3, 1);
        else
            player.sendTitle(title.replace("&", "§"), subtitle.replace("&", "§"), 1, 3, 1);
    }

    @JsonIgnore
    @Override
    public ExecutionTime getExecutionTime() {
        return executionTime;
    }

    @JsonIgnore
    @Override
    public ActionType getType() {
        return ActionType.TITLE;
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
