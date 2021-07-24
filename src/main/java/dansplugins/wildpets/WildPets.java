package dansplugins.wildpets;

import dansplugins.wildpets.managers.StorageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class WildPets extends JavaPlugin {

    private static WildPets instance;

    public static WildPets getInstance() {
        return instance;
    }

    private final String version = "v0.10";

    @Override
    public void onEnable() {
        instance = this;

        EventRegistry.getInstance().registerEvents();

        StorageManager.getInstance().load();
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
}
