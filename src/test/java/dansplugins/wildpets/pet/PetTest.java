
package dansplugins.wildpets.pet;

import dansplugins.wildpets.helpers.ServerProvider;
import org.bukkit.Server;
import org.bukkit.entity.Mob;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class PetTest {
    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Mob mockMob;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockServerProvider.get()).thenReturn(mockServer);
    }

    @Test
    public void testInitializeFromScratch() {
        // prepare
        UUID entityUniqueId = UUID.randomUUID();
        UUID playerOwnerUniqueId = UUID.randomUUID();
        String playerOwnerName = "Daniel";

        // execute - use mock BukkitServer to avoid NPE
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // verify
        assert pet.getUniqueID().equals(entityUniqueId);
        assert pet.getOwnerUUID().equals(playerOwnerUniqueId);
        assert pet.getName().equals(playerOwnerName + "'s_Pet");
        assert pet.getMovementState().equals("Wandering");
        assert pet.getAccessList().contains(playerOwnerUniqueId);
    }

    @Test
    public void testSaveToJson() {
        // prepare
        UUID entityUniqueId = UUID.randomUUID();
        UUID playerOwnerUniqueId = UUID.randomUUID();
        String playerOwnerName = "Daniel";

        // Use mock BukkitServer to avoid NPE
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // execute
        Map<String, String> petData = pet.save();

        // verify
        assert petData.get("uniqueID").equals("\"" + entityUniqueId.toString() + "\"");
        assert petData.get("owner").equals("\"" + playerOwnerUniqueId.toString() + "\"");
        assert petData.get("assignedID").equals("0");
        assert petData.get("name").equals("\"" + playerOwnerName + "\\u0027s_Pet\"");
        assert petData.get("lastKnownX").equals("0");
        assert petData.get("lastKnownY").equals("0");
        assert petData.get("lastKnownZ").equals("0");
        assert petData.get("movementState").equals("\"Wandering\"");
        assert petData.get("locked").equals("false");
        assert petData.get("accessList").contains(playerOwnerUniqueId.toString());
        assert petData.get("parentIDs").equals("[]");
        assert petData.get("childIDs").equals("[]");
    }

    @Test
    public void testInitializeFromJson() {
        // prepare
        UUID entityId = UUID.randomUUID();
        Map<String, String> petData = new HashMap<>();
        petData.put("uniqueID", entityId.toString());
        petData.put("owner", UUID.randomUUID().toString());
        petData.put("assignedID", "0");
        petData.put("name", "Daniel's_Pet");
        petData.put("lastKnownX", "0");
        petData.put("lastKnownY", "0");
        petData.put("lastKnownZ", "0");
        petData.put("movementState", "Wandering");
        petData.put("locked", "false");
        petData.put("accessList", "[\"" + petData.get("owner") + "\"]");
        petData.put("parentIDs", "[]");
        petData.put("childIDs", "[]");

        // Mock the server to return null for entity (entity not found case)
        when(mockServer.getEntity(any(UUID.class))).thenReturn(null);

        // execute - use mock BukkitServer to avoid NPE
        Pet pet = new Pet(petData, mockServerProvider);

        // verify
        assert pet.getUniqueID().equals(UUID.fromString(petData.get("uniqueID")));
        assert pet.getOwnerUUID().equals(UUID.fromString(petData.get("owner")));
        assert pet.getName().equals(petData.get("name"));
        assert pet.getMovementState().equals(petData.get("movementState"));
        assert pet.getAccessList().contains(UUID.fromString(petData.get("owner")));
        assert pet.getParentUUIDs().isEmpty();
        assert pet.getChildUUIDs().isEmpty();
    }

    @Test
    public void testSetStaying() {
        // prepare
        UUID entityUniqueId = UUID.randomUUID();
        UUID playerOwnerUniqueId = UUID.randomUUID();
        String playerOwnerName = "Daniel";

        // Mock entity retrieval
        when(mockServer.getEntity(entityUniqueId)).thenReturn(mockMob);

        // Create pet with mocked BukkitServer
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName, mockServerProvider);

        // execute
        pet.setStaying();

        // verify
        assert pet.getMovementState().equals("Staying");
        verify(mockMob).setAware(false);
    }

    @Test
    public void testLoadStayingState() {
        // prepare
        UUID entityUniqueId = UUID.randomUUID();
        Map<String, String> petData = new HashMap<>();
        petData.put("uniqueID", entityUniqueId.toString());
        petData.put("owner", UUID.randomUUID().toString());
        petData.put("assignedID", "0");
        petData.put("name", "Daniel's_Pet");
        petData.put("lastKnownX", "0");
        petData.put("lastKnownY", "0");
        petData.put("lastKnownZ", "0");
        petData.put("movementState", "Staying");
        petData.put("locked", "false");
        petData.put("accessList", "[\"" + petData.get("owner") + "\"]");
        petData.put("parentIDs", "[]");
        petData.put("childIDs", "[]");

        // Mock entity retrieval
        when(mockServer.getEntity(UUID.fromString(petData.get("uniqueID")))).thenReturn(mockMob);

        // Create pet with mocked BukkitServer
        Pet pet = new Pet(petData, mockServerProvider);

        // verify
        assert pet.getMovementState().equals("Staying");
        verify(mockMob).setAware(false);
    }
}