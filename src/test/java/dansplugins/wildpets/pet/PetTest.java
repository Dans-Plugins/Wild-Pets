package dansplugins.wildpets.pet;

import dansplugins.wildpets.helpers.ServerProvider;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PetTest {
    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Mob mockMob;

    @Mock
    private Entity mockEntity; // Non-Mob entity for testing

    private UUID entityUniqueId;
    private UUID playerOwnerUniqueId;
    private String playerOwnerName;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockServerProvider.get()).thenReturn(mockServer);

        // Common test data
        entityUniqueId = UUID.randomUUID();
        when(mockServer.getEntity(entityUniqueId)).thenReturn(null);
        playerOwnerUniqueId = UUID.randomUUID();
        playerOwnerName = "Daniel";
    }

    @Test
    public void testInitializeFromScratch() {
        // execute
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // verify
        assertEquals(entityUniqueId, pet.getUniqueID());
        assertEquals(playerOwnerUniqueId, pet.getOwnerUUID());
        assertEquals(playerOwnerName + "'s_Pet", pet.getName());
        assertEquals("Wandering", pet.getMovementState());
        assertTrue(pet.getAccessList().contains(playerOwnerUniqueId));
        assertEquals(0, pet.getAssignedID()); // Default should be 0
        assertFalse(pet.isLocked()); // Default should be unlocked
    }

    @Test
    public void testSaveToJson() {
        // prepare
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);
        pet.setAssignedID(123);

        // execute
        Map<String, String> petData = pet.save();

        // verify
        assertEquals("\"" + entityUniqueId.toString() + "\"", petData.get("uniqueID"));
        assertEquals("\"" + playerOwnerUniqueId.toString() + "\"", petData.get("owner"));
        assertEquals("123", petData.get("assignedID"));
        assertEquals("\"" + playerOwnerName + "\\u0027s_Pet\"", petData.get("name"));
        assertEquals("0", petData.get("lastKnownX"));
        assertEquals("0", petData.get("lastKnownY"));
        assertEquals("0", petData.get("lastKnownZ"));
        assertEquals("\"Wandering\"", petData.get("movementState"));
        assertEquals("false", petData.get("locked"));
        assertTrue(petData.get("accessList").contains(playerOwnerUniqueId.toString()));
        assertEquals("[]", petData.get("parentIDs"));
        assertEquals("[]", petData.get("childIDs"));
    }

    @Test
    public void testInitializeFromJson() {
        // prepare
        Map<String, String> petData = createBasicPetData("Wandering");

        // Mock the server to return null for entity (entity not found case)
        when(mockServer.getEntity(any(UUID.class))).thenReturn(null);

        // execute
        Pet pet = new Pet(petData, mockServerProvider);

        // verify
        assertEquals(UUID.fromString(petData.get("uniqueID")), pet.getUniqueID());
        assertEquals(UUID.fromString(petData.get("owner")), pet.getOwnerUUID());
        assertEquals(petData.get("name"), pet.getName());
        assertEquals(petData.get("movementState"), pet.getMovementState());
        assertTrue(pet.getAccessList().contains(UUID.fromString(petData.get("owner"))));
        assertTrue(pet.getParentUUIDs().isEmpty());
        assertTrue(pet.getChildUUIDs().isEmpty());
    }

    @Test
    public void testSetStaying() {
        // Mock entity retrieval
        when(mockServer.getEntity(entityUniqueId)).thenReturn(mockMob);

        // Create pet with mocked ServerProvider
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // execute
        pet.setStaying();

        // verify
        assertEquals("Staying", pet.getMovementState());
        verify(mockMob).setAware(false);
    }

    @Test
    public void testSetWandering() {
        // Mock entity retrieval
        when(mockServer.getEntity(entityUniqueId)).thenReturn(mockMob);

        // Create pet and set to Staying first
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);
        pet.setStaying(); // First set to Staying
        verify(mockMob).setAware(false);

        // Reset mock to verify new calls
        reset(mockMob);

        // execute
        pet.setWandering();

        // verify
        assertEquals("Wandering", pet.getMovementState());
        verify(mockMob).setAware(true);
    }

    @Test
    public void testSetFollowing() {
        // Mock entity retrieval
        when(mockServer.getEntity(entityUniqueId)).thenReturn(mockMob);

        // Create pet
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);
        pet.setStaying(); // First set to Staying
        verify(mockMob).setAware(false);

        // Reset mock to verify new calls
        reset(mockMob);

        // execute
        pet.setFollowing();

        // verify
        assertEquals("Following", pet.getMovementState());
        verify(mockMob).setAware(true);
    }

    @Test
    public void testLoadStayingState() {
        // prepare
        Map<String, String> petData = createBasicPetData("Staying");

        // Mock entity retrieval
        when(mockServer.getEntity(UUID.fromString(petData.get("uniqueID")))).thenReturn(mockMob);

        // Create pet with mocked ServerProvider
        Pet pet = new Pet(petData, mockServerProvider);

        // verify
        assertEquals("Staying", pet.getMovementState());
        verify(mockMob).setAware(false);
    }

    @Test
    public void testLoadFollowingState() {
        // prepare
        Map<String, String> petData = createBasicPetData("Following");

        // Mock entity retrieval
        when(mockServer.getEntity(UUID.fromString(petData.get("uniqueID")))).thenReturn(mockMob);

        // Create pet with mocked ServerProvider
        Pet pet = new Pet(petData, mockServerProvider);

        // verify
        assertEquals("Following", pet.getMovementState());
        verify(mockMob).setAware(true);
    }

    @Test
    public void testAccessListFunctionality() {
        // prepare
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);
        UUID newUser = UUID.randomUUID();

        // verify initial state
        assertTrue(pet.hasAccess(playerOwnerUniqueId));
        assertFalse(pet.hasAccess(newUser));

        // add new user
        pet.addToAccessList(newUser);

        // verify after add
        assertTrue(pet.hasAccess(newUser));
        assertEquals(2, pet.getAccessList().size());

        // remove user
        pet.removeFromAccessList(newUser);

        // verify after remove
        assertFalse(pet.hasAccess(newUser));
        assertEquals(1, pet.getAccessList().size());
    }

    @Test
    public void testLockingFunctionality() {
        // prepare
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // verify initial state
        assertFalse(pet.isLocked());

        // lock
        pet.setLocked(true);

        // verify locked
        assertTrue(pet.isLocked());

        // unlock
        pet.setLocked(false);

        // verify unlocked
        assertFalse(pet.isLocked());
    }

    @Test
    public void testFamilialRelationships() {
        // prepare
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);
        UUID parent1 = UUID.randomUUID();
        UUID parent2 = UUID.randomUUID();
        UUID child1 = UUID.randomUUID();

        // Add parents and children
        pet.addParent(parent1);
        pet.addParent(parent2);
        pet.addChild(child1);

        // verify
        assertEquals(2, pet.getParentUUIDs().size());
        assertTrue(pet.getParentUUIDs().contains(parent1));
        assertTrue(pet.getParentUUIDs().contains(parent2));
        assertEquals(1, pet.getChildUUIDs().size());
        assertTrue(pet.getChildUUIDs().contains(child1));

        // Test string methods
        String parentString = pet.getParentsUUIDsSeparatedByCommas();
        assertTrue(parentString.contains(parent1.toString()));
        assertTrue(parentString.contains(parent2.toString()));
        assertTrue(parentString.contains(", ")); // Ensure comma separation

        String childString = pet.getChildrenUUIDsSeparatedByCommas();
        assertTrue(childString.contains(child1.toString()));
        assertFalse(childString.contains(", ")); // Only one child, no comma
    }

    @Test
    public void testApplyAIStateWithNonMobEntity() {
        // prepare - mockEntity is not a Mob
        when(mockServer.getEntity(entityUniqueId)).thenReturn(mockEntity);

        // Create pet
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // execute - this should not throw an exception
        pet.setStaying();

        // verify movement state was changed, but no exception occurred
        assertEquals("Staying", pet.getMovementState());
        // We can't verify setAware wasn't called because mockEntity doesn't have that method
    }

    @Test
    public void testApplyAIStateWithNullEntity() {
        // prepare - entity not found
        when(mockServer.getEntity(entityUniqueId)).thenReturn(null);

        // Create pet
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // execute - this should not throw an exception
        pet.setStaying();

        // verify movement state was changed, but no exception occurred
        assertEquals("Staying", pet.getMovementState());
    }

    @Test
    public void testEnsurePersistenceWithMob() {
        // prepare - Mob extends LivingEntity
        when(mockServer.getEntity(entityUniqueId)).thenReturn(mockMob);

        // Create pet
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // execute
        pet.ensurePersistence();

        // verify
        verify(mockMob).setPersistent(true);
        verify(mockMob).setRemoveWhenFarAway(false);
    }

    @Test
    public void testEnsurePersistenceWithNonLivingEntity() {
        // prepare - mockEntity is just Entity, not LivingEntity
        when(mockServer.getEntity(entityUniqueId)).thenReturn(mockEntity);

        // Create pet
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // execute
        pet.ensurePersistence();

        // verify setPersistent is called, but setRemoveWhenFarAway is not (not a LivingEntity)
        verify(mockEntity).setPersistent(true);
    }

    @Test
    public void testEnsurePersistenceWithNullEntity() {
        // prepare - entity not found (e.g., in unloaded chunk)
        when(mockServer.getEntity(entityUniqueId)).thenReturn(null);

        // Create pet
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // execute - should not throw an exception
        pet.ensurePersistence();

        // verify no interactions attempted on null entity
    }

    @Test
    public void testEnsurePersistenceWithNullServerProvider() {
        // Create pet with null server provider
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, null);

        // execute - should not throw an exception
        pet.ensurePersistence();

        // verify no exception occurred
    }

    // Helper method to create test data
    private Map<String, String> createBasicPetData(String movementState) {
        Map<String, String> petData = new HashMap<>();
        petData.put("uniqueID", entityUniqueId.toString());
        petData.put("owner", playerOwnerUniqueId.toString());
        petData.put("assignedID", "0");
        petData.put("name", "Daniel's_Pet");
        petData.put("lastKnownX", "0");
        petData.put("lastKnownY", "0");
        petData.put("lastKnownZ", "0");
        petData.put("movementState", movementState);
        petData.put("locked", "false");
        petData.put("accessList", "[\"" + petData.get("owner") + "\"]");
        petData.put("parentIDs", "[]");
        petData.put("childIDs", "[]");
        return petData;
    }
}