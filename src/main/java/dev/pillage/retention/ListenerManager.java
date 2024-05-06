package dev.pillage.retention;

import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;

public class ListenerManager {

    public void registerListeners() {
        String packageName = "dev.pillage.retention.listeners.*";

        for (Class<?> clazz : new Reflections(packageName).getSubTypesOf(Listener.class)) {
            try {
                Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                Retention.getInstance().getServer().getPluginManager().registerEvents(listener, Retention.getInstance());
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
