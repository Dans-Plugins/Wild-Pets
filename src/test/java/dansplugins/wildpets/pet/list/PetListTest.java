package dansplugins.wildpets.pet.list;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.utils.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PetListTest {
    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Mob mockMob;

    @Mock
    private Player mockPlayer;

    private UUID ownerUUID;
    private PetList petList;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getEntity(any(UUID.class))).thenReturn(null);

        ownerUUID = UUID.randomUUID();
        petList = new PetList(mockConfigService, ownerUUID, mockServerProvider);
    }

    @Test
    public void testInitializeEmptyList() {
        // verify
        assertEquals(ownerUUID, petList.getOwnerUUID());
        assertTrue(petList.getPets().isEmpty());
        assertEquals(0, petList.getNumPets());
    }

    @Test
    public void testAddPetIncreasesCount() {
        // prepare
        Pet pet = createPet("Daniel");

        // execute
        petList.addPet(pet);

        // verify
        assertEquals(1, petList.getNumPets());
        assertTrue(petList.getPets().contains(pet));
    }

    @Test
    public void testGetPetByUniqueID() {
        // prepare
        Pet pet = createPet("Daniel");
        petList.addPet(pet);

        // execute & verify
        assertSame(pet, petList.getPet(pet.getUniqueID()));
        assertNull(petList.getPet(UUID.randomUUID()));
    }

    @Test
    public void testGetPetByNameIsCaseInsensitive() {
        // prepare
        Pet pet = createPet("Daniel");
        petList.addPet(pet);

        // execute & verify
        assertSame(pet, petList.getPet("Daniel's_Pet"));
        assertSame(pet, petList.getPet("daniel'S_pEt"));
        assertNull(petList.getPet("Somebody_Elses_Pet"));
    }

    @Test
    public void testIsNameTakenIsCaseInsensitive() {
        // prepare
        Pet pet = createPet("Daniel");
        petList.addPet(pet);

        // execute & verify
        assertTrue(petList.isNameTaken(pet.getName()));
        assertTrue(petList.isNameTaken(pet.getName().toUpperCase()));
        assertFalse(petList.isNameTaken("Unclaimed_Name"));
    }

    @Test
    public void testRemovePetForTransferLeavesEntityStateAlone() {
        // prepare
        Pet pet = createPet("Daniel");
        petList.addPet(pet);

        // execute
        boolean removed = petList.removePetForTransfer(pet);

        // verify - unlike removePet, this path never resolves the entity at all
        assertTrue(removed);
        assertEquals(0, petList.getNumPets());
        verify(mockServer, never()).getEntity(any(UUID.class));
    }

    @Test
    public void testRemovePetClearsEntityState() {
        // prepare
        Pet pet = createPet("Daniel");
        petList.addPet(pet);
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(mockMob);

        // execute
        boolean removed = petList.removePet(pet);

        // verify
        assertTrue(removed);
        assertEquals(0, petList.getNumPets());
        verify(mockMob).setCustomName("");
        verify(mockMob).setPersistent(false);
        verify(mockMob).setRemoveWhenFarAway(true);
        verify(mockMob).setInvulnerable(false);
    }

    @Test
    public void testRemovePetStillRemovesWhenEntityIsNotLoaded() {
        // prepare - the default stubbing resolves every entity to null
        Pet pet = createPet("Daniel");
        petList.addPet(pet);

        // execute
        boolean removed = petList.removePet(pet);

        // verify
        assertTrue(removed);
        assertEquals(0, petList.getNumPets());
    }

    @Test
    public void testRemovePetReturnsFalseWhenPetAbsent() {
        // prepare
        Pet pet = createPet("Daniel");
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(mockMob);

        // execute & verify - the entity is still cleaned up, but nothing was removed
        assertFalse(petList.removePet(pet));
    }

    @Test
    public void testRemovePetToleratesAnUnavailableServer() {
        // prepare
        when(mockServerProvider.get()).thenReturn(null);
        Pet pet = createPet("Daniel");
        petList.addPet(pet);

        // execute & verify
        assertTrue(petList.removePet(pet));
        assertEquals(0, petList.getNumPets());
    }

    @Test
    public void testSendListOfPetsToPlayerMarksUnloadedPets() {
        // prepare
        Pet loadedPet = createPet("Loaded");
        Pet unloadedPet = createPet("Unloaded");
        petList.addPet(loadedPet);
        petList.addPet(unloadedPet);
        when(mockServer.getEntity(loadedPet.getUniqueID())).thenReturn(mockMob);
        when(mockPlayer.getUniqueId()).thenReturn(ownerUUID);

        // execute
        petList.sendListOfPetsToPlayer(mockPlayer);

        // verify
        verify(mockPlayer).sendMessage(MessageFormat.line(ChatColor.WHITE + loadedPet.getName()));
        verify(mockPlayer).sendMessage(MessageFormat.line(
                ChatColor.WHITE + unloadedPet.getName() + ChatColor.RED + " [not found]"));
        verify(mockPlayer).sendMessage(MessageFormat.footer());
    }

    @Test
    public void testSendListOfPetsToPlayerTellsOwnerWhenEmpty() {
        // prepare
        when(mockPlayer.getUniqueId()).thenReturn(ownerUUID);

        // execute
        petList.sendListOfPetsToPlayer(mockPlayer);

        // verify
        verify(mockPlayer).sendMessage(ChatColor.RED + "You don't have any pets yet.");
    }

    @Test
    public void testSendListOfPetsToPlayerTellsOtherPlayersWhenEmpty() {
        // prepare
        when(mockPlayer.getUniqueId()).thenReturn(UUID.randomUUID());

        // execute
        petList.sendListOfPetsToPlayer(mockPlayer);

        // verify
        verify(mockPlayer).sendMessage(ChatColor.RED + "That player doesn't have any pets yet.");
    }

    @Test
    public void testRemovePetForTransferReturnsFalseWhenPetAbsent() {
        // prepare
        Pet pet = createPet("Daniel");

        // execute & verify
        assertFalse(petList.removePetForTransfer(pet));
    }

    @Test
    public void testGetFollowingPetsOnlyReturnsFollowers() {
        // prepare
        Pet follower = createPet("Follower");
        follower.setFollowing();
        Pet wanderer = createPet("Wanderer");
        Pet stayer = createPet("Stayer");
        stayer.setStaying();
        petList.addPet(follower);
        petList.addPet(wanderer);
        petList.addPet(stayer);

        // execute & verify
        assertEquals(1, petList.getFollowingPets().size());
        assertSame(follower, petList.getFollowingPets().get(0));
    }

    @Test
    public void testGetNewIDIsWithinRangeAndUnused() {
        // prepare
        when(mockConfigService.getInt("petLimit")).thenReturn(10);
        Pet pet = createPet("Daniel");
        pet.setAssignedID(5);
        petList.addPet(pet);

        // execute
        int newID = petList.getNewID();

        // verify
        assertTrue(newID >= 0);
        assertTrue(newID < 100); // petLimit * 10
        assertNotEquals(5, newID);
    }

    @Test
    public void testGetNewIDNeverCollidesWithExistingIDs() {
        // prepare - a petLimit of 1 leaves only IDs 0-9, so collisions are likely
        when(mockConfigService.getInt("petLimit")).thenReturn(1);
        for (int assignedID = 0; assignedID < 9; assignedID++) {
            Pet pet = createPet("Owner" + assignedID);
            pet.setAssignedID(assignedID);
            petList.addPet(pet);
        }

        // execute - the only remaining free ID is 9
        int newID = petList.getNewID();

        // verify
        assertEquals(9, newID);
    }

    private Pet createPet(String ownerName) {
        return new Pet(UUID.randomUUID(), ownerUUID, ownerName, mockServerProvider);
    }
}
