package dansplugins.wildpets.data;

import dansplugins.wildpets.pet.Pet;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EphemeralDataTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private Pet mockPet;

    @Mock
    private Pet mockOtherPet;

    private UUID playerUUID;
    private UUID otherPlayerUUID;
    private EphemeralData ephemeralData;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        playerUUID = UUID.randomUUID();
        otherPlayerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);

        ephemeralData = new EphemeralData();
    }

    @Test
    public void testFreshInstanceTracksNothingForAPlayer() {
        // verify
        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
        assertFalse(ephemeralData.isPlayerSelecting(playerUUID));
        assertFalse(ephemeralData.isPlayerLocking(playerUUID));
        assertFalse(ephemeralData.isPlayerUnlocking(playerUUID));
        assertFalse(ephemeralData.isPlayerCheckingAccess(playerUUID));
        assertFalse(ephemeralData.isPlayerGrantingAccess(playerUUID));
        assertFalse(ephemeralData.isPlayerRevokingAccess(playerUUID));
        assertFalse(ephemeralData.hasRightClickCooldown(playerUUID));
        assertNull(ephemeralData.getGrantee(playerUUID));
        assertNull(ephemeralData.getRevokee(playerUUID));
        assertNull(ephemeralData.getPetSelectionForPlayer(playerUUID));
    }

    // ----- taming and selecting

    @Test
    public void testSetPlayerAsTamingAndBack() {
        // execute
        ephemeralData.setPlayerAsTaming(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerTaming(playerUUID));

        // execute
        ephemeralData.setPlayerAsNotTaming(playerUUID);

        // verify
        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
    }

    @Test
    public void testSetPlayerAsSelectingAndBack() {
        // execute
        ephemeralData.setPlayerAsSelecting(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerSelecting(playerUUID));

        // execute
        ephemeralData.setPlayerAsNotSelecting(playerUUID);

        // verify
        assertFalse(ephemeralData.isPlayerSelecting(playerUUID));
    }

    @Test
    public void testTamingAndSelectingAreMutuallyExclusive() {
        // prepare
        ephemeralData.setPlayerAsSelecting(playerUUID);

        // execute
        ephemeralData.setPlayerAsTaming(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerTaming(playerUUID));
        assertFalse(ephemeralData.isPlayerSelecting(playerUUID));

        // execute
        ephemeralData.setPlayerAsSelecting(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerSelecting(playerUUID));
        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
    }

    @Test
    public void testSetPlayerAsTamingIsIdempotent() {
        // execute
        ephemeralData.setPlayerAsTaming(playerUUID);
        ephemeralData.setPlayerAsTaming(playerUUID);

        // verify - a single unset call is enough to leave taming mode
        ephemeralData.setPlayerAsNotTaming(playerUUID);
        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
    }

    @Test
    public void testEnteringTamingModeLeavesTheOtherActionFlagsAlone() {
        // prepare - only taming and selecting are cleared when taming mode is entered
        ephemeralData.setPlayerAsLocking(playerUUID);
        ephemeralData.setPlayerAsUnlocking(playerUUID);
        ephemeralData.setPlayerAsCheckingAccess(playerUUID);
        ephemeralData.setPlayerAsGrantingAccess(playerUUID, otherPlayerUUID);
        ephemeralData.setPlayerAsRevokingAccess(playerUUID, otherPlayerUUID);

        // execute
        ephemeralData.setPlayerAsTaming(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerTaming(playerUUID));
        assertTrue(ephemeralData.isPlayerLocking(playerUUID));
        assertTrue(ephemeralData.isPlayerUnlocking(playerUUID));
        assertTrue(ephemeralData.isPlayerCheckingAccess(playerUUID));
        assertTrue(ephemeralData.isPlayerGrantingAccess(playerUUID));
        assertTrue(ephemeralData.isPlayerRevokingAccess(playerUUID));
    }

    @Test
    public void testTamingIsTrackedPerPlayer() {
        // execute
        ephemeralData.setPlayerAsTaming(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerTaming(playerUUID));
        assertFalse(ephemeralData.isPlayerTaming(otherPlayerUUID));
    }

    // ----- locking, unlocking and access checking

    @Test
    public void testSetPlayerAsLockingAndBack() {
        // execute
        ephemeralData.setPlayerAsLocking(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerLocking(playerUUID));

        // execute
        ephemeralData.setPlayerAsNotLocking(playerUUID);

        // verify
        assertFalse(ephemeralData.isPlayerLocking(playerUUID));
    }

    @Test
    public void testSetPlayerAsUnlockingAndBack() {
        // execute
        ephemeralData.setPlayerAsUnlocking(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerUnlocking(playerUUID));

        // execute
        ephemeralData.setPlayerAsNotUnlocking(playerUUID);

        // verify
        assertFalse(ephemeralData.isPlayerUnlocking(playerUUID));
    }

    @Test
    public void testSetPlayerAsCheckingAccessAndBack() {
        // execute
        ephemeralData.setPlayerAsCheckingAccess(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerCheckingAccess(playerUUID));

        // execute
        ephemeralData.setPlayerAsNotCheckingAccess(playerUUID);

        // verify
        assertFalse(ephemeralData.isPlayerCheckingAccess(playerUUID));
    }

    @Test
    public void testLockingUnlockingAndAccessCheckingDoNotClearEachOther() {
        // execute - none of these three clear any other action list
        ephemeralData.setPlayerAsLocking(playerUUID);
        ephemeralData.setPlayerAsUnlocking(playerUUID);
        ephemeralData.setPlayerAsCheckingAccess(playerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerLocking(playerUUID));
        assertTrue(ephemeralData.isPlayerUnlocking(playerUUID));
        assertTrue(ephemeralData.isPlayerCheckingAccess(playerUUID));
    }

    @Test
    public void testUnsettingAnActionFlagThatWasNeverSetIsHarmless() {
        // execute
        ephemeralData.setPlayerAsNotTaming(playerUUID);
        ephemeralData.setPlayerAsNotSelecting(playerUUID);
        ephemeralData.setPlayerAsNotLocking(playerUUID);
        ephemeralData.setPlayerAsNotUnlocking(playerUUID);
        ephemeralData.setPlayerAsNotCheckingAccess(playerUUID);
        ephemeralData.setPlayerAsNotGrantingAccess(playerUUID);
        ephemeralData.setPlayerAsNotRevokingAccess(playerUUID);

        // verify
        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
        assertFalse(ephemeralData.isPlayerLocking(playerUUID));
        assertFalse(ephemeralData.isPlayerUnlocking(playerUUID));
    }

    // ----- access granting and revoking

    @Test
    public void testSetPlayerAsGrantingAccessRecordsTheGrantee() {
        // execute
        ephemeralData.setPlayerAsGrantingAccess(playerUUID, otherPlayerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerGrantingAccess(playerUUID));
        assertEquals(otherPlayerUUID, ephemeralData.getGrantee(playerUUID));

        // execute
        ephemeralData.setPlayerAsNotGrantingAccess(playerUUID);

        // verify
        assertFalse(ephemeralData.isPlayerGrantingAccess(playerUUID));
        assertNull(ephemeralData.getGrantee(playerUUID));
    }

    @Test
    public void testSetPlayerAsRevokingAccessRecordsTheRevokee() {
        // execute
        ephemeralData.setPlayerAsRevokingAccess(playerUUID, otherPlayerUUID);

        // verify
        assertTrue(ephemeralData.isPlayerRevokingAccess(playerUUID));
        assertEquals(otherPlayerUUID, ephemeralData.getRevokee(playerUUID));

        // execute
        ephemeralData.setPlayerAsNotRevokingAccess(playerUUID);

        // verify
        assertFalse(ephemeralData.isPlayerRevokingAccess(playerUUID));
        assertNull(ephemeralData.getRevokee(playerUUID));
    }

    @Test
    public void testGrantingAccessOverwritesAPreviousGrantee() {
        // prepare
        UUID firstGrantee = UUID.randomUUID();
        UUID secondGrantee = UUID.randomUUID();
        ephemeralData.setPlayerAsGrantingAccess(playerUUID, firstGrantee);

        // execute
        ephemeralData.setPlayerAsGrantingAccess(playerUUID, secondGrantee);

        // verify
        assertEquals(secondGrantee, ephemeralData.getGrantee(playerUUID));
    }

    @Test
    public void testGrantingAndRevokingAreTrackedIndependently() {
        // prepare
        UUID grantee = UUID.randomUUID();
        UUID revokee = UUID.randomUUID();

        // execute
        ephemeralData.setPlayerAsGrantingAccess(playerUUID, grantee);
        ephemeralData.setPlayerAsRevokingAccess(playerUUID, revokee);

        // verify
        assertEquals(grantee, ephemeralData.getGrantee(playerUUID));
        assertEquals(revokee, ephemeralData.getRevokee(playerUUID));

        // execute
        ephemeralData.setPlayerAsNotGrantingAccess(playerUUID);

        // verify
        assertFalse(ephemeralData.isPlayerGrantingAccess(playerUUID));
        assertTrue(ephemeralData.isPlayerRevokingAccess(playerUUID));
    }

    // ----- pet selections

    @Test
    public void testSelectPetForPlayer() {
        // execute
        ephemeralData.selectPetForPlayer(mockPet, playerUUID);

        // verify
        assertSame(mockPet, ephemeralData.getPetSelectionForPlayer(playerUUID));
    }

    @Test
    public void testSelectPetForPlayerReplacesAnExistingSelection() {
        // prepare
        ephemeralData.selectPetForPlayer(mockPet, playerUUID);

        // execute
        ephemeralData.selectPetForPlayer(mockOtherPet, playerUUID);

        // verify
        assertSame(mockOtherPet, ephemeralData.getPetSelectionForPlayer(playerUUID));
    }

    @Test
    public void testClearPetSelectionForPlayer() {
        // prepare
        ephemeralData.selectPetForPlayer(mockPet, playerUUID);

        // execute
        ephemeralData.clearPetSelectionForPlayer(playerUUID);

        // verify
        assertNull(ephemeralData.getPetSelectionForPlayer(playerUUID));
    }

    @Test
    public void testPetSelectionsAreTrackedPerPlayer() {
        // execute
        ephemeralData.selectPetForPlayer(mockPet, playerUUID);
        ephemeralData.selectPetForPlayer(mockOtherPet, otherPlayerUUID);

        // verify
        assertSame(mockPet, ephemeralData.getPetSelectionForPlayer(playerUUID));
        assertSame(mockOtherPet, ephemeralData.getPetSelectionForPlayer(otherPlayerUUID));

        // execute
        ephemeralData.clearPetSelectionForPlayer(playerUUID);

        // verify
        assertNull(ephemeralData.getPetSelectionForPlayer(playerUUID));
        assertSame(mockOtherPet, ephemeralData.getPetSelectionForPlayer(otherPlayerUUID));
    }

    // ----- right click cooldown

    @Test
    public void testSetRightClickCooldownOnAndOff() {
        // execute
        ephemeralData.setRightClickCooldown(playerUUID, true);

        // verify
        assertTrue(ephemeralData.hasRightClickCooldown(playerUUID));

        // execute
        ephemeralData.setRightClickCooldown(playerUUID, false);

        // verify
        assertFalse(ephemeralData.hasRightClickCooldown(playerUUID));
    }

    @Test
    public void testSetRightClickCooldownIsIdempotent() {
        // execute
        ephemeralData.setRightClickCooldown(playerUUID, true);
        ephemeralData.setRightClickCooldown(playerUUID, true);

        // verify - a single removal is enough to clear the cooldown
        ephemeralData.setRightClickCooldown(playerUUID, false);
        assertFalse(ephemeralData.hasRightClickCooldown(playerUUID));
    }

    @Test
    public void testClearingARightClickCooldownThatWasNeverSetIsHarmless() {
        // execute
        ephemeralData.setRightClickCooldown(playerUUID, false);

        // verify
        assertFalse(ephemeralData.hasRightClickCooldown(playerUUID));
    }

    // ----- clearPlayerFromLists

    @Test
    public void testClearPlayerFromListsClearsTheActionFlagsItCovers() {
        // prepare
        ephemeralData.setPlayerAsTaming(playerUUID);
        ephemeralData.setPlayerAsLocking(playerUUID);
        ephemeralData.setPlayerAsCheckingAccess(playerUUID);
        ephemeralData.setPlayerAsGrantingAccess(playerUUID, otherPlayerUUID);
        ephemeralData.setPlayerAsRevokingAccess(playerUUID, otherPlayerUUID);

        // execute
        ephemeralData.clearPlayerFromLists(mockPlayer);

        // verify
        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
        assertFalse(ephemeralData.isPlayerSelecting(playerUUID));
        assertFalse(ephemeralData.isPlayerLocking(playerUUID));
        assertFalse(ephemeralData.isPlayerCheckingAccess(playerUUID));
        assertFalse(ephemeralData.isPlayerGrantingAccess(playerUUID));
        assertFalse(ephemeralData.isPlayerRevokingAccess(playerUUID));
    }

    @Test
    public void testClearPlayerFromListsLeavesUnlockingModeSet() {
        // prepare
        ephemeralData.setPlayerAsUnlocking(playerUUID);

        // execute
        ephemeralData.clearPlayerFromLists(mockPlayer);

        // verify - clearPlayerFromLists does not call setPlayerAsNotUnlocking, unlike the
        // other action lists, so unlocking mode survives a quit. Tracked in issue #315.
        assertTrue(ephemeralData.isPlayerUnlocking(playerUUID));
    }

    @Test
    public void testClearPlayerFromListsLeavesTheSelectionAndCooldownIntact() {
        // prepare - neither the selection map nor the cooldown list is an "action list"
        ephemeralData.selectPetForPlayer(mockPet, playerUUID);
        ephemeralData.setRightClickCooldown(playerUUID, true);

        // execute
        ephemeralData.clearPlayerFromLists(mockPlayer);

        // verify
        assertSame(mockPet, ephemeralData.getPetSelectionForPlayer(playerUUID));
        assertTrue(ephemeralData.hasRightClickCooldown(playerUUID));
    }

    @Test
    public void testClearPlayerFromListsOnlyAffectsTheGivenPlayer() {
        // prepare
        ephemeralData.setPlayerAsTaming(playerUUID);
        ephemeralData.setPlayerAsTaming(otherPlayerUUID);

        // execute
        ephemeralData.clearPlayerFromLists(mockPlayer);

        // verify
        assertFalse(ephemeralData.isPlayerTaming(playerUUID));
        assertTrue(ephemeralData.isPlayerTaming(otherPlayerUUID));
    }
}
