package dansplugins.wildpets.pet;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetTest {

    @Test
    public void testInitializeFromScratch() {
        // prepare
        UUID entityUniqueId = UUID.randomUUID();
        UUID playerOwnerUniqueId = UUID.randomUUID();
        String playerOwnerName = "Daniel";

        // execute
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName);

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
        Pet pet = new Pet(entityUniqueId, playerOwnerUniqueId, playerOwnerName);

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
        Map<String, String> petData = new HashMap<>();
        petData.put("uniqueID", UUID.randomUUID().toString());
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

        // execute
        Pet pet = new Pet(petData);

        // verify
        assert pet.getUniqueID().equals(UUID.fromString(petData.get("uniqueID")));
        assert pet.getOwnerUUID().equals(UUID.fromString(petData.get("owner")));
        assert pet.getName().equals(petData.get("name"));
        assert pet.getMovementState().equals(petData.get("movementState"));
        assert pet.getAccessList().contains(UUID.fromString(petData.get("owner")));
        assert pet.getParentUUIDs().isEmpty();
        assert pet.getChildUUIDs().isEmpty();
    }
}
