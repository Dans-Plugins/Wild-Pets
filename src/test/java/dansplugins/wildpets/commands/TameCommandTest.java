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

public class TameCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private CommandSender mockConsoleSender;

    private UUID playerUUID;
    private EphemeralData ephemeralData;
    private TameCommand tameCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        ephemeralData = new EphemeralData();
        tameCommand = new TameCommand(ephemeralData);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersTameSubcommandAndPermission() {
        assertEquals(1, tameCommand.getNames().size());
        assertTrue(tameCommand.getNames().contains("tame"));
        assertEquals(1, tameCommand.getPermissions().size());
        assertTrue(tameCommand.getPermissions().contains("wp.tame"));
    }

    @Test
    public void testExecuteWithoutArgumentsPutsPlayerIntoTamingMode() {
        assertTrue(tameCommand.execute(mockPlayer));

        assertTrue(ephemeralData.isPlayerTaming(playerUUID));
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Right click on an entity to tame it.")));
    }

    @Test
    public void testExecuteWithoutArgumentsClearsSelectingMode() {
        ephemeralData.setPlayerAsSelecting(playerUUID);

        assertTrue(tameCommand.execute(mockPlayer));

        assertTrue(ephemeralData.isPlayerTaming(playerUUID));
        assertFalse(ephemeralData.isPlayerSelecting(playerUUID));
    }

    @Test
    public void testExecuteWithoutArgumentsClearsLockingMode() {
        ephemeralData.setPlayerAsLocking(playerUUID);

        assertTrue(tameCommand.execute(mockPlayer));

        assertTrue(ephemeralData.isPlayerTaming(playerUUID));
        assertFalse(ephemeralData.isPlayerLocking(playerUUID));
    }

    @Test
    public void testExecuteWithCancelTakesPlayerOutOfTamingMode() {
        ephemeralData.setPlayerAsTaming(playerUUID);

        assertTrue(tameCommand.execute(mockPlayer, new String[]{"cancel"}));

        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Taming cancelled.")));
    }

    @Test
    public void testExecuteWithCancelIsCaseInsensitive() {
        ephemeralData.setPlayerAsTaming(playerUUID);

        assertTrue(tameCommand.execute(mockPlayer, new String[]{"CANCEL"}));

        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
    }

    @Test
    public void testExecuteWithOtherArgumentFallsBackToTamingMode() {
        assertTrue(tameCommand.execute(mockPlayer, new String[]{"wolf"}));

        assertTrue(ephemeralData.isPlayerTaming(playerUUID));
    }

    @Test
    public void testExecuteRejectsNonPlayerSender() {
        assertFalse(tameCommand.execute(mockConsoleSender));
        assertFalse(tameCommand.execute(mockConsoleSender, new String[]{"cancel"}));

        verifyNoInteractions(mockConsoleSender);
    }
}
