package dansplugins.wildpets.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Pins the on-disk behaviour of the usage-reporting block against the real YamlConfiguration
 * and the real bundled config.yml rather than a mock. (WildPets is final, so ConfigService is
 * exercised through its static copy step; the instance method only adds the save.)
 *
 * Wild Pets never calls saveDefaultConfig(); config.yml is written by
 * ConfigService.saveMissingConfigDefaultsIfNotPresent, and only on a first run or a version
 * mismatch. So a server upgraded in place from before usage reporting keeps a config.yml with no
 * usage-reporting block: the one-argument getters still fall through to the bundled key, and
 * copyUsageReportingDefaults puts the block in the file so the switch is visible.
 */
public class UsageReportingConfigTest {

    private static final String BUNDLED_KEY = "ZrZAS0eHFSWjr0BCnlhfEuE_jipOqizNzt3yr5Hvwhg";

    /** What an installation from before usage reporting has on disk. */
    private static final String PRE_EXISTING_FILE = "version: v1.9\nconfigOptions:\n  debugMode: false\n  petLimit: 10\n";

    private YamlConfiguration bundled;

    @Before
    public void setUp() {
        InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml");
        assertNotNull("src/main/resources/config.yml must be on the classpath", stream);
        bundled = YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    private YamlConfiguration loadWithBundledDefaults(String onDisk) {
        // Mirrors JavaPlugin.reloadConfig(): the file's contents, with the jar's config.yml as defaults.
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(onDisk);
        } catch (InvalidConfigurationException e) {
            throw new IllegalStateException(e);
        }
        config.setDefaults(bundled);
        return config;
    }

    private YamlConfiguration reload(String written) {
        YamlConfiguration reloaded = new YamlConfiguration();
        try {
            reloaded.loadFromString(written);
        } catch (InvalidConfigurationException e) {
            throw new IllegalStateException(e);
        }
        return reloaded;
    }

    @Test
    public void bundledConfig_carriesTheUsageReportingBlock() {
        assertTrue(bundled.getBoolean("usage-reporting.enabled"));
        assertEquals("https://trace.danielstephenson.dev", bundled.getString("usage-reporting.endpoint"));
        assertEquals(BUNDLED_KEY, bundled.getString("usage-reporting.key"));
    }

    @Test
    public void oneArgumentGetters_fallThroughToTheBundledKeyWhenTheFileHasNoBlock() {
        YamlConfiguration config = loadWithBundledDefaults(PRE_EXISTING_FILE);

        assertFalse(config.isSet("usage-reporting"));
        assertTrue(config.getBoolean("usage-reporting.enabled"));
        assertEquals(BUNDLED_KEY, config.getString("usage-reporting.key"));
        // and the trap the getters avoid:
        assertEquals("", config.getString("usage-reporting.key", ""));
    }

    @Test
    public void missingBlock_isCopiedFromTheBundledDefaultsAndReportedAsNeedingASave() {
        YamlConfiguration config = loadWithBundledDefaults(PRE_EXISTING_FILE);

        assertTrue(ConfigService.copyUsageReportingDefaults(config));

        // copyDefaults is off here, so what saveToString() emits is exactly what is on disk.
        YamlConfiguration reloaded = reload(config.saveToString());
        assertTrue(reloaded.isSet("usage-reporting"));
        assertTrue(reloaded.getBoolean("usage-reporting.enabled"));
        assertEquals("https://trace.danielstephenson.dev", reloaded.getString("usage-reporting.endpoint"));
        assertEquals(BUNDLED_KEY, reloaded.getString("usage-reporting.key"));
        assertEquals("v1.9", reloaded.getString("version"));
        assertEquals(10, reloaded.getInt("configOptions.petLimit"));
    }

    @Test
    public void existingBlock_isLeftAloneAndNeedsNoSave() {
        YamlConfiguration config = loadWithBundledDefaults(PRE_EXISTING_FILE
                + "usage-reporting:\n  enabled: false\n  endpoint: http://localhost:8080\n  key: abc\n");

        assertFalse(ConfigService.copyUsageReportingDefaults(config));

        assertFalse(config.getBoolean("usage-reporting.enabled"));
        assertEquals("http://localhost:8080", config.getString("usage-reporting.endpoint"));
        assertEquals("abc", config.getString("usage-reporting.key"));
    }

    @Test
    public void block_isNotInventedWithoutBundledDefaults() {
        YamlConfiguration config = new YamlConfiguration();

        assertFalse(ConfigService.copyUsageReportingDefaults(config));

        assertFalse(config.isSet("usage-reporting"));
    }
}
