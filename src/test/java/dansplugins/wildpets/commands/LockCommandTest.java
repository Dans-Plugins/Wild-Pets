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

public class LockCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private CommandSender mockConsoleSender;

    private UUID playerUUID;
    private EphemeralData ephemeralData;
    private LockCommand lockCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        ephemeralData = new EphemeralData();
        lockCommand = new LockCommand(ephemeralData);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersLockSubcommandAndPermission() {
        assertEquals(1, lockCommand.getNames().size());
        assertTrue(lockCommand.getNames().contains("lock"));
        assertEquals(1, lockCommand.getPermissions().size());
        assertTrue(lockCommand.getPermissions().contains("wp.lock"));
    }

    @Test
    public void testExecutePutsPlayerIntoLockingMode() {
        assertTrue(lockCommand.execute(mockPlayer));

        assertTrue(ephemeralData.isPlayerLocking(playerUUID));
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Right click one of your pets to lock it.")));
    }

    @Test
    public void testExecuteClearsPendingTamingMode() {
        ephemeralData.setPlayerAsTaming(playerUUID);

        assertTrue(lockCommand.execute(mockPlayer));

        assertTrue(ephemeralData.isPlayerLocking(playerUUID));
        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
    }

    @Test
    public void testExecuteWithArgumentsIgnoresArguments() {
        assertTrue(lockCommand.execute(mockPlayer, new String[]{"extra"}));

        assertTrue(ephemeralData.isPlayerLocking(playerUUID));
    }

    @Test
    public void testExecuteRejectsNonPlayerSender() {
        assertFalse(lockCommand.execute(mockConsoleSender));

        verify(mockConsoleSender).sendMessage("Only players can use this command.");
    }
}
