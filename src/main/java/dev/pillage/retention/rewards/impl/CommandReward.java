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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
public class CommandReward extends RegionReward {

    public CommandReward(String command, int seconds) {
        this.command = command;
        this.seconds = seconds;
    }

    @JsonProperty("type")
    private String type = "command";
    @JsonProperty("command")
    private String command;
    @JsonProperty("seconds")
    private int seconds;

    @JsonIgnore
    @Override
    public void giveReward(Player player) {
        Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), prepareCommand(player));
    }

    @JsonIgnore
    @Override
    public int getTime() {
        return seconds;
    }

    @JsonIgnore
    @Override
    public RewardType getType() {
        return RewardType.COMMAND;
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
