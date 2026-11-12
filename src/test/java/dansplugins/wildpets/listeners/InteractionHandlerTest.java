package dansplugins.wildpets.listeners;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.config.EntityConfigService;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import dansplugins.wildpets.scheduler.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Covers the right-click and mount handlers. WildPets is final and cannot be mocked, so the handler is
 * built with a null plugin; the taming branch is only exercised up to the guards that run before
 * isDebugEnabled() is first consulted. Name lookups in the info-view and access-list output paths
 * resolve against a mock Server supplied through the injected ServerProvider.
 */
public class InteractionHandlerTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private Player mockOtherPlayer;

    @Mock
    private Cow mockCow;

    @Mock
    private Horse mockHorse;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private EntityConfigService mockEntityConfigService;

    @Mock
    private PetListRepository mockPetListRepository;

    @Mock
    private PetRecordRepository mockPetRecordRepository;

    @Mock
    private EphemeralData mockEphemeralData;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private Scheduler mockScheduler;

    private PlayerInteractEntityEvent event;

    private UUID playerUUID;
    private UUID otherPlayerUUID;
    private Pet ownPet;
    private Pet othersPet;
    private InteractionHandler handler;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        otherPlayerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockOtherPlayer.getUniqueId()).thenReturn(otherPlayerUUID);
        event = new PlayerInteractEntityEvent(mockPlayer, mockCow);

        ownPet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        ownPet.setName("Bessie");
        othersPet = new Pet(UUID.randomUUID(), otherPlayerUUID, "Other", mockServerProvider);
        othersPet.setName("Daisy");

        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getPlayer(playerUUID)).thenReturn(mockPlayer);
        when(mockServer.getPlayer(otherPlayerUUID)).thenReturn(mockOtherPlayer);
        when(mockPlayer.getName()).thenReturn("Daniel");
        when(mockOtherPlayer.getName()).thenReturn("Other");

        handler = new InteractionHandler(mockEntityConfigService, mockPetListRepository, mockPetRecordRepository,
                mockEphemeralData, null, mockConfigService, mockScheduler, mockServerProvider);
    }

    // --- taming guards ---

    @Test
    public void testTamingAPlayerIsRefusedAndTamingModeIsKept() {
        when(mockEphemeralData.isPlayerTaming(playerUUID)).thenReturn(true);
        event = new PlayerInteractEntityEvent(mockPlayer, mockOtherPlayer);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "You can't tame players.");
        verify(mockEphemeralData, never()).setPlayerAsNotTaming(any());
        verify(mockEphemeralData).setRightClickCooldown(playerUUID, true);
        verify(mockScheduler).scheduleRightClickCooldownSetter(mockPlayer, 1);
        verify(mockPetListRepository, never()).addNewPet(any(), any());
    }

    @Test
    public void testTamingANonLivingEntityIsRefusedAndEndsTamingMode() {
        when(mockEphemeralData.isPlayerTaming(playerUUID)).thenReturn(true);
        event = new PlayerInteractEntityEvent(mockPlayer, mock(Entity.class));

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "You can only tame living entities.");
        verify(mockEphemeralData).setPlayerAsNotTaming(playerUUID);
        verify(mockPetListRepository, never()).addNewPet(any(), any());
    }

    @Test
    public void testTamingAnExistingPetIsRefusedAndEndsTamingMode() {
        when(mockEphemeralData.isPlayerTaming(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "That entity is already a pet.");
        verify(mockEphemeralData).setPlayerAsNotTaming(playerUUID);
        verify(mockPetListRepository, never()).addNewPet(any(), any());
        verifyNoInteractions(mockEntityConfigService);
    }

    // --- selecting ---

    @Test
    public void testSelectingANonPetEndsSelectingMode() {
        when(mockEphemeralData.isPlayerSelecting(playerUUID)).thenReturn(true);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "That entity is not a pet.");
        verify(mockEphemeralData).setPlayerAsNotSelecting(playerUUID);
        verify(mockEphemeralData, never()).selectPetForPlayer(any(), any());
        verify(mockScheduler).scheduleRightClickCooldownSetter(mockPlayer, 1);
    }

    @Test
    public void testSelectingSomeoneElsesPetIsRefused() {
        when(mockEphemeralData.isPlayerSelecting(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);
        when(mockPetListRepository.getPlayersPet(mockPlayer, mockCow)).thenReturn(null);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "That entity is not your pet.");
        verify(mockEphemeralData).setPlayerAsNotSelecting(playerUUID);
        verify(mockEphemeralData, never()).selectPetForPlayer(any(), any());
    }

    @Test
    public void testSelectingOwnPetSelectsItAndEndsSelectingMode() {
        when(mockEphemeralData.isPlayerSelecting(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);
        when(mockPetListRepository.getPlayersPet(mockPlayer, mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockEphemeralData).selectPetForPlayer(ownPet, playerUUID);
        verify(mockPlayer).sendMessage(contains(ChatColor.GREEN + "Bessie selected."));
        verify(mockEphemeralData).setPlayerAsNotSelecting(playerUUID);
        assertFalse(event.isCancelled());
    }

    // --- locking ---

    @Test
    public void testLockingANonPetEndsLockingMode() {
        when(mockEphemeralData.isPlayerLocking(playerUUID)).thenReturn(true);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "This entity isn't a pet.");
        verify(mockEphemeralData).setPlayerAsNotLocking(playerUUID);
        verifyNoInteractions(mockScheduler);
    }

    @Test
    public void testLockingSomeoneElsesPetIsRefused() {
        when(mockEphemeralData.isPlayerLocking(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "This is not your pet.");
        verify(mockEphemeralData).setPlayerAsNotLocking(playerUUID);
        assertFalse(othersPet.isLocked());
    }

    @Test
    public void testLockingAnAlreadyLockedPetIsRefused() {
        ownPet.setLocked(true);
        when(mockEphemeralData.isPlayerLocking(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "This pet is already locked.");
        verify(mockEphemeralData).setPlayerAsNotLocking(playerUUID);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testLockingOwnPetLocksItAndCancelsTheInteraction() {
        when(mockEphemeralData.isPlayerLocking(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        assertTrue(ownPet.isLocked());
        verify(mockPlayer).sendMessage(contains(ChatColor.GREEN + "This pet has been locked."));
        verify(mockEphemeralData).setPlayerAsNotLocking(playerUUID);
        assertTrue(event.isCancelled());
        verify(mockPlayer, never()).sendMessage(ChatColor.RED + "You don't have access to this pet.");
    }

    // --- unlocking ---

    @Test
    public void testUnlockingANonPetEndsUnlockingMode() {
        when(mockEphemeralData.isPlayerUnlocking(playerUUID)).thenReturn(true);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "This entity isn't a pet.");
        verify(mockEphemeralData).setPlayerAsNotUnlocking(playerUUID);
    }

    @Test
    public void testUnlockingSomeoneElsesLockedPetIsRefusedAndCancelsTheInteraction() {
        othersPet.setLocked(true);
        when(mockEphemeralData.isPlayerUnlocking(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "This is not your pet.");
        verify(mockEphemeralData).setPlayerAsNotUnlocking(playerUUID);
        assertTrue(othersPet.isLocked());
        verify(mockPlayer).sendMessage(ChatColor.RED + "You don't have access to this pet.");
        assertTrue(event.isCancelled());
    }

    @Test
    public void testUnlockingAnUnlockedPetIsRefused() {
        when(mockEphemeralData.isPlayerUnlocking(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "This pet is already unlocked.");
        verify(mockEphemeralData).setPlayerAsNotUnlocking(playerUUID);
    }

    @Test
    public void testUnlockingOwnLockedPetUnlocksItAndCancelsTheInteraction() {
        ownPet.setLocked(true);
        when(mockEphemeralData.isPlayerUnlocking(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        assertFalse(ownPet.isLocked());
        verify(mockPlayer).sendMessage(contains(ChatColor.GREEN + "This pet has been unlocked."));
        verify(mockEphemeralData).setPlayerAsNotUnlocking(playerUUID);
        assertTrue(event.isCancelled());
    }

    // --- checking access ---

    @Test
    public void testCheckingAccessOnANonPetEndsCheckingMode() {
        when(mockEphemeralData.isPlayerCheckingAccess(playerUUID)).thenReturn(true);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "This entity isn't a pet.");
        verify(mockEphemeralData, atLeastOnce()).setPlayerAsNotCheckingAccess(playerUUID);
    }

    @Test
    public void testCheckingAccessOnAnUnlockedPetReportsItIsNotLocked() {
        when(mockEphemeralData.isPlayerCheckingAccess(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "This pet isn't locked.");
        verify(mockEphemeralData).setPlayerAsNotCheckingAccess(playerUUID);
    }

    @Test
    public void testCheckingAccessWhenOnlyTheOwnerHasAccess() {
        ownPet.setLocked(true);
        when(mockEphemeralData.isPlayerCheckingAccess(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "No one has access to this pet but you.");
        verify(mockEphemeralData).setPlayerAsNotCheckingAccess(playerUUID);
    }

    @Test
    public void testCheckingAccessWhenTheAccessListIsEmpty() {
        ownPet.setLocked(true);
        ownPet.removeFromAccessList(playerUUID);
        when(mockEphemeralData.isPlayerCheckingAccess(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "No one has access to this pet.");
        verify(mockEphemeralData).setPlayerAsNotCheckingAccess(playerUUID);
    }

    @Test
    public void testCheckingAccessListsTheNamesOnTheAccessListAndSkipsUnresolvableOnes() {
        UUID unknownUUID = UUID.randomUUID();
        OfflinePlayer unknownOfflinePlayer = mock(OfflinePlayer.class);
        when(mockServer.getOfflinePlayer(unknownUUID)).thenReturn(unknownOfflinePlayer);
        ownPet.setLocked(true);
        ownPet.addToAccessList(otherPlayerUUID);
        ownPet.addToAccessList(unknownUUID);
        when(mockEphemeralData.isPlayerCheckingAccess(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(contains("Access List"));
        verify(mockPlayer).sendMessage(contains(ChatColor.WHITE + "Daniel"));
        verify(mockPlayer).sendMessage(contains(ChatColor.WHITE + "Other"));
        verify(mockServer).getOfflinePlayer(unknownUUID);
        verify(mockEphemeralData).setPlayerAsNotCheckingAccess(playerUUID);
        assertTrue(event.isCancelled());
        verify(mockPlayer, never()).sendMessage(ChatColor.RED + "You don't have access to this pet.");
    }

    // --- refusals on someone else's locked pet still block the interaction ---

    @Test
    public void testTamingSomeoneElsesLockedPetIsRefusedAndCancelsTheInteraction() {
        othersPet.setLocked(true);
        when(mockEphemeralData.isPlayerTaming(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "That entity is already a pet.");
        verify(mockPlayer).sendMessage(ChatColor.RED + "You don't have access to this pet.");
        assertTrue(event.isCancelled());
    }

    @Test
    public void testSelectingSomeoneElsesLockedPetIsRefusedAndCancelsTheInteraction() {
        othersPet.setLocked(true);
        when(mockEphemeralData.isPlayerSelecting(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);
        when(mockPetListRepository.getPlayersPet(mockPlayer, mockCow)).thenReturn(null);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "That entity is not your pet.");
        verify(mockPlayer).sendMessage(ChatColor.RED + "You don't have access to this pet.");
        assertTrue(event.isCancelled());
    }

    @Test
    public void testLockingSomeoneElsesLockedPetIsRefusedAndCancelsTheInteraction() {
        othersPet.setLocked(true);
        when(mockEphemeralData.isPlayerLocking(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "This is not your pet.");
        verify(mockPlayer).sendMessage(ChatColor.RED + "You don't have access to this pet.");
        assertTrue(event.isCancelled());
    }

    @Test
    public void testCheckingAccessOnSomeoneElsesLockedPetWithEmptyAccessListCancelsTheInteraction() {
        othersPet.setLocked(true);
        othersPet.removeFromAccessList(otherPlayerUUID);
        when(mockEphemeralData.isPlayerCheckingAccess(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "No one has access to this pet.");
        verify(mockPlayer).sendMessage(ChatColor.RED + "You don't have access to this pet.");
        assertTrue(event.isCancelled());
    }

    // --- plain right-click ---

    @Test
    public void testPlainRightClickOnANonPetDoesNothing() {
        handler.handle(event);

        verify(mockPlayer, never()).sendMessage(anyString());
        verifyNoInteractions(mockScheduler);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testPlainRightClickOnOwnPetSendsInfoAndStartsTheViewCooldown() {
        when(mockConfigService.getInt("rightClickViewCooldown")).thenReturn(7);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(contains("Pet Info"));
        verify(mockPlayer).sendMessage(contains(ChatColor.WHITE + "Bessie"));
        verify(mockPlayer).sendMessage(contains("Owner:    " + ChatColor.WHITE + "Daniel"));
        verify(mockEphemeralData).setRightClickCooldown(playerUUID, true);
        verify(mockScheduler).scheduleRightClickCooldownSetter(mockPlayer, 7);
        verify(mockEphemeralData, never()).selectPetForPlayer(any(), any());
        assertFalse(event.isCancelled());
    }

    @Test
    public void testPlainRightClickOnOwnPetSelectsItWhenRightClickToSelectIsEnabled() {
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(contains("Pet Info"));
        verify(mockEphemeralData).selectPetForPlayer(ownPet, playerUUID);
        verify(mockPlayer).sendMessage(contains(ChatColor.GREEN + "Bessie selected."));
        assertFalse(event.isCancelled());
    }

    @Test
    public void testPlainRightClickOnAlreadySelectedOwnPetDoesNotReselectIt() {
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(true);
        when(mockEphemeralData.getPetSelectionForPlayer(playerUUID)).thenReturn(ownPet);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(ownPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(contains("Pet Info"));
        verify(mockEphemeralData, never()).selectPetForPlayer(any(), any());
        verify(mockPlayer, never()).sendMessage(contains("selected."));
    }

    @Test
    public void testPlainRightClickOnSomeoneElsesLockedPetIsNotSelectedAndCancelsTheInteraction() {
        othersPet.setLocked(true);
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);

        handler.handle(event);

        // Current behaviour: the info view is still shown before the access check runs.
        verify(mockPlayer).sendMessage(contains("Owner:    " + ChatColor.WHITE + "Other"));
        verify(mockEphemeralData, never()).selectPetForPlayer(any(), any());
        verify(mockPlayer).sendMessage(ChatColor.RED + "You don't have access to this pet.");
        assertTrue(event.isCancelled());
    }

    @Test
    public void testLockedPetDeniesInteractionToPlayerWithoutAccess() {
        othersPet.setLocked(true);
        when(mockEphemeralData.hasRightClickCooldown(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);

        handler.handle(event);

        verify(mockPlayer).sendMessage(ChatColor.RED + "You don't have access to this pet.");
        assertTrue(event.isCancelled());
    }

    @Test
    public void testLockedPetAllowsInteractionToPlayerOnAccessList() {
        othersPet.setLocked(true);
        othersPet.addToAccessList(playerUUID);
        when(mockEphemeralData.hasRightClickCooldown(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);

        handler.handle(event);

        verify(mockPlayer, never()).sendMessage(anyString());
        assertFalse(event.isCancelled());
    }

    @Test
    public void testUnlockedPetOfAnotherPlayerAllowsInteraction() {
        when(mockEphemeralData.hasRightClickCooldown(playerUUID)).thenReturn(true);
        when(mockPetListRepository.getPet(mockCow)).thenReturn(othersPet);

        handler.handle(event);

        verify(mockPlayer, never()).sendMessage(anyString());
        assertFalse(event.isCancelled());
    }

    // --- mounting ---

    @Test
    public void testNonPlayerMountIsIgnored() {
        EntityMountEvent mountEvent = new EntityMountEvent(mockCow, mockHorse);

        handler.handle(mountEvent);

        verifyNoInteractions(mockPetListRepository);
        assertFalse(mountEvent.isCancelled());
    }

    @Test
    public void testMountingANonPetIsIgnored() {
        EntityMountEvent mountEvent = new EntityMountEvent(mockPlayer, mockHorse);

        handler.handle(mountEvent);

        verify(mockPlayer, never()).sendMessage(anyString());
        assertFalse(mountEvent.isCancelled());
    }

    @Test
    public void testMountingAnUnlockedPetIsSilentlyAllowed() {
        EntityMountEvent mountEvent = new EntityMountEvent(mockPlayer, mockHorse);
        when(mockPetListRepository.getPet(mockHorse)).thenReturn(othersPet);

        handler.handle(mountEvent);

        verify(mockPlayer, never()).sendMessage(anyString());
        assertFalse(mountEvent.isCancelled());
    }

    @Test
    public void testMountingALockedPetWithoutAccessIsCancelled() {
        othersPet.setLocked(true);
        EntityMountEvent mountEvent = new EntityMountEvent(mockPlayer, mockHorse);
        when(mockPetListRepository.getPet(mockHorse)).thenReturn(othersPet);

        handler.handle(mountEvent);

        assertTrue(mountEvent.isCancelled());
        verify(mockPlayer).sendMessage(ChatColor.RED + "You don't have access to this pet.");
    }

    @Test
    public void testMountingOwnLockedPetIsAllowedWithAMessage() {
        ownPet.setLocked(true);
        EntityMountEvent mountEvent = new EntityMountEvent(mockPlayer, mockHorse);
        when(mockPetListRepository.getPet(mockHorse)).thenReturn(ownPet);

        handler.handle(mountEvent);

        assertFalse(mountEvent.isCancelled());
        verify(mockPlayer).sendMessage(contains(ChatColor.GREEN + "You mount Bessie"));
    }

    @Test
    public void testMountingALockedPetWithAccessIsAllowed() {
        othersPet.setLocked(true);
        othersPet.addToAccessList(playerUUID);
        EntityMountEvent mountEvent = new EntityMountEvent(mockPlayer, mockHorse);
        when(mockPetListRepository.getPet(mockHorse)).thenReturn(othersPet);

        handler.handle(mountEvent);

        assertFalse(mountEvent.isCancelled());
        verify(mockPlayer).sendMessage(contains(ChatColor.GREEN + "You mount Daisy"));
    }
}
