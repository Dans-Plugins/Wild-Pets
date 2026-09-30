package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CheckAccessCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private CommandSender mockConsoleSender;

    private UUID playerUUID;
    private EphemeralData ephemeralData;
    private CheckAccessCommand checkAccessCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        ephemeralData = new EphemeralData();
        checkAccessCommand = new CheckAccessCommand(ephemeralData);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersCheckAccessSubcommandAndPermission() {
        assertEquals(1, checkAccessCommand.getNames().size());
        assertTrue(checkAccessCommand.getNames().contains("checkaccess"));
        assertEquals(1, checkAccessCommand.getPermissions().size());
        assertTrue(checkAccessCommand.getPermissions().contains("wp.checkaccess"));
    }

    @Test
    public void testExecutePutsPlayerIntoCheckingAccessMode() {
        assertTrue(checkAccessCommand.execute(mockPlayer));

        assertTrue(ephemeralData.isPlayerCheckingAccess(playerUUID));
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Right click a pet to check who has access to it.")));
    }

    @Test
    public void testExecuteWithArgumentsIgnoresArguments() {
        assertTrue(checkAccessCommand.execute(mockPlayer, new String[]{"extra"}));

        assertTrue(ephemeralData.isPlayerCheckingAccess(playerUUID));
    }

    @Test
    public void testExecuteRejectsNonPlayerSender() {
        assertFalse(checkAccessCommand.execute(mockConsoleSender));

        verify(mockConsoleSender).sendMessage("Only players can use this command.");
    }
}
