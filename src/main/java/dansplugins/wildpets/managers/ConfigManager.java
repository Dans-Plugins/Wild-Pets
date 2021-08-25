package dansplugins.wildpets.managers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.objects.EntityConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private boolean debug = WildPets.getInstance().isDebugEnabled();

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
        // set version
        if (!WildPets.getInstance().getConfig().isString("version")) {
            WildPets.getInstance().getConfig().addDefault("version", WildPets.getInstance().getVersion());
        }
        else {
            WildPets.getInstance().getConfig().set("version", WildPets.getInstance().getVersion());
        }

        // save config options
        if (!WildPets.getInstance().getConfig().isSet("configOptions." + "debugMode")) {
            WildPets.getInstance().getConfig().set("configOptions." + "debugMode", false);
        }
        if (!WildPets.getInstance().getConfig().isSet("configOptions." + "petLimit")) {
            WildPets.getInstance().getConfig().set("configOptions." + "petLimit", 10);
        }
        if (!WildPets.getInstance().getConfig().isSet("configOptions." + "cancelTamingAfterFailedAttempt")) {
            WildPets.getInstance().getConfig().set("configOptions." + "cancelTamingAfterFailedAttempt", false);
        }

        // save default entity configuration
        EntityConfig defaultEntityConfig = EntityConfigManager.getInstance().getDefaultConfiguration();
        HashMap<String, String> defaultOptions = new HashMap<>();
        defaultOptions.put("chanceToSucceed", "" + defaultEntityConfig.getChanceToSucceed());
        defaultOptions.put("requiredTamingItem", defaultEntityConfig.getRequiredTamingItem().name());
        defaultOptions.put("tamingItemAmount", "" + defaultEntityConfig.getTamingItemAmount());
        defaultOptions.put("enabled", "" + defaultEntityConfig.isEnabled());
        for (Map.Entry<String, String> entry : defaultOptions.entrySet()) {
            String identifier = "entityConfigurations." + defaultEntityConfig.getType() + "." + entry.getKey();
            if (!WildPets.getInstance().getConfig().isSet(identifier)) {
                if (debug) { System.out.println("[DEBUG] Adding missing configuration for " + identifier); }
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
                String identifier = "entityConfigurations." + entityConfig.getType() + "." + entry.getKey();
                if (!WildPets.getInstance().getConfig().isSet(identifier)) {
                    if (debug) { System.out.println("[DEBUG] Adding missing configuration for " + identifier); }
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
            } else if (option.equalsIgnoreCase("initialMaxPowerLevel")) {
                WildPets.getInstance().getConfig().set(prefix + option, Integer.parseInt(value));
                sender.sendMessage(ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("mobsSpawnInFactionTerritory")) {
                WildPets.getInstance().getConfig().set(prefix + option, Boolean.parseBoolean(value));
                sender.sendMessage(ChatColor.GREEN + "Boolean set.");
            } else if (option.equalsIgnoreCase("factionOwnerMultiplier")) {
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

    public void saveConfigDefaults() {
        WildPets.getInstance().getConfig().addDefault("version", WildPets.getInstance().getVersion());

        // save config options
        WildPets.getInstance().getConfig().set("configOptions." + "debugMode", false);
        WildPets.getInstance().getConfig().set("configOptions." + "petLimit", 10);
        WildPets.getInstance().getConfig().set("configOptions." + "cancelTamingAfterFailedAttempt", false);

        // save default entity configuration
        EntityConfig defaultEntityConfig = EntityConfigManager.getInstance().getDefaultConfiguration();
        HashMap<String, String> defaultOptions = new HashMap<>();
        defaultOptions.put("chanceToSucceed", "" + defaultEntityConfig.getChanceToSucceed());
        defaultOptions.put("requiredTamingItem", defaultEntityConfig.getRequiredTamingItem().name());
        defaultOptions.put("tamingItemAmount", "" + defaultEntityConfig.getTamingItemAmount());
        defaultOptions.put("enabled", "" + defaultEntityConfig.isEnabled());
        for (Map.Entry<String, String> entry : defaultOptions.entrySet()) {
            String identifier = "entityConfigurations." + defaultEntityConfig.getType() + "." + entry.getKey();
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
                String identifier = "entityConfigurations." + entityConfig.getType() + "." + entry.getKey();
                WildPets.getInstance().getConfig().set(identifier, entry.getValue());
            }
        }
        WildPets.getInstance().getConfig().options().copyDefaults(true);
        WildPets.getInstance().saveConfig();
    }

    public void sendConfigList(CommandSender sender) {
        String prefix = "configOptions.";

        sender.sendMessage(ChatColor.AQUA + "=== Config List ===");
        sender.sendMessage(ChatColor.AQUA + "version: " + WildPets.getInstance().getConfig().getString("version")
                + ", debugMode: " + WildPets.getInstance().getConfig().getString(prefix + "debugMode")
                + ", petLimit: " + WildPets.getInstance().getConfig().getString(prefix + "petLimit")
                + ", cancelTamingAfterFailedAttempt: " + WildPets.getInstance().getConfig().getString(prefix + "cancelTamingAfterFailedAttempt"));
        sender.sendMessage(ChatColor.AQUA + "====================");
        sender.sendMessage(ChatColor.AQUA + "Note: Entity configurations are not shown.");
        sender.sendMessage(ChatColor.AQUA + "====================");
    }

    public boolean hasBeenAltered() {
        return altered;
    }

}