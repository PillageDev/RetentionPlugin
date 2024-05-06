package dev.pillage.retention.rewards;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dev.pillage.retention.rewards.impl.*;
import org.bukkit.entity.Player;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CommandReward.class, name = "command"),
        @JsonSubTypes.Type(value = ItemReward.class, name = "item"),
        @JsonSubTypes.Type(value = MessageReward.class, name = "message"),
        @JsonSubTypes.Type(value = MoneyReward.class, name = "money"),
        @JsonSubTypes.Type(value = TitleReward.class, name = "title")
})
public abstract class RegionReward {
    public abstract RewardType getType();
    public abstract int getTime();
    public abstract void giveReward(Player player);
    public abstract String toJson();

    @Override
    public String toString() {
        return toJson();
    }
}
