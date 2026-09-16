package dansplugins.wildpets.config;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.utils.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
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

    private static final String USAGE_REPORTING_SECTION = "usage-reporting";
    private static final String USAGE_REPORTING_ENABLED_KEY = "usage-reporting.enabled";
    private static final String USAGE_REPORTING_ENDPOINT_KEY = "usage-reporting.endpoint";
    private static final String USAGE_REPORTING_KEY_KEY = "usage-reporting.key";
    private static final String DEFAULT_USAGE_REPORTING_ENDPOINT = "https://trace.danielstephenson.dev";

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
        if (!getConfig().isSet(configOptionsPrefix + "damageFromPetsEnabled")) {
            getConfig().set(configOptionsPrefix + "damageFromPetsEnabled", false);
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
                MessageFormat.sendSuccessBox(sender, "Config", ChatColor.GREEN + "Integer set.");
            } else if (option.equalsIgnoreCase("debugMode")
                    || option.equalsIgnoreCase("rightClickToSelect")
                    || option.equalsIgnoreCase("preventMountingLockedPets")
                    || option.equalsIgnoreCase("damageToPetsEnabled")
                    || option.equalsIgnoreCase("showLineageInfo")
                    || option.equalsIgnoreCase("bornPetsEnabled")
                    || option.equalsIgnoreCase("damageFromPetsEnabled")) {
                getConfig().set(configOptionsPrefix + option, Boolean.parseBoolean(value));
                MessageFormat.sendSuccessBox(sender, "Config", ChatColor.GREEN + "Boolean set.");
            } else if (option.equalsIgnoreCase("C")) { // no doubles yet
                getConfig().set(configOptionsPrefix + option, Double.parseDouble(value));
                MessageFormat.sendSuccessBox(sender, "Config", ChatColor.GREEN + "Double set.");
            } else {
                getConfig().set(configOptionsPrefix + option, value);
                MessageFormat.sendSuccessBox(sender, "Config", ChatColor.GREEN + "String set.");
            }

            // save
            wildPets.saveConfig();
            altered = true;
        } else {
            sender.sendMessage(ChatColor.RED + "That config option wasn't found.");
        }
    }

    public void sendConfigList(CommandSender sender) {
        String[][] configEntries = {
            {"version", getConfig().getString("version")},
            {"debugMode", getString("debugMode")},
            {"petLimit", getString("petLimit")},
            {"cancelTamingAfterFailedAttempt", getString("cancelTamingAfterFailedAttempt")},
            {"rightClickViewCooldown", String.valueOf(getInt("rightClickViewCooldown"))},
            {"rightClickToSelect", String.valueOf(getBoolean("rightClickToSelect"))},
            {"maxScheduleAttempts", String.valueOf(getInt("maxScheduleAttempts"))},
            {"petNameCharacterLimit", String.valueOf(getInt("petNameCharacterLimit"))},
            {"preventMountingLockedPets", String.valueOf(getBoolean("preventMountingLockedPets"))},
            {"damageToPetsEnabled", String.valueOf(getBoolean("damageToPetsEnabled"))},
            {"showLineageInfo", String.valueOf(getBoolean("showLineageInfo"))},
            {"bornPetsEnabled", String.valueOf(getBoolean("bornPetsEnabled"))},
            {"damageFromPetsEnabled", String.valueOf(getBoolean("damageFromPetsEnabled"))}
        };

        // Compute max key length for alignment
        int maxKeyLength = 0;
        for (String[] entry : configEntries) {
            if (entry[0].length() > maxKeyLength) {
                maxKeyLength = entry[0].length();
            }
        }
        // Add space for colon and trailing space
        int columnWidth = maxKeyLength + 2;

        sender.sendMessage("");
        sender.sendMessage(MessageFormat.header("Wild Pets", "Config"));
        for (String[] entry : configEntries) {
            String label = entry[0] + ":";
            String padding = String.format("%-" + (columnWidth - label.length()) + "s", "");
            sender.sendMessage(MessageFormat.line(ChatColor.GRAY + label + padding + ChatColor.WHITE + entry[1]));
        }
        sender.sendMessage(MessageFormat.line(ChatColor.GRAY + "Note: Entity configurations are not shown."));
        sender.sendMessage(MessageFormat.footer());
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

    /**
     * Puts the usage-reporting block on disk if the file does not have one.
     * saveMissingConfigDefaultsIfNotPresent only rewrites config.yml on a first
     * run or a version mismatch, so a server upgraded in place from before usage
     * reporting kept reporting through the bundled defaults (see the getters
     * below) with no visible switch to turn it off. The values are copied from
     * the jar's config.yml, not written as new literals, so the key and endpoint
     * stay defined in one place.
     */
    public void saveUsageReportingDefaultsIfNotPresent() {
        if (copyUsageReportingDefaults(getConfig())) {
            wildPets.saveConfig();
        }
    }

    /**
     * Copies the three usage-reporting values from the configuration's defaults
     * into the configuration itself when it has no usage-reporting block.
     * @return whether anything was copied, i.e. whether the file needs saving.
     */
    static boolean copyUsageReportingDefaults(FileConfiguration config) {
        Configuration defaults = config.getDefaults();
        if (defaults == null || config.isSet(USAGE_REPORTING_SECTION)) {
            return false;
        }
        config.set(USAGE_REPORTING_ENABLED_KEY, defaults.get(USAGE_REPORTING_ENABLED_KEY));
        config.set(USAGE_REPORTING_ENDPOINT_KEY, defaults.get(USAGE_REPORTING_ENDPOINT_KEY));
        config.set(USAGE_REPORTING_KEY_KEY, defaults.get(USAGE_REPORTING_KEY_KEY));
        return true;
    }

    // The usage-reporting block lives outside configOptions and is not managed by
    // /wp config; it is read straight from the config with the one-argument
    // getters, deliberately. saveMissingConfigDefaultsIfNotPresent() only writes
    // the on-disk config on first run and on a version change, so a server
    // upgraded from a version before usage reporting has no usage-reporting
    // block on disk until then. Bukkit registers the jar's config.yml as the
    // defaults for that file, and the one-argument getters fall through to
    // them -- but the two-argument getters return their explicit fallback
    // instead, which for the key would be "" and would turn reporting off on
    // every existing installation. Verified against YamlConfiguration, not
    // assumed.

    public boolean isUsageReportingEnabled() {
        return getConfig().getBoolean(USAGE_REPORTING_ENABLED_KEY);
    }

    public String getUsageReportingEndpoint() {
        String endpoint = getConfig().getString(USAGE_REPORTING_ENDPOINT_KEY);
        return endpoint != null ? endpoint : DEFAULT_USAGE_REPORTING_ENDPOINT;
    }

    /** Empty when no key is configured or bundled, which the client treats as "off". */
    public String getUsageReportingKey() {
        String key = getConfig().getString(USAGE_REPORTING_KEY_KEY);
        return key != null ? key : "";
    }
}