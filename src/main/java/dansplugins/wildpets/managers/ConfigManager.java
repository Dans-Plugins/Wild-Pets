package dansplugins.wildpets.managers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.objects.EntityConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

/*
    To add a new config option, the following methods must be altered:
    - handleVersionMismatch()
    - setConfigOption()
    - saveConfigDefaults()
    - sendConfigList()
 */

public class ConfigManager {

    private static ConfigManager instance;
    private boolean altered = false;

    private final String configOptionsPrefix = "configOptions.";
    private final String entityConfigurationsPrefix = "entityConfigurations.";

    private ConfigManager() {

    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void saveConfigDefaults() {
        WildPets.getInstance().getConfig().addDefault("version", WildPets.getInstance().getVersion());

        // save config options
        WildPets.getInstance().getConfig().set(configOptionsPrefix + "debugMode", false);
        WildPets.getInstance().getConfig().set(configOptionsPrefix + "petLimit", 10);
        WildPets.getInstance().getConfig().set(configOptionsPrefix + "cancelTamingAfterFailedAttempt", false);
        WildPets.getInstance().getConfig().set(configOptionsPrefix + "rightClickViewCooldown", 3);
        WildPets.getInstance().getConfig().set(configOptionsPrefix + "secondsBetweenStayTeleports", 0.5);
        WildPets.getInstance().getConfig().set(configOptionsPrefix + "rightClickToSelect", false);
        WildPets.getInstance().getConfig().set(configOptionsPrefix + "secondsBetweenSchedulingAttempts", 60);
        WildPets.getInstance().getConfig().set(configOptionsPrefix + "maxScheduleAttempts", 1440);

        // save default entity configuration
        EntityConfig defaultEntityConfig = EntityConfigManager.getInstance().getDefaultConfiguration();
        HashMap<String, String> defaultOptions = new HashMap<>();
        defaultOptions.put("chanceToSucceed", "" + defaultEntityConfig.getChanceToSucceed());
        defaultOptions.put("requiredTamingItem", defaultEntityConfig.getRequiredTamingItem().name());
        defaultOptions.put("tamingItemAmount", "" + defaultEntityConfig.getTamingItemAmount());
        defaultOptions.put("enabled", "" + defaultEntityConfig.isEnabled());
        for (Map.Entry<String, String> entry : defaultOptions.entrySet()) {
            String identifier = entityConfigurationsPrefix + defaultEntityConfig.getType() + "." + entry.getKey();
            WildPets.getInstance().getConfig().set(identifier, entry.getValue());
        }

        // save entity configurations
        for (EntityConfig entityConfig : EntityConfigManager.getInstance().getEntityConfigurations()) {
            HashMap<String, String> options = new HashMap<>();
            options.put("chanceToSucceed", "" + entityConfig.getChanceToSucceed());
            options.put("requiredTamingItem", entityConfig.getRequiredTamingItem().name());
            options.put("tamingItemAmount", "" + entityConfig.getTamingItemAmount());
            options.put("enabled", "" + entityConfig.isEnabled());
            for (Map.Entry<String, String> entry : options.entrySet()) {
                String identifier = entityConfigurationsPrefix + entityConfig.getType() + "." + entry.getKey();
                WildPets.getInstance().getConfig().set(identifier, entry.getValue());
            }
        }
        WildPets.getInstance().getConfig().options().copyDefaults(true);
        WildPets.getInstance().saveConfig();
    }

    public void handleVersionMismatch() {
        // set version
        if (!WildPets.getInstance().getConfig().isString("version")) {
            WildPets.getInstance().getConfig().addDefault("version", WildPets.getInstance().getVersion());
        }
        else {
            WildPets.getInstance().getConfig().set("version", WildPets.getInstance().getVersion());
        }

        // save config options
        if (!WildPets.getInstance().getConfig().isSet(configOptionsPrefix + "debugMode")) {
            WildPets.getInstance().getConfig().set(configOptionsPrefix + "debugMode", false);
        }
        if (!WildPets.getInstance().getConfig().isSet(configOptionsPrefix + "petLimit")) {
            WildPets.getInstance().getConfig().set(configOptionsPrefix + "petLimit", 10);
        }
        if (!WildPets.getInstance().getConfig().isSet(configOptionsPrefix + "cancelTamingAfterFailedAttempt")) {
            WildPets.getInstance().getConfig().set(configOptionsPrefix + "cancelTamingAfterFailedAttempt", false);
        }
        if (!WildPets.getInstance().getConfig().isSet(configOptionsPrefix + "rightClickViewCooldown")) {
            WildPets.getInstance().getConfig().set(configOptionsPrefix + "rightClickViewCooldown", 3);
        }
        if (!WildPets.getInstance().getConfig().isSet(configOptionsPrefix + "secondsBetweenStayTeleports")) {
            WildPets.getInstance().getConfig().set(configOptionsPrefix + "secondsBetweenStayTeleports", 0.5);
        }
        if (!WildPets.getInstance().getConfig().isSet(configOptionsPrefix + "rightClickToSelect")) {
            WildPets.getInstance().getConfig().set(configOptionsPrefix + "rightClickToSelect", true);
        }
        if (!WildPets.getInstance().getConfig().isSet(configOptionsPrefix + "secondsBetweenSchedulingAttempts")) {
            WildPets.getInstance().getConfig().set(configOptionsPrefix + "secondsBetweenSchedulingAttempts", 60);
        }
        if (!WildPets.getInstance().getConfig().isSet(configOptionsPrefix + "maxScheduleAttempts")) {
            WildPets.getInstance().getConfig().set(configOptionsPrefix + "maxScheduleAttempts", 1440);
        }

        // save default entity configuration
        EntityConfig defaultEntityConfig = EntityConfigManager.getInstance().getDefaultConfiguration();
        HashMap<String, String> defaultOptions = new HashMap<>();
        defaultOptions.put("chanceToSucceed", "" + defaultEntityConfig.getChanceToSucceed());
        defaultOptions.put("requiredTamingItem", defaultEntityConfig.getRequiredTamingItem().name());
        defaultOptions.put("tamingItemAmount", "" + defaultEntityConfig.getTamingItemAmount());
        defaultOptions.put("enabled", "" + defaultEntityConfig.isEnabled());
        for (Map.Entry<String, String> entry : defaultOptions.entrySet()) {
            String identifier = entityConfigurationsPrefix + defaultEntityConfig.getType() + "." + entry.getKey();
            if (!WildPets.getInstance().getConfig().isSet(identifier)) {
                if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Adding missing configuration for " + identifier); }
                WildPets.getInstance().getConfig().set(identifier, entry.getValue());
            }
        }

        // save entity configurations
        for (EntityConfig entityConfig : EntityConfigManager.getInstance().getDefaults()) {
            HashMap<String, String> options = new HashMap<>();
            options.put("chanceToSucceed", "" + entityConfig.getChanceToSucceed());
            options.put("requiredTamingItem", entityConfig.getRequiredTamingItem().name());
            options.put("tamingItemAmount", "" + entityConfig.getTamingItemAmount());
            options.put("enabled", "" + entityConfig.isEnabled());
            for (Map.Entry<String, String> entry : options.entrySet()) {
                String identifier = entityConfigurationsPrefix + entityConfig.getType() + "." + entry.getKey();
                if (!WildPets.getInstance().getConfig().isSet(identifier)) {
                    if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Adding missing configuration for " + identifier); }
                    WildPets.getInstance().getConfig().set(identifier, entry.getValue());
                }
            }
        }
        WildPets.getInstance().getConfig().options().copyDefaults(true);
        WildPets.getInstance().saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {

        String prefix = "configOptions.";

        if (WildPets.getInstance().getConfig().isSet(prefix + option)) {

            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("rightClickViewCooldown")
                    || option.equalsIgnoreCase("secondsBetweenSchedulingAttempts")
                    || option.equalsIgnoreCase("maxScheduleAttempts")) {
                WildPets.getInstance().getConfig().set(prefix + option, Integer.parseInt(value));
                sender.sendMessage(ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("debugMode")
                    || option.equalsIgnoreCase("rightClickToSelect")) {
                WildPets.getInstance().getConfig().set(prefix + option, Boolean.parseBoolean(value));
                sender.sendMessage(ChatColor.GREEN + "Boolean set.");
            } else if (option.equalsIgnoreCase("secondsBetweenStayTeleports")) { // no doubles yet
                WildPets.getInstance().getConfig().set(prefix + option, Double.parseDouble(value));
                sender.sendMessage(ChatColor.GREEN + "Double set.");
            } else {
                WildPets.getInstance().getConfig().set(prefix + option, value);
                sender.sendMessage(ChatColor.GREEN + "String set.");
            }

            // save
            WildPets.getInstance().saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    public void sendConfigList(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Config List ===");
        sender.sendMessage(ChatColor.AQUA + "version: " + WildPets.getInstance().getConfig().getString("version")
                + ", debugMode: " + WildPets.getInstance().getConfig().getString(configOptionsPrefix + "debugMode")
                + ", petLimit: " + WildPets.getInstance().getConfig().getString(configOptionsPrefix + "petLimit")
                + ", cancelTamingAfterFailedAttempt: " + WildPets.getInstance().getConfig().getString(configOptionsPrefix + "cancelTamingAfterFailedAttempt")
                + ", rightClickViewCooldown: " + WildPets.getInstance().getConfig().getInt(configOptionsPrefix + "rightClickViewCooldown")
                + ", secondsBetweenStayTeleports: " + WildPets.getInstance().getConfig().getDouble(configOptionsPrefix + "secondsBetweenStayTeleports")
                + ", rightClickToSelect: " + WildPets.getInstance().getConfig().getBoolean(configOptionsPrefix + "rightClickToSelect")
                + ", secondsBetweenSchedulingAttempts: " + WildPets.getInstance().getConfig().getInt(configOptionsPrefix + "secondsBetweenSchedulingAttempts")
                + ", maxScheduleAttempts: " + WildPets.getInstance().getConfig().getInt(configOptionsPrefix + "maxScheduleAttempts"));
        sender.sendMessage(ChatColor.AQUA + "====================");
        sender.sendMessage(ChatColor.AQUA + "Note: Entity configurations are not shown.");
        sender.sendMessage(ChatColor.AQUA + "====================");
    }

    public boolean hasBeenAltered() {
        return altered;
    }

}