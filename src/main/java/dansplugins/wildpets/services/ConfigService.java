package dansplugins.wildpets.services;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.objects.EntityConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Daniel McCoy Stephenson
 *     To add a new config option, the following methods must be altered:
 *     - handleVersionMismatch()
 *     - setConfigOption()
 *     - saveConfigDefaults()
 *     - sendConfigList()
 */
public class ConfigService {
    private final WildPets wildPets;
    private final EntityConfigService entityConfigService;
    
    private boolean altered = false;

    private final String configOptionsPrefix = "configOptions.";
    private final String entityConfigurationsPrefix = "entityConfigurations.";

    public ConfigService(WildPets wildPets, EntityConfigService entityConfigService) {
        this.wildPets = wildPets;
        this.entityConfigService = entityConfigService;
    }

    public void saveMissingConfigDefaultsIfNotPresent() {
        // set version
        if (!getConfig().isString("version")) {
            getConfig().addDefault("version", wildPets.getVersion());
        }
        else {
            getConfig().set("version", wildPets.getVersion());
        }

        // save config options
        if (!getConfig().isSet(configOptionsPrefix + "debugMode")) {
            getConfig().set(configOptionsPrefix + "debugMode", false);
        }
        if (!getConfig().isSet(configOptionsPrefix + "petLimit")) {
            getConfig().set(configOptionsPrefix + "petLimit", 10);
        }
        if (!getConfig().isSet(configOptionsPrefix + "cancelTamingAfterFailedAttempt")) {
            getConfig().set(configOptionsPrefix + "cancelTamingAfterFailedAttempt", false);
        }
        if (!getConfig().isSet(configOptionsPrefix + "rightClickViewCooldown")) {
            getConfig().set(configOptionsPrefix + "rightClickViewCooldown", 3);
        }
        if (!getConfig().isSet(configOptionsPrefix + "rightClickToSelect")) {
            getConfig().set(configOptionsPrefix + "rightClickToSelect", true);
        }
        if (!getConfig().isSet(configOptionsPrefix + "maxScheduleAttempts")) {
            getConfig().set(configOptionsPrefix + "maxScheduleAttempts", 1440);
        }
        if (!getConfig().isSet(configOptionsPrefix + "petNameCharacterLimit")) {
            getConfig().set(configOptionsPrefix + "petNameCharacterLimit", 20);
        }
        if (!getConfig().isSet(configOptionsPrefix + "preventMountingLockedPets")) {
            getConfig().set(configOptionsPrefix + "preventMountingLockedPets", true);
        }
        if (!getConfig().isSet(configOptionsPrefix + "damageToPetsEnabled")) {
            getConfig().set(configOptionsPrefix + "damageToPetsEnabled", false);
        }
        if (!getConfig().isSet(configOptionsPrefix + "showLineageInfo")) {
            getConfig().set(configOptionsPrefix + "showLineageInfo", true);
        }
        if (!getConfig().isSet(configOptionsPrefix + "bornPetsEnabled")) {
            getConfig().set(configOptionsPrefix + "bornPetsEnabled", true);
        }

        // save default entity configuration
        EntityConfig defaultEntityConfig = entityConfigService.getDefaultConfiguration();
        HashMap<String, String> defaultOptions = new HashMap<>();
        defaultOptions.put("chanceToSucceed", "" + defaultEntityConfig.getChanceToSucceed());
        defaultOptions.put("requiredTamingItem", defaultEntityConfig.getRequiredTamingItem().name());
        defaultOptions.put("tamingItemAmount", "" + defaultEntityConfig.getTamingItemAmount());
        defaultOptions.put("enabled", "" + defaultEntityConfig.isEnabled());
        for (Map.Entry<String, String> entry : defaultOptions.entrySet()) {
            String identifier = entityConfigurationsPrefix + defaultEntityConfig.getType() + "." + entry.getKey();
            if (!getConfig().isSet(identifier)) {
                if (wildPets.isDebugEnabled()) { System.out.println("[DEBUG] Adding missing configuration for " + identifier); }
                getConfig().set(identifier, entry.getValue());
            }
        }

        // save entity configurations
        for (EntityConfig entityConfig : entityConfigService.getDefaults()) {
            HashMap<String, String> options = new HashMap<>();
            options.put("chanceToSucceed", "" + entityConfig.getChanceToSucceed());
            options.put("requiredTamingItem", entityConfig.getRequiredTamingItem().name());
            options.put("tamingItemAmount", "" + entityConfig.getTamingItemAmount());
            options.put("enabled", "" + entityConfig.isEnabled());
            for (Map.Entry<String, String> entry : options.entrySet()) {
                String identifier = entityConfigurationsPrefix + entityConfig.getType() + "." + entry.getKey();
                if (!getConfig().isSet(identifier)) {
                    if (wildPets.isDebugEnabled()) { System.out.println("[DEBUG] Adding missing configuration for " + identifier); }
                    getConfig().set(identifier, entry.getValue());
                }
            }
        }
        getConfig().options().copyDefaults(true);
        wildPets.saveConfig();
    }

    public void setConfigOption(String option, String value, CommandSender sender) {

        if (getConfig().isSet(configOptionsPrefix + option)) {

            if (option.equalsIgnoreCase("version")) {
                sender.sendMessage(ChatColor.RED + "Cannot set version.");
                return;
            } else if (option.equalsIgnoreCase("rightClickViewCooldown")
                    || option.equalsIgnoreCase("maxScheduleAttempts")
                    || option.equalsIgnoreCase("petNameCharacterLimit")
                    || option.equalsIgnoreCase("petLimit")) {
                getConfig().set(configOptionsPrefix + option, Integer.parseInt(value));
                sender.sendMessage(ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("debugMode")
                    || option.equalsIgnoreCase("rightClickToSelect")
                    || option.equalsIgnoreCase("preventMountingLockedPets")
                    || option.equalsIgnoreCase("damageToPetsEnabled")
                    || option.equalsIgnoreCase("showLineageInfo")
                    || option.equalsIgnoreCase("bornPetsEnabled")) {
                getConfig().set(configOptionsPrefix + option, Boolean.parseBoolean(value));
                sender.sendMessage(ChatColor.GREEN + "Boolean set.");
            } else if (option.equalsIgnoreCase("C")) { // no doubles yet
                getConfig().set(configOptionsPrefix + option, Double.parseDouble(value));
                sender.sendMessage(ChatColor.GREEN + "Double set.");
            } else {
                getConfig().set(configOptionsPrefix + option, value);
                sender.sendMessage(ChatColor.GREEN + "String set.");
            }

            // save
            wildPets.saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    public void sendConfigList(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== Config List ===");
        sender.sendMessage(ChatColor.AQUA + "version: " + getConfig().getString("version")
                + ", debugMode: " + getString("debugMode")
                + ", petLimit: " + getString("petLimit")
                + ", cancelTamingAfterFailedAttempt: " + getString("cancelTamingAfterFailedAttempt")
                + ", rightClickViewCooldown: " + getInt("rightClickViewCooldown")
                + ", rightClickToSelect: " + getBoolean("rightClickToSelect")
                + ", secondsBetweenSchedulingAttempts: " + getInt("secondsBetweenSchedulingAttempts")
                + ", maxScheduleAttempts: " + getInt("maxScheduleAttempts")
                + ", petNameCharacterLimit: " + getInt("petNameCharacterLimit")
                + ", preventMountingLockedPets: " + getInt("preventMountingLockedPets")
                + ", damageToPetsEnabled: " + getBoolean("damageToPetsEnabled")
                + ", showLineageInfo: " + getBoolean("bornPetsEnabled")
                + ", bornPetsEnabled: " + getBoolean("bornPetsEnabled"));
        sender.sendMessage(ChatColor.AQUA + "====================");
        sender.sendMessage(ChatColor.AQUA + "Note: Entity configurations are not shown.");
        sender.sendMessage(ChatColor.AQUA + "====================");
    }

    public boolean hasBeenAltered() {
        return altered;
    }
    
    public FileConfiguration getConfig() {
        return wildPets.getConfig();
    }

    public int getInt(String option) {
        return getConfig().getInt(configOptionsPrefix + option);
    }

    public boolean getBoolean(String option) {
        return getConfig().getBoolean(configOptionsPrefix + option);
    }

    public double getDouble(String option) {
        return getConfig().getDouble(configOptionsPrefix + option);
    }

    public String getString(String option) {
        return getConfig().getString(configOptionsPrefix + option);
    }
}