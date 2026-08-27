package dansplugins.wildpets.pet.list;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PetListRepositoryTest {
    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Player mockPlayer;

    @Mock
    private Mob mockMob;

    private UUID playerUUID;
    private UUID entityUUID;
    private PetListRepository petListRepository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getEntity(any(UUID.class))).thenReturn(null);
        when(mockConfigService.getInt("petLimit")).thenReturn(10);

        playerUUID = UUID.randomUUID();
        entityUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("Daniel");
        when(mockMob.getUniqueId()).thenReturn(entityUUID);
        when(mockMob.getLocation()).thenReturn(new Location(null, 10.9, 64.2, -3.5));

        petListRepository = new PetListRepository(mockConfigService, mockServerProvider);
    }

    @Test
    public void testInitializeEmptyRepository() {
        // verify
        assertTrue(petListRepository.getPetLists().isEmpty());
        assertTrue(petListRepository.getAllPets().isEmpty());
        assertTrue(petListRepository.hasNoTrackedPets());
    }

    @Test
    public void testCreatePetListForPlayer() {
        // execute
        PetList petList = petListRepository.createPetListForPlayer(playerUUID);

        // verify
        assertEquals(playerUUID, petList.getOwnerUUID());
        assertEquals(1, petListRepository.getPetLists().size());
        assertSame(petList, petListRepository.getPetList(playerUUID));
    }

    @Test
    public void testGetPetListReturnsNullForUnknownPlayer() {
        // verify
        assertNull(petListRepository.getPetList(UUID.randomUUID()));
    }

    @Test
    public void testAddNewPetRegistersPetAndPreparesEntity() {
        // prepare
        petListRepository.createPetListForPlayer(playerUUID);

        // execute
        boolean added = petListRepository.addNewPet(mockPlayer, mockMob);

        // verify
        assertTrue(added);
        assertFalse(petListRepository.hasNoTrackedPets());
        assertEquals(1, petListRepository.getPetList(playerUUID).getNumPets());

        Pet pet = petListRepository.getPet(mockMob);
        assertNotNull(pet);
        assertEquals(entityUUID, pet.getUniqueID());
        assertEquals(playerUUID, pet.getOwnerUUID());
        assertEquals("Daniel's_Pet", pet.getName());
        // The Bukkit location is truncated toward zero when stored as a WpLocation
        assertEquals(10, pet.getLastKnownLocation().getX());
        assertEquals(64, pet.getLastKnownLocation().getY());
        assertEquals(-3, pet.getLastKnownLocation().getZ());

        verify(mockMob).setCustomName(contains("Daniel's_Pet"));
        verify(mockMob).setPersistent(true);
        verify(mockMob).setRemoveWhenFarAway(false);
        verify(mockMob).playEffect(EntityEffect.LOVE_HEARTS);
    }

    @Test
    public void testGetPetReturnsNullForUntrackedEntity() {
        // prepare
        Entity untrackedEntity = mock(Entity.class);
        when(untrackedEntity.getUniqueId()).thenReturn(UUID.randomUUID());

        // verify
        assertNull(petListRepository.getPet(untrackedEntity));
    }

    @Test
    public void testAddExistingPetCreatesMissingPetList() {
        // prepare
        Pet pet = createPet(playerUUID, "Daniel");

        // execute
        petListRepository.addExistingPet(pet);

        // verify
        assertNotNull(petListRepository.getPetList(playerUUID));
        assertSame(pet, petListRepository.getPetList(playerUUID).getPet(pet.getUniqueID()));
        assertFalse(petListRepository.hasNoTrackedPets());
    }

    @Test
    public void testAddExistingPetReusesPetList() {
        // prepare
        PetList petList = petListRepository.createPetListForPlayer(playerUUID);

        // execute
        petListRepository.addExistingPet(createPet(playerUUID, "Daniel"));
        petListRepository.addExistingPet(createPet(playerUUID, "Daniel"));

        // verify
        assertEquals(1, petListRepository.getPetLists().size());
        assertEquals(2, petList.getNumPets());
    }

    @Test
    public void testGetAllPetsSpansEveryPetList() {
        // prepare
        UUID otherPlayerUUID = UUID.randomUUID();
        Pet firstPet = createPet(playerUUID, "Daniel");
        Pet secondPet = createPet(otherPlayerUUID, "Other");
        petListRepository.addExistingPet(firstPet);
        petListRepository.addExistingPet(secondPet);

        // execute & verify
        assertEquals(2, petListRepository.getAllPets().size());
        assertTrue(petListRepository.getAllPets().contains(firstPet));
        assertTrue(petListRepository.getAllPets().contains(secondPet));
    }

    @Test
    public void testGetPlayersPetByEntityAndByName() {
        // prepare
        Pet pet = createPet(playerUUID, "Daniel");
        petListRepository.addExistingPet(pet);
        Entity petEntity = mock(Entity.class);
        when(petEntity.getUniqueId()).thenReturn(pet.getUniqueID());

        // execute & verify
        assertSame(pet, petListRepository.getPlayersPet(mockPlayer, petEntity));
        assertSame(pet, petListRepository.getPlayersPet(mockPlayer, "Daniel's_Pet"));
        assertNull(petListRepository.getPlayersPet(mockPlayer, "Nobodys_Pet"));
    }

    @Test
    public void testClearAllEmptiesListsAndIndex() {
        // prepare
        petListRepository.addExistingPet(createPet(playerUUID, "Daniel"));

        // execute
        petListRepository.clearAll();

        // verify
        assertTrue(petListRepository.getPetLists().isEmpty());
        assertTrue(petListRepository.hasNoTrackedPets());
    }

    @Test
    public void testTransferPetMovesPetAndOwnership() {
        // prepare
        UUID newOwnerUUID = UUID.randomUUID();
        Pet pet = createPet(playerUUID, "Daniel");
        petListRepository.addExistingPet(pet);
        petListRepository.createPetListForPlayer(newOwnerUUID);

        // execute
        boolean transferred = petListRepository.transferPet(pet, newOwnerUUID);

        // verify
        assertTrue(transferred);
        assertEquals(0, petListRepository.getPetList(playerUUID).getNumPets());
        assertEquals(1, petListRepository.getPetList(newOwnerUUID).getNumPets());
        assertEquals(newOwnerUUID, pet.getOwnerUUID());
        assertTrue(pet.hasAccess(newOwnerUUID));
        assertFalse(pet.hasAccess(playerUUID));
    }

    @Test
    public void testTransferPetCreatesPetListForRecipientWithoutOne() {
        // prepare
        UUID newOwnerUUID = UUID.randomUUID();
        Pet pet = createPet(playerUUID, "Daniel");
        petListRepository.addExistingPet(pet);

        // execute
        boolean transferred = petListRepository.transferPet(pet, newOwnerUUID);

        // verify
        assertTrue(transferred);
        assertNotNull(petListRepository.getPetList(newOwnerUUID));
        assertSame(pet, petListRepository.getPetList(newOwnerUUID).getPet(pet.getUniqueID()));
    }

    @Test
    public void testTransferPetKeepsEntityIndexIntact() {
        // prepare
        UUID newOwnerUUID = UUID.randomUUID();
        Pet pet = createPet(playerUUID, "Daniel");
        petListRepository.addExistingPet(pet);
        Entity petEntity = mock(Entity.class);
        when(petEntity.getUniqueId()).thenReturn(pet.getUniqueID());

        // execute
        petListRepository.transferPet(pet, newOwnerUUID);

        // verify - the entity UUID index is keyed by entity, so it survives an ownership change
        assertSame(pet, petListRepository.getPet(petEntity));
    }

    @Test
    public void testTransferPetRollsBackWhenRecipientIsAtPetLimit() {
        // prepare
        when(mockConfigService.getInt("petLimit")).thenReturn(1);
        UUID newOwnerUUID = UUID.randomUUID();
        Pet pet = createPet(playerUUID, "Daniel");
        petListRepository.addExistingPet(pet);
        petListRepository.addExistingPet(createPet(newOwnerUUID, "Other"));

        // execute
        boolean transferred = petListRepository.transferPet(pet, newOwnerUUID);

        // verify - the pet stays with its original owner and keeps its original access list
        assertFalse(transferred);
        assertEquals(1, petListRepository.getPetList(playerUUID).getNumPets());
        assertSame(pet, petListRepository.getPetList(playerUUID).getPet(pet.getUniqueID()));
        assertEquals(1, petListRepository.getPetList(newOwnerUUID).getNumPets());
        assertEquals(playerUUID, pet.getOwnerUUID());
        assertTrue(pet.hasAccess(playerUUID));
    }

    @Test
    public void testTransferPetFailsWhenPetIsNotInItsOwnersList() {
        // prepare - the pet claims an owner that has no pet list at all
        Pet pet = createPet(UUID.randomUUID(), "Ghost");

        // execute & verify
        assertFalse(petListRepository.transferPet(pet, playerUUID));
    }

    @Test
    public void testRemovePetReturnsFalseWhenOwnerHasNoPetList() {
        // prepare
        Pet pet = createPet(UUID.randomUUID(), "Ghost");

        // execute & verify
        assertFalse(petListRepository.removePet(pet));
    }

    @Test
    public void testRemovePetClearsEntityStateAndDropsIndexEntry() {
        // prepare
        petListRepository.createPetListForPlayer(playerUUID);
        petListRepository.addNewPet(mockPlayer, mockMob);
        Pet pet = petListRepository.getPetList(playerUUID).getPet(entityUUID);
        when(mockServer.getEntity(entityUUID)).thenReturn(mockMob);

        // execute
        boolean removed = petListRepository.removePet(pet);

        // verify
        assertTrue(removed);
        assertEquals(0, petListRepository.getPetList(playerUUID).getNumPets());
        assertTrue(petListRepository.hasNoTrackedPets());
        verify(mockMob).setCustomName("");
        verify(mockMob).setInvulnerable(false);
    }

    @Test
    public void testRemovePetReturnsFalseWhenPetIsNotInItsOwnersList() {
        // prepare - the owner has a list, but the pet was never added to it
        petListRepository.createPetListForPlayer(playerUUID);
        Pet pet = createPet(playerUUID, "Daniel");

        // execute & verify
        assertFalse(petListRepository.removePet(pet));
    }

    private Pet createPet(UUID ownerUUID, String ownerName) {
        return new Pet(UUID.randomUUID(), ownerUUID, ownerName, mockServerProvider);
    }
}
