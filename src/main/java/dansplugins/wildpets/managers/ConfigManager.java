package dansplugins.wildpets.managers;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ConfigManager {

    private static ConfigManager instance;
    private boolean altered = false;

    private ConfigManager() {

    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void handleVersionMismatch() {
        // TODO
    }

    public void setConfigOption(String option, String value, CommandSender sender) {
        // TODO

        altered = true;
    }

    public void saveConfigDefaults() {
        // TODO
    }

    public void sendConfigList(CommandSender sender) {
        // TODO: send title
        sender.sendMessage(ChatColor.AQUA + "Note: Entity configurations are not shown.");
        // TODO: send config options excluding entity configurations
    }

    public boolean hasBeenAltered() {
        return altered;
    }

}
