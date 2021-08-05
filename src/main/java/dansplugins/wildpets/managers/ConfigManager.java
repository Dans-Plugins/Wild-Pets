package dansplugins.wildpets.managers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.objects.EntityConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

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
        WildPets.getInstance().getConfig().addDefault("version", WildPets.getInstance().getVersion());

        // save config options
        if (!WildPets.getInstance().getConfig().isSet("configOptions." + "petLimit")) {
            WildPets.getInstance().getConfig().set("configOptions." + "petLimit", 10);
        }

        // save default entity configuration
        EntityConfig defaultEntityConfig = EntityConfigManager.getInstance().getDefaultConfiguration();
        HashMap<String, String> defaultOptions = new HashMap<>();
        defaultOptions.put("chanceToSucceed", "" + defaultEntityConfig.getChanceToSucceed());
        defaultOptions.put("requiredTamingItem", defaultEntityConfig.getRequiredTamingItem().name());
        defaultOptions.put("tamingItemAmount", "" + defaultEntityConfig.getTamingItemAmount());
        defaultOptions.put("enabled", "" + defaultEntityConfig.isEnabled());
        for (Map.Entry<String, String> entry : defaultOptions.entrySet()) {
            if (!WildPets.getInstance().getConfig().isSet("entityConfigurations." + defaultEntityConfig.getType() + "." + entry.getKey())) {
                WildPets.getInstance().getConfig().set("entityConfigurations." + defaultEntityConfig.getType() + "." + entry.getKey(), entry.getValue());
            }
        }

        // save entity configurations
        for (EntityConfig entityConfig : EntityConfigManager.getInstance().getEntityConfigurations()) {
            HashMap<String, String> options = new HashMap<>();
            options.put("chanceToSucceed", "" + entityConfig.getChanceToSucceed());
            options.put("requiredTamingItem", entityConfig.getRequiredTamingItem().name());
            options.put("tamingItemAmount", "" + entityConfig.getTamingItemAmount());
            options.put("enabled", "" + entityConfig.isEnabled());
            for (Map.Entry<String, String> entry : options.entrySet()) {
                if (!WildPets.getInstance().getConfig().isSet("entityConfigurations." + defaultEntityConfig.getType() + "." + entry.getKey())) {
                    WildPets.getInstance().getConfig().set("entityConfigurations." + entityConfig.getType() + "." + entry.getKey(), entry.getValue());
                }
            }
        }
        WildPets.getInstance().getConfig().options().copyDefaults(true);
        WildPets.getInstance().saveConfig();
    }
/*
    public void setConfigOption(String option, String value, CommandSender sender) {
        // TODO

        altered = true;
    }
*/
    public void saveConfigDefaults() {
        WildPets.getInstance().getConfig().addDefault("version", WildPets.getInstance().getVersion());

        // save config options
        WildPets.getInstance().getConfig().set("configOptions." + "petLimit", 10);

        // save default entity configuration
        EntityConfig defaultEntityConfig = EntityConfigManager.getInstance().getDefaultConfiguration();
        HashMap<String, String> defaultOptions = new HashMap<>();
        defaultOptions.put("chanceToSucceed", "" + defaultEntityConfig.getChanceToSucceed());
        defaultOptions.put("requiredTamingItem", defaultEntityConfig.getRequiredTamingItem().name());
        defaultOptions.put("tamingItemAmount", "" + defaultEntityConfig.getTamingItemAmount());
        defaultOptions.put("enabled", "" + defaultEntityConfig.isEnabled());
        for (Map.Entry<String, String> entry : defaultOptions.entrySet()) {
            WildPets.getInstance().getConfig().set("entityConfigurations." + defaultEntityConfig.getType() + "." + entry.getKey(), entry.getValue());
        }

        // save entity configurations
        for (EntityConfig entityConfig : EntityConfigManager.getInstance().getEntityConfigurations()) {
            HashMap<String, String> options = new HashMap<>();
            options.put("chanceToSucceed", "" + entityConfig.getChanceToSucceed());
            options.put("requiredTamingItem", entityConfig.getRequiredTamingItem().name());
            options.put("tamingItemAmount", "" + entityConfig.getTamingItemAmount());
            options.put("enabled", "" + entityConfig.isEnabled());
            for (Map.Entry<String, String> entry : options.entrySet()) {
                WildPets.getInstance().getConfig().set("entityConfigurations." + entityConfig.getType() + "." + entry.getKey(), entry.getValue());
            }
        }
        WildPets.getInstance().getConfig().options().copyDefaults(true);
        WildPets.getInstance().saveConfig();
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