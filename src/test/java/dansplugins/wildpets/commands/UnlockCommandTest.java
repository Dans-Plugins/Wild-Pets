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

public class UnlockCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private CommandSender mockConsoleSender;

    private UUID playerUUID;
    private EphemeralData ephemeralData;
    private UnlockCommand unlockCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        ephemeralData = new EphemeralData();
        unlockCommand = new UnlockCommand(ephemeralData);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersUnlockSubcommandAndPermission() {
        assertEquals(1, unlockCommand.getNames().size());
        assertTrue(unlockCommand.getNames().contains("unlock"));
        assertEquals(1, unlockCommand.getPermissions().size());
        assertTrue(unlockCommand.getPermissions().contains("wp.unlock"));
    }

    @Test
    public void testExecutePutsPlayerIntoUnlockingMode() {
        assertTrue(unlockCommand.execute(mockPlayer));

        assertTrue(ephemeralData.isPlayerUnlocking(playerUUID));
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Right click one of your pets to unlock it.")));
    }

    @Test
    public void testExecuteWithArgumentsIgnoresArguments() {
        assertTrue(unlockCommand.execute(mockPlayer, new String[]{"extra"}));

        assertTrue(ephemeralData.isPlayerUnlocking(playerUUID));
    }

    @Test
    public void testExecuteRejectsNonPlayerSender() {
        assertFalse(unlockCommand.execute(mockConsoleSender));

        verify(mockConsoleSender).sendMessage("Only players can use this command.");
    }
}
