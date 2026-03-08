package dansplugins.wildpets.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.List;

/**
 * Utility class for registering event handlers.
 */
public class EventHandlerRegistry {
    private final PluginManager manager;

    public EventHandlerRegistry() {
        this.manager = Bukkit.getPluginManager();
    }

    /**
     * Method to register multiple listeners for a given plugin.
     *
     * @param listeners to register.
     * @param plugin to register it for.
     * @throws IllegalArgumentException if the arguments are invalid.
     */
    public void registerEventHandlers(List<Listener> listeners, Plugin plugin) {
        if (listeners == null || listeners.isEmpty()) {
            throw new IllegalArgumentException("Listeners cannot be null or empty!");
        }
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null!");
        }
        listeners.forEach(listener -> manager.registerEvents(listener, plugin));
    }
}
