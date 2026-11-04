package dansplugins.wildpets.config;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * Exercises /wp config against a real YamlConfiguration, because the difference between a stored
 * Boolean and a stored String only shows up in the real getters. WildPets is final, so saving is
 * out of reach: option handling is tested through the static applyConfigOption step, and
 * sendConfigList through a spy with getConfig() stubbed.
 */
public class ConfigServiceTest {
    private static final String[] BOOLEAN_OPTIONS = {
            "debugMode",
            "cancelTamingAfterFailedAttempt",
            "rightClickToSelect",
            "preventMountingLockedPets",
            "damageToPetsEnabled",
            "showLineageInfo",
            "bornPetsEnabled",
            "damageFromPetsEnabled"
    };

    private static final String[] INTEGER_OPTIONS = {
            "petLimit",
            "rightClickViewCooldown",
            "maxScheduleAttempts",
            "petNameCharacterLimit"
    };

    @Mock
    private CommandSender mockSender;

    private YamlConfiguration config;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        config = new YamlConfiguration();
        for (String option : BOOLEAN_OPTIONS) {
            config.set("configOptions." + option, false);
        }
        for (String option : INTEGER_OPTIONS) {
            config.set("configOptions." + option, 10);
        }
    }

    private YamlConfiguration writtenToDiskAndReloaded() throws InvalidConfigurationException {
        YamlConfiguration reloaded = new YamlConfiguration();
        reloaded.loadFromString(config.saveToString());
        return reloaded;
    }

    private List<String> messagesSentToSender() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockSender, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    private boolean senderWasSent(String expectedMessage) {
        return messagesSentToSender().stream().anyMatch(message -> message.contains(expectedMessage));
    }

    private String sentLineFor(String option) {
        return messagesSentToSender().stream()
                .filter(message -> message.contains(option + ":"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No line for " + option));
    }

    @Test
    public void everyBooleanOption_survivesASaveAsABoolean() throws InvalidConfigurationException {
        for (String option : BOOLEAN_OPTIONS) {
            assertTrue(option, ConfigService.applyConfigOption(config, option, "true", mockSender));

            assertTrue(option, writtenToDiskAndReloaded().getBoolean("configOptions." + option));
        }
    }

    @Test
    public void everyBooleanOption_canBeSwitchedBackOff() throws InvalidConfigurationException {
        for (String option : BOOLEAN_OPTIONS) {
            config.set("configOptions." + option, true);

            assertTrue(option, ConfigService.applyConfigOption(config, option, "FALSE", mockSender));

            assertFalse(option, writtenToDiskAndReloaded().getBoolean("configOptions." + option));
        }
    }

    @Test
    public void booleanOption_reportsBooleanSet() {
        ConfigService.applyConfigOption(config, "cancelTamingAfterFailedAttempt", "true", mockSender);

        assertTrue(senderWasSent("Boolean set."));
    }

    @Test
    public void booleanOption_rejectsAnythingButTrueOrFalse() {
        config.set("configOptions.debugMode", true);

        assertFalse(ConfigService.applyConfigOption(config, "debugMode", "yes", mockSender));

        assertTrue(config.getBoolean("configOptions.debugMode"));
        assertTrue(senderWasSent("true or false"));
        assertFalse(senderWasSent("Boolean set."));
    }

    @Test
    public void everyIntegerOption_isStoredAsAnInteger() throws InvalidConfigurationException {
        for (String option : INTEGER_OPTIONS) {
            assertTrue(option, ConfigService.applyConfigOption(config, option, "42", mockSender));

            assertEquals(option, 42, writtenToDiskAndReloaded().getInt("configOptions." + option));
        }
        assertTrue(senderWasSent("Integer set."));
    }

    @Test
    public void integerOption_rejectsANonNumericValue() {
        assertFalse(ConfigService.applyConfigOption(config, "petLimit", "lots", mockSender));

        assertEquals(10, config.getInt("configOptions.petLimit"));
        assertTrue(senderWasSent("whole number"));
        assertFalse(senderWasSent("Integer set."));
    }

    @Test
    public void unknownOption_isReportedAndNotWritten() {
        assertFalse(ConfigService.applyConfigOption(config, "noSuchOption", "true", mockSender));

        assertFalse(config.isSet("configOptions.noSuchOption"));
        assertTrue(senderWasSent("wasn't found"));
    }

    @Test
    public void configList_showsTheValueTheHandlerActsOn() {
        // What an earlier /wp config set left on disk: the text 'true', which getBoolean reads as false.
        config.set("configOptions.cancelTamingAfterFailedAttempt", "true");
        ConfigService configService = spy(new ConfigService(null, null));
        doReturn(config).when(configService).getConfig();

        configService.sendConfigList(mockSender);

        String line = sentLineFor("cancelTamingAfterFailedAttempt");
        assertTrue(line, line.endsWith("false"));
    }
}
