package dev.pillage.retention.cache.entities;

import co.aikar.idb.DB;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.pillage.retention.Retention;
import dev.pillage.retention.actions.Action;
import dev.pillage.retention.cache.RegionCache;
import dev.pillage.retention.rewards.RegionReward;
import dev.pillage.retention.utils.Cuboid;
import lombok.*;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Region {

    public Region(Cuboid cuboid, String name) {
        this.x1 = cuboid.getLowerX();
        this.x2 = cuboid.getUpperX();
        this.y1 = cuboid.getLowerY();
        this.y2 = cuboid.getUpperY();
        this.z1 = cuboid.getLowerZ();
        this.z2 = cuboid.getUpperZ();
        this.worldName = cuboid.getWorld().getName();
        this.name = name;
        this.id = generateId();
    }

    @JsonProperty("id")
    private int id;
    @JsonProperty("x1")
    private int x1;
    @JsonProperty("y1")
    private int y1;
    @JsonProperty("z1")
    private int z1;
    @JsonProperty("x2")
    private int x2;
    @JsonProperty("y2")
    private int y2;
    @JsonProperty("z2")
    private int z2;
    @JsonProperty("worldName")
    private String worldName;
    @JsonProperty("name")
    private String name;
    @JsonProperty("rewards")
    private List<RegionReward> rewards;
    @JsonProperty("actions")
    private List<Action> actions;

    @JsonIgnore
    public Cuboid getCuboid() {
        return new Cuboid(Retention.getInstance().getServer().getWorld(worldName), x1, y1, z1, x2, y2, z2);
    }

    public void addReward(RegionReward reward) {
        rewards.add(reward);
    }

    public void removeReward(RegionReward reward) {
        rewards.remove(reward);
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public void removeAction(Action action) {
        actions.remove(action);
    }

    public boolean playerInCuboid(Player player) {
        return getCuboid().contains(player.getLocation());
    }

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public static Region fromString(String data) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            return mapper.readValue(data, Region.class);
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

        DB.executeUpdate("INSERT OR REPLACE INTO regions (id, data) VALUES (?, ?)", id, this.toString());
        RegionCache.updateRegion(id, this);
    }

    @SneakyThrows
    public static int generateId() {
        String idString = DB.getFirstRow("SELECT MAX(id) FROM regions").getString("MAX(id)", "0");
        return Integer.parseInt(idString) + 1;
    }

}
