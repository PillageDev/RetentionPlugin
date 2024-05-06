package dev.pillage.retention;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public enum Messages {

    TEST("");

    private final String messageId;
    private final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(Retention.getInstance().getDataFolder() + "/messages.yml"));

    Messages(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return config.getString(messageId);
    }
}
