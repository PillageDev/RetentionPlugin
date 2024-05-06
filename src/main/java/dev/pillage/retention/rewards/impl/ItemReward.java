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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ItemReward extends RegionReward {

    public ItemReward(String name, Material material, int amount, int seconds, List<String> lore) {
        this.name = name;
        this.material = material;
        this.amount = amount;
        this.seconds = seconds;
        this.lore = lore;
    }

    @JsonProperty("type")
    private String type = "item";
    @JsonProperty("name")
    private String name;
    @JsonProperty("material")
    private Material material;
    @JsonProperty("amount")
    private int amount;
    @JsonProperty("seconds")
    private int seconds;
    @JsonProperty("lore")
    private List<String> lore;

    @JsonIgnore
    @Override
    public void giveReward(Player player) {
        ItemStack item = ItemBuilder.from(material)
                .setName(name)
                .setLore(lore)
                .setAmount(amount)
                .asGuiItem().getItemStack();

        player.getInventory().addItem(item);
    }

    @JsonIgnore
    @Override
    public int getTime() {
        return seconds;
    }

    @JsonIgnore
    @Override
    public RewardType getType() {
        return RewardType.ITEM;
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
