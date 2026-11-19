package dansplugins.wildpets.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import dansplugins.wildpets.trace.UsageReportingSetting;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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

    // An operator who turns usage reporting off must find it off, every time.

    @Test
    public void aQuotedFalse_turnsReportingOff() {
        YamlConfiguration config = loadWithBundledDefaults(PRE_EXISTING_FILE + "usage-reporting:\n  enabled: \"false\"\n");
        // getBoolean() returns the bundled default, true, for the string "false".
        assertTrue(config.getBoolean("usage-reporting.enabled"));

        assertFalse(ConfigService.isUsageReportingEnabled(config, null));
    }

    @Test
    public void theBundledDefault_isOnWhenTheFileHasNoBlock() throws Exception {
        File file = onDisk(PRE_EXISTING_FILE);

        assertTrue(ConfigService.isUsageReportingEnabled(loadWithBundledDefaults(PRE_EXISTING_FILE), file));
    }

    @Test
    public void anOptOutWrittenByHandWhileRunning_winsOverTheLoadedConfigAndSurvivesASave() throws Exception {
        String before = PRE_EXISTING_FILE + "usage-reporting:\n  enabled: true\n";
        YamlConfiguration loaded = loadWithBundledDefaults(before);
        File file = onDisk(PRE_EXISTING_FILE + "usage-reporting:\n  enabled: false\n");

        assertFalse(ConfigService.isUsageReportingEnabled(loaded, file));

        // /wp config set writes the loaded (stale) config back through WildPets.saveConfig().
        assertTrue(ConfigService.applyConfigOption(loaded, "petLimit", "5", mock(CommandSender.class)));
        assertTrue(UsageReportingSetting.prepareToSave(loaded, file, null));
        loaded.save(file);

        YamlConfiguration saved = YamlConfiguration.loadConfiguration(file);
        assertEquals(Boolean.FALSE, saved.get("usage-reporting.enabled"));
        assertEquals(5, saved.getInt("configOptions.petLimit"));
    }

    @Test
    public void aFileThatCannotBeParsed_isNotOverwrittenByTheUpgradeStepAndReportingIsOff() throws Exception {
        String broken = "usage-reporting:\n  enabled: false\n\tconfigOptions: [\n";
        File file = onDisk(broken);
        // Bukkit loads such a file as empty, so the block is found missing and copied in as true.
        YamlConfiguration loaded = loadWithBundledDefaults("");
        assertTrue(ConfigService.copyUsageReportingDefaults(loaded));

        assertFalse(UsageReportingSetting.prepareToSave(loaded, file, null));
        assertFalse(ConfigService.isUsageReportingEnabled(loaded, file));
        assertEquals(broken, new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
    }

    private File onDisk(String contents) throws Exception {
        File file = new File(folder.getRoot(), "config.yml");
        Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
        return file;
    }
}
