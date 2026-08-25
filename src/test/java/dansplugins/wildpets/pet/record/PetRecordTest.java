package dansplugins.wildpets.pet.record;

import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import org.bukkit.Server;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PetRecordTest {
    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    private UUID entityUUID;
    private UUID ownerUUID;
    private Pet pet;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getEntity(any(UUID.class))).thenReturn(null);

        entityUUID = UUID.randomUUID();
        ownerUUID = UUID.randomUUID();
        pet = new Pet(entityUUID, ownerUUID, "Daniel", mockServerProvider);
        pet.setAssignedID(42);
    }

    @Test
    public void testInitializeFromPet() {
        // execute
        PetRecord petRecord = new PetRecord(pet);

        // verify
        assertEquals(entityUUID, petRecord.getUniqueID());
        assertEquals(ownerUUID, petRecord.getOwnerUUID());
        assertEquals(42, petRecord.getAssignedID());
        assertEquals("Daniel's_Pet", petRecord.getName());
    }

    @Test
    public void testSaveToJson() {
        // prepare
        PetRecord petRecord = new PetRecord(pet);

        // execute
        Map<String, String> data = petRecord.save();

        // verify
        assertEquals("\"" + entityUUID + "\"", data.get("uniqueID"));
        assertEquals("\"" + ownerUUID + "\"", data.get("owner"));
        assertEquals("42", data.get("assignedID"));
        assertEquals("\"Daniel\\u0027s_Pet\"", data.get("name"));
    }

    @Test
    public void testSaveAndLoadRoundTrip() {
        // prepare
        Map<String, String> data = new PetRecord(pet).save();

        // execute
        PetRecord loadedRecord = new PetRecord(data);

        // verify
        assertEquals(entityUUID, loadedRecord.getUniqueID());
        assertEquals(ownerUUID, loadedRecord.getOwnerUUID());
        assertEquals(42, loadedRecord.getAssignedID());
        assertEquals("Daniel's_Pet", loadedRecord.getName());
    }

    @Test
    public void testSetters() {
        // prepare
        PetRecord petRecord = new PetRecord(pet);
        UUID newUniqueID = UUID.randomUUID();
        UUID newOwnerUUID = UUID.randomUUID();

        // execute
        petRecord.setUniqueID(newUniqueID);
        petRecord.setOwnerUUID(newOwnerUUID);
        petRecord.setAssignedID(7);
        petRecord.setName("Renamed_Pet");

        // verify
        assertEquals(newUniqueID, petRecord.getUniqueID());
        assertEquals(newOwnerUUID, petRecord.getOwnerUUID());
        assertEquals(7, petRecord.getAssignedID());
        assertEquals("Renamed_Pet", petRecord.getName());
    }

    @Test
    public void testEqualityIsBasedOnUniqueIDAlone() {
        // prepare
        PetRecord petRecord = new PetRecord(pet);
        PetRecord sameEntityRecord = new PetRecord(pet);
        sameEntityRecord.setName("A_Different_Name");
        sameEntityRecord.setOwnerUUID(UUID.randomUUID());
        sameEntityRecord.setAssignedID(99);

        PetRecord otherEntityRecord = new PetRecord(pet);
        otherEntityRecord.setUniqueID(UUID.randomUUID());

        // verify
        assertEquals(petRecord, sameEntityRecord);
        assertNotEquals(petRecord, otherEntityRecord);
    }

    @Test
    public void testEqualityAgainstNullAndOtherTypes() {
        // prepare
        PetRecord petRecord = new PetRecord(pet);

        // verify
        assertNotEquals(null, petRecord);
        assertNotEquals("not a pet record", petRecord);
    }

    @Test
    public void testHashCodeIsBasedOnUniqueIDAlone() {
        // prepare - equal records must agree on hashCode, whatever else differs
        PetRecord petRecord = new PetRecord(pet);
        PetRecord sameEntityRecord = new PetRecord(pet);
        sameEntityRecord.setName("A_Different_Name");
        sameEntityRecord.setOwnerUUID(UUID.randomUUID());
        sameEntityRecord.setAssignedID(99);

        // verify
        assertEquals(petRecord, sameEntityRecord);
        assertEquals(petRecord.hashCode(), sameEntityRecord.hashCode());
    }

    @Test
    public void testEqualRecordsCollapseInAHashSet() {
        // prepare
        PetRecord petRecord = new PetRecord(pet);
        PetRecord sameEntityRecord = new PetRecord(pet);
        sameEntityRecord.setName("A_Different_Name");

        // execute
        Set<PetRecord> records = new HashSet<>();
        records.add(petRecord);
        boolean addedAgain = records.add(sameEntityRecord);

        // verify
        assertFalse(addedAgain);
        assertEquals(1, records.size());
    }
}
