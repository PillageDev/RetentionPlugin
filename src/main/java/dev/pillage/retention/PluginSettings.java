package dev.pillage.retention;

import dev.pillage.retention.exceptions.ConfigValueNotFoundException;
import lombok.SneakyThrows;

public enum PluginSettings {
    LOCAL_DB("local-db"),
    REMOTE_SQL_USERNAME("remote-sql.username"),
    REMOTE_SQL_PASSWORD("remote-sql.password"),
    REMOTE_SQL_DB("remote-sql.db"),
    REMOTE_SQL_HOST("remote-sql.host"),
    REMOTE_SQL_PORT("remote-sql.port"),
    WAND_ITEM("wand.material"),
    WAND_NAME("wand.name");

    private final String path;

    PluginSettings(String path) {
        this.path = path;
    }

    @SneakyThrows
    public Object getValue() {
        Object value =  Retention.getInstance().getConfig().get(path);
        if (value == null) {
            throw new ConfigValueNotFoundException(this.path);
        }
        return value;
    }

    public boolean getBoolean()  {
        return (boolean) getValue();
    }

    public String getString()  {
        return (String) getValue();
    }

    public int getInt()  {
        return (int) getValue();
    }
}
