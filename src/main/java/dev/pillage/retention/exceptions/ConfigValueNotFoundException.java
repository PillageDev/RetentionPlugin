package dev.pillage.retention.exceptions;

public class ConfigValueNotFoundException extends Exception {

    public ConfigValueNotFoundException(String configValue) {
        super("Config value not found at path " + configValue + "! Please check your config file and try again. If you need further help, please consult the documentation or contact us.");
    }
}
