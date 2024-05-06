package dev.pillage.retention;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.Set;

public class CommandManager extends BukkitCommandManager {

    public CommandManager(Plugin plugin) {
        super(plugin);
    }

    public void registerCommands() {
        try {
            for (Class<?> clazz : getAllCommands()) {
                registerCommand((BaseCommand) clazz.newInstance());
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Class<? extends BaseCommand>> getAllCommands()  {
        Reflections reflections = new Reflections("dev.pillage.retention.commands.*");
        return reflections.getSubTypesOf(BaseCommand.class);
    }
}
