package dansplugins.wildpets;

import org.bukkit.plugin.java.JavaPlugin;

public final class WildPets extends JavaPlugin {

    private static WildPets instance;

    public static WildPets getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        EventRegistry.getInstance().registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
