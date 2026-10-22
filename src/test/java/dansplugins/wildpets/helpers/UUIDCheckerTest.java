package dansplugins.wildpets.helpers;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UUIDCheckerTest {
    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Player mockOnlinePlayer;

    @Mock
    private OfflinePlayer mockOfflinePlayer;

    private final UUID playerUUID = UUID.randomUUID();

    private UUIDChecker uuidChecker;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockServerProvider.get()).thenReturn(mockServer);
        uuidChecker = new UUIDChecker(mockServerProvider);
    }

    @Test(expected = IllegalArgumentException.class)
    public void findPlayerNameBasedOnUUID_nullUUID_throws() {
        uuidChecker.findPlayerNameBasedOnUUID(null);
    }

    @Test
    public void findPlayerNameBasedOnUUID_onlinePlayer_returnsOnlineName() {
        when(mockServer.getPlayer(playerUUID)).thenReturn(mockOnlinePlayer);
        when(mockOnlinePlayer.getName()).thenReturn("Steve");

        assertEquals("Steve", uuidChecker.findPlayerNameBasedOnUUID(playerUUID));
        verify(mockServer, never()).getOfflinePlayer(any(UUID.class));
    }

    @Test
    public void findPlayerNameBasedOnUUID_offlinePlayerWhoHasPlayed_returnsOfflineName() {
        when(mockServer.getOfflinePlayer(playerUUID)).thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.getName()).thenReturn("Alex");
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

        assertEquals("Alex", uuidChecker.findPlayerNameBasedOnUUID(playerUUID));
    }

    @Test
    public void findPlayerNameBasedOnUUID_offlinePlayerWhoHasNeverPlayed_returnsNull() {
        when(mockServer.getOfflinePlayer(playerUUID)).thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.getName()).thenReturn("Alex");
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(false);

        assertNull(uuidChecker.findPlayerNameBasedOnUUID(playerUUID));
    }

    @Test
    public void findPlayerNameBasedOnUUID_offlinePlayerWithNoName_returnsNull() {
        when(mockServer.getOfflinePlayer(playerUUID)).thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.getName()).thenReturn(null);
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);

        assertNull(uuidChecker.findPlayerNameBasedOnUUID(playerUUID));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findUUIDBasedOnPlayerName_nullName_throws() {
        uuidChecker.findUUIDBasedOnPlayerName(null);
    }

    @Test
    public void findUUIDBasedOnPlayerName_onlinePlayer_returnsOnlineUUID() {
        when(mockServer.getPlayer("Steve")).thenReturn(mockOnlinePlayer);
        when(mockOnlinePlayer.getUniqueId()).thenReturn(playerUUID);

        assertEquals(playerUUID, uuidChecker.findUUIDBasedOnPlayerName("Steve"));
        verify(mockServer, never()).getOfflinePlayer(anyString());
    }

    @Test
    public void findUUIDBasedOnPlayerName_offlinePlayerWhoHasPlayed_returnsOfflineUUID() {
        when(mockServer.getOfflinePlayer("Alex")).thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.getName()).thenReturn("Alex");
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(true);
        when(mockOfflinePlayer.getUniqueId()).thenReturn(playerUUID);

        assertEquals(playerUUID, uuidChecker.findUUIDBasedOnPlayerName("Alex"));
    }

    @Test
    public void findUUIDBasedOnPlayerName_offlinePlayerWhoHasNeverPlayed_returnsNull() {
        when(mockServer.getOfflinePlayer("Alex")).thenReturn(mockOfflinePlayer);
        when(mockOfflinePlayer.getName()).thenReturn("Alex");
        when(mockOfflinePlayer.hasPlayedBefore()).thenReturn(false);
        when(mockOfflinePlayer.getUniqueId()).thenReturn(playerUUID);

        assertNull(uuidChecker.findUUIDBasedOnPlayerName("Alex"));
    }
}
