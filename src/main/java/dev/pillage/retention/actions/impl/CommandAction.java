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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
public class CommandAction extends Action {

    public CommandAction(String command, ExecutionTime executionTime) {
        this.command = command;
        this.executionTime = executionTime;
    }

    @JsonProperty("type")
    private String type = "command";
    @JsonProperty("command")
    private String command;
    @JsonProperty("executionTime")
    private ExecutionTime executionTime;

    @JsonIgnore
    @Override
    public void execute(Player player) {
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), prepareCommand(player));
    }

    @JsonIgnore
    @Override
    public ExecutionTime getExecutionTime() {
        return executionTime;
    }

    @JsonIgnore
    @Override
    public ActionType getType() {
        return ActionType.COMMAND;
    }

    @JsonIgnore
    private String prepareCommand(Player player) {
        String preparedCommand = command;
        if (preparedCommand.startsWith("/")) {
            preparedCommand = preparedCommand.substring(1);
        }
        if (preparedCommand.contains("{player}")) {
            preparedCommand = preparedCommand.replace("{player}", player.getName());
        }
        return preparedCommand;
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
