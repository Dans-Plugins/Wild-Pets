package dansplugins.wildpets.trace;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * An operator who turns usage reporting off must find it off, every time: these cover each way the
 * opt-out used to be lost (a quoted "false", a stale loaded config saved over a hand edit, and a
 * config.yml Bukkit could not parse being replaced by the bundled defaults).
 */
public class UsageReportingSettingTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File configFile(String contents) throws Exception {
        File file = new File(folder.getRoot(), "config.yml");
        Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    /** What JavaPlugin.getConfig() loads: the file, with the jar's config.yml as defaults. */
    private YamlConfiguration loadLikeBukkit(String contents) throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        config.loadFromString(contents);
        config.setDefaults(bundled());
        return config;
    }

    private static YamlConfiguration bundled() throws Exception {
        try (InputStreamReader jar = new InputStreamReader(
                UsageReportingSettingTest.class.getResourceAsStream("/config.yml"), StandardCharsets.UTF_8)) {
            return YamlConfiguration.loadConfiguration(jar);
        }
    }

    private static String read(File file) throws Exception {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    @Test
    public void theBundledDefaultIsOn() throws Exception {
        // Otherwise the tests below that rely on the default being true would prove nothing.
        assertEquals(Boolean.TRUE, bundled().get(UsageReportingSetting.ENABLED_KEY));
    }

    @Test
    public void aQuotedFalseTurnsReportingOff() throws Exception {
        String contents = "usage-reporting:\n  enabled: \"false\"\n";
        File file = configFile(contents);
        YamlConfiguration loaded = loadLikeBukkit(contents);
        // getBoolean() returns the bundled default, true, for the string "false".
        assertTrue(loaded.getBoolean(UsageReportingSetting.ENABLED_KEY));

        assertFalse(UsageReportingSetting.isEnabled(loaded, file));
        assertTrue(UsageReportingSetting.turnedOffOnDisk(file));
    }

    @Test
    public void anUnrecognisedValueTurnsReportingOff() throws Exception {
        String contents = "usage-reporting:\n  enabled: disabled\n";
        assertFalse(UsageReportingSetting.isEnabled(loadLikeBukkit(contents), configFile(contents)));
    }

    @Test
    public void trueIsOn() throws Exception {
        String contents = "usage-reporting:\n  enabled: true\n";
        assertTrue(UsageReportingSetting.isEnabled(loadLikeBukkit(contents), configFile(contents)));
    }

    @Test
    public void aMissingBlockOrFileIsNotAnOptOut() throws Exception {
        String contents = "version: 1\n";
        File file = configFile(contents);
        assertNull(UsageReportingSetting.readFromDisk(file));
        assertFalse(UsageReportingSetting.turnedOffOnDisk(file));
        assertTrue(UsageReportingSetting.isEnabled(loadLikeBukkit(contents), file));

        File absent = new File(folder.getRoot(), "absent.yml");
        assertNull(UsageReportingSetting.readFromDisk(absent));
        assertFalse(UsageReportingSetting.isUnparseable(absent));
        assertTrue(UsageReportingSetting.prepareToSave(loadLikeBukkit(""), absent, null));
    }

    @Test
    public void aStaleLoadedConfigCannotOverwriteAnOptOutMadeWhileRunning() throws Exception {
        String before = "usage-reporting:\n  enabled: true\n";
        File file = configFile(before);
        YamlConfiguration loaded = loadLikeBukkit(before);

        // The operator turns reporting off by hand while the server runs.
        configFile("usage-reporting:\n  enabled: false\n");
        assertFalse(UsageReportingSetting.isEnabled(loaded, file));

        // Something later saves the loaded (stale) config.
        assertTrue(UsageReportingSetting.prepareToSave(loaded, file, null));
        loaded.save(file);

        assertEquals(Boolean.FALSE, UsageReportingSetting.readFromDisk(file));
    }

    @Test
    public void anUpgradeSaveKeepsAnOptOut() throws Exception {
        String contents = "usage-reporting:\n  enabled: false\n";
        File file = configFile(contents);
        YamlConfiguration loaded = loadLikeBukkit(contents);
        loaded.options().copyDefaults(true);

        assertTrue(UsageReportingSetting.prepareToSave(loaded, file, null));
        loaded.save(file);

        assertEquals(Boolean.FALSE, UsageReportingSetting.readFromDisk(file));
        assertFalse(UsageReportingSetting.isEnabled(loaded, file));
    }

    @Test
    public void anUnparseableFileIsNeverOverwrittenAndReportingIsOff() throws Exception {
        String broken = "usage-reporting:\n  enabled: false\n\tbroken: [\n";
        File file = configFile(broken);
        // Bukkit loads a file it cannot parse as empty, so the bundled default (true) shows through.
        YamlConfiguration loaded = loadLikeBukkit("");
        assertTrue(loaded.getBoolean(UsageReportingSetting.ENABLED_KEY));
        List<LogRecord> logged = new ArrayList<>();
        Logger logger = Logger.getAnonymousLogger();
        logger.setUseParentHandlers(false);
        logger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                logged.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        });

        assertTrue(UsageReportingSetting.isUnparseable(file));
        assertFalse(UsageReportingSetting.isEnabled(loaded, file));
        assertFalse(UsageReportingSetting.prepareToSave(loaded, file, logger));

        assertEquals(broken, read(file));
        assertEquals(1, logged.size());
        assertEquals(Level.SEVERE, logged.get(0).getLevel());
    }
}
