package dansplugins.wildpets.listeners;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetList;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JoinAndQuitHandlerTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private PetListRepository mockPetListRepository;

    @Mock
    private PetList mockPetList;

    @Mock
    private ServerProvider mockServerProvider;

    private UUID playerUUID;
    private EphemeralData ephemeralData;
    private JoinAndQuitHandler handler;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        ephemeralData = new EphemeralData();
        handler = new JoinAndQuitHandler(mockPetListRepository, ephemeralData);
    }

    // ----- join

    @Test
    public void join_createsPetListWhenPlayerHasNone() {
        when(mockPetListRepository.getPetList(playerUUID)).thenReturn(null);

        handler.handle(new PlayerJoinEvent(mockPlayer, "joined"));

        verify(mockPetListRepository).createPetListForPlayer(playerUUID);
    }

    @Test
    public void join_doesNotReplaceExistingPetList() {
        when(mockPetListRepository.getPetList(playerUUID)).thenReturn(mockPetList);

        handler.handle(new PlayerJoinEvent(mockPlayer, "joined"));

        verify(mockPetListRepository, never()).createPetListForPlayer(any());
    }

    // ----- quit

    @Test
    public void quit_clearsEveryPendingRightClickAction() {
        // each setter clears the other action lists, so set them one at a time and quit after each
        ephemeralData.setPlayerAsTaming(playerUUID);
        assertTrue(ephemeralData.isPlayerTaming(playerUUID));
        handler.handle(new PlayerQuitEvent(mockPlayer, "left"));
        assertFalse(ephemeralData.isPlayerTaming(playerUUID));

        ephemeralData.setPlayerAsSelecting(playerUUID);
        assertTrue(ephemeralData.isPlayerSelecting(playerUUID));
        handler.handle(new PlayerQuitEvent(mockPlayer, "left"));
        assertFalse(ephemeralData.isPlayerSelecting(playerUUID));

        ephemeralData.setPlayerAsLocking(playerUUID);
        assertTrue(ephemeralData.isPlayerLocking(playerUUID));
        handler.handle(new PlayerQuitEvent(mockPlayer, "left"));
        assertFalse(ephemeralData.isPlayerLocking(playerUUID));

        ephemeralData.setPlayerAsUnlocking(playerUUID);
        assertTrue(ephemeralData.isPlayerUnlocking(playerUUID));
        handler.handle(new PlayerQuitEvent(mockPlayer, "left"));
        assertFalse(ephemeralData.isPlayerUnlocking(playerUUID));

        ephemeralData.setPlayerAsCheckingAccess(playerUUID);
        assertTrue(ephemeralData.isPlayerCheckingAccess(playerUUID));
        handler.handle(new PlayerQuitEvent(mockPlayer, "left"));
        assertFalse(ephemeralData.isPlayerCheckingAccess(playerUUID));
    }

    @Test
    public void quit_clearsPendingAccessGrantAndRevoke() {
        UUID grantee = UUID.randomUUID();
        UUID revokee = UUID.randomUUID();
        ephemeralData.setPlayerAsGrantingAccess(playerUUID, grantee);
        ephemeralData.setPlayerAsRevokingAccess(playerUUID, revokee);
        assertTrue(ephemeralData.isPlayerGrantingAccess(playerUUID));
        assertTrue(ephemeralData.isPlayerRevokingAccess(playerUUID));

        handler.handle(new PlayerQuitEvent(mockPlayer, "left"));

        assertFalse(ephemeralData.isPlayerGrantingAccess(playerUUID));
        assertFalse(ephemeralData.isPlayerRevokingAccess(playerUUID));
    }

    @Test
    public void quit_leavesOtherPlayersPendingActionsUntouched() {
        UUID otherPlayer = UUID.randomUUID();
        ephemeralData.setPlayerAsTaming(otherPlayer);

        handler.handle(new PlayerQuitEvent(mockPlayer, "left"));

        assertTrue(ephemeralData.isPlayerTaming(otherPlayer));
    }

    @Test
    public void quit_currentlyKeepsPetSelectionAndRightClickCooldown() {
        // Characterization: clearPlayerFromLists does not touch the selection or the cooldown,
        // so both survive a quit and are still present when the player rejoins.
        Pet pet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        ephemeralData.selectPetForPlayer(pet, playerUUID);
        ephemeralData.setRightClickCooldown(playerUUID, true);

        handler.handle(new PlayerQuitEvent(mockPlayer, "left"));

        assertSame(pet, ephemeralData.getPetSelectionForPlayer(playerUUID));
        assertTrue(ephemeralData.hasRightClickCooldown(playerUUID));
    }

    @Test
    public void quit_doesNotTouchPetListRepository() {
        handler.handle(new PlayerQuitEvent(mockPlayer, "left"));

        verifyNoInteractions(mockPetListRepository);
    }
}
