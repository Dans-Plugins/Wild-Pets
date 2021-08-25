package dansplugins.wildpets;

import dansplugins.wildpets.bstats.Metrics;
import dansplugins.wildpets.managers.ConfigManager;
import dansplugins.wildpets.managers.EntityConfigManager;
import dansplugins.wildpets.managers.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class WildPets extends JavaPlugin {

    private static WildPets instance;

    public static WildPets getInstance() {
        return instance;
    }

    private final String version = "v0.22";

    @Override
    public void onEnable() {
        instance = this;

        // create/load config
        if (!(new File("./plugins/WildPets/config.yml").exists())) {
            EntityConfigManager.getInstance().initializeWithDefaults();
            ConfigManager.getInstance().saveConfigDefaults();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                ConfigManager.getInstance().handleVersionMismatch();
            }
            reloadConfig();
            EntityConfigManager.getInstance().initializeWithConfig();
        }

        EventRegistry.getInstance().registerEvents();

        StorageManager.getInstance().load();

        // bStats
        int pluginId = 12332;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        StorageManager.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        CommandInterpreter commandInterpreter = new CommandInterpreter();
        return commandInterpreter.interpretCommand(sender, label, args);
    }

    public String getVersion() {
        return version;
    }

    public boolean isDebugEnabled() {
        return getConfig().getBoolean("debugMode");
    }

    private boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }
}