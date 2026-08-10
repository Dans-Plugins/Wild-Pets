package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HelpCommandTest {
    @Mock
    private ConfigService mockConfigService;

    @Mock
    private Player mockPlayer;

    @Mock
    private CommandSender mockConsoleSender;

    private HelpCommand helpCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        helpCommand = new HelpCommand(mockConfigService);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    private void assertListsSubcommand(List<String> messages, String subcommand) {
        boolean found = messages.stream().anyMatch(message -> message.contains(subcommand + " "));
        assertTrue("Expected /wp help to list '" + subcommand + "'", found);
    }

    @Test
    public void testExecuteListsCheckAccessSubcommand() {
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(true);

        assertTrue(helpCommand.execute(mockPlayer));

        assertListsSubcommand(captureMessagesSentToPlayer(), "/wp checkaccess");
    }

    @Test
    public void testExecuteListsStatsSubcommand() {
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(true);

        assertTrue(helpCommand.execute(mockPlayer));

        assertListsSubcommand(captureMessagesSentToPlayer(), "/wp stats");
    }

    @Test
    public void testExecuteListsEveryRegisteredSubcommand() {
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(true);

        assertTrue(helpCommand.execute(mockPlayer));

        List<String> messages = captureMessagesSentToPlayer();
        String[] subcommands = {"/wp help", "/wp tame", "/wp list", "/wp select", "/wp info",
                "/wp rename", "/wp wander", "/wp follow", "/wp stay", "/wp call", "/wp locate",
                "/wp lock", "/wp unlock", "/wp checkaccess", "/wp setfree", "/wp trade",
                "/wp gather", "/wp stats", "/wp config"};
        for (String subcommand : subcommands) {
            assertListsSubcommand(messages, subcommand);
        }
    }

    @Test
    public void testExecuteOmitsBareSelectLineWhenRightClickToSelectIsEnabled() {
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(true);

        assertTrue(helpCommand.execute(mockPlayer));

        long bareSelectLines = captureMessagesSentToPlayer().stream()
                .filter(message -> message.contains("/wp select "))
                .count();
        assertEquals(1, bareSelectLines);
    }

    @Test
    public void testExecuteIncludesBareSelectLineWhenRightClickToSelectIsDisabled() {
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(false);

        assertTrue(helpCommand.execute(mockPlayer));

        long bareSelectLines = captureMessagesSentToPlayer().stream()
                .filter(message -> message.contains("/wp select "))
                .count();
        assertEquals(2, bareSelectLines);
    }

    @Test
    public void testExecuteRejectsNonPlayerSender() {
        assertFalse(helpCommand.execute(mockConsoleSender));

        verify(mockConsoleSender, never()).sendMessage(anyString());
    }
}
