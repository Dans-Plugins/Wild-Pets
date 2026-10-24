package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ConfigCommandTest {
    @Mock
    private CommandSender mockSender;

    @Mock
    private ConfigService mockConfigService;

    private ConfigCommand configCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        configCommand = new ConfigCommand(mockConfigService);
    }

    private List<String> captureMessagesSentToSender() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockSender, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    private boolean senderWasSent(String expectedMessage) {
        return captureMessagesSentToSender().stream()
                .anyMatch(message -> message.contains(expectedMessage));
    }

    private void verifyConfigServiceUntouched() {
        verify(mockConfigService, never()).sendConfigList(any());
        verify(mockConfigService, never()).setConfigOption(anyString(), anyString(), any());
    }

    @Test
    public void testRegistersConfigSubcommandAndPermission() {
        assertEquals(1, configCommand.getNames().size());
        assertTrue(configCommand.getNames().contains("config"));
        assertEquals(1, configCommand.getPermissions().size());
        assertTrue(configCommand.getPermissions().contains("wp.config"));
    }

    @Test
    public void testNoArgumentsSendsUsage() {
        assertFalse(configCommand.execute(mockSender));

        assertTrue(senderWasSent("Usage: /wp config <set|show> (option) (value)"));
        verifyConfigServiceUntouched();
    }

    @Test
    public void testEmptyArgumentArraySendsSubcommandList() {
        assertFalse(configCommand.execute(mockSender, new String[]{}));

        assertTrue(senderWasSent("Sub-commands: show, set"));
        verifyConfigServiceUntouched();
    }

    @Test
    public void testShowSendsConfigList() {
        assertTrue(configCommand.execute(mockSender, new String[]{"show"}));

        verify(mockConfigService).sendConfigList(mockSender);
    }

    @Test
    public void testShowIsCaseInsensitive() {
        assertTrue(configCommand.execute(mockSender, new String[]{"SHOW"}));

        verify(mockConfigService).sendConfigList(mockSender);
    }

    @Test
    public void testSetPassesOptionAndValueToConfigService() {
        assertTrue(configCommand.execute(mockSender, new String[]{"set", "petLimit", "42"}));

        verify(mockConfigService).setConfigOption("petLimit", "42", mockSender);
    }

    @Test
    public void testSetIsCaseInsensitive() {
        assertTrue(configCommand.execute(mockSender, new String[]{"SeT", "petLimit", "42"}));

        verify(mockConfigService).setConfigOption("petLimit", "42", mockSender);
    }

    @Test
    public void testSetIgnoresArgumentsBeyondValue() {
        assertTrue(configCommand.execute(mockSender, new String[]{"set", "petLimit", "42", "extra"}));

        verify(mockConfigService).setConfigOption("petLimit", "42", mockSender);
    }

    @Test
    public void testSetWithoutValueSendsUsage() {
        assertFalse(configCommand.execute(mockSender, new String[]{"set", "petLimit"}));

        assertTrue(senderWasSent("Usage: /wp config <set|show> (option) (value)"));
        verifyConfigServiceUntouched();
    }

    @Test
    public void testSetWithoutOptionSendsUsage() {
        assertFalse(configCommand.execute(mockSender, new String[]{"set"}));

        assertTrue(senderWasSent("Usage: /wp config <set|show> (option) (value)"));
        verifyConfigServiceUntouched();
    }

    @Test
    public void testUnknownSubcommandSendsSubcommandList() {
        assertFalse(configCommand.execute(mockSender, new String[]{"reload"}));

        assertTrue(senderWasSent("Sub-commands: show, set"));
        verifyConfigServiceUntouched();
    }
}
