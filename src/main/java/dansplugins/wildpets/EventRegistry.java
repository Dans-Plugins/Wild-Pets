package dansplugins.wildpets;

import dansplugins.wildpets.eventhandlers.InteractionHandler;
import dansplugins.wildpets.eventhandlers.JoinHandler;
import org.bukkit.plugin.PluginManager;

public class EventRegistry {

    private static EventRegistry instance;

    private EventRegistry() {

    }

    public static EventRegistry getInstance() {
        if (instance == null) {
            instance = new EventRegistry();
        }
        return instance;
    }

    public void registerEvents() {

        WildPets mainInstance = WildPets.getInstance();
        PluginManager manager = mainInstance.getServer().getPluginManager();

        // event handlers
        manager.registerEvents(new InteractionHandler(), mainInstance);
        manager.registerEvents(new JoinHandler(), mainInstance);
    }

}
