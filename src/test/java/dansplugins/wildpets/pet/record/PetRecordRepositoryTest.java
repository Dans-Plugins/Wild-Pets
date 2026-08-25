package dansplugins.wildpets.pet.record;

import dansplugins.wildpets.exceptions.PetRecordNotFoundException;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import org.bukkit.Server;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PetRecordRepositoryTest {
    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    private UUID entityUUID;
    private Pet pet;
    private PetRecordRepository petRecordRepository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getEntity(any(UUID.class))).thenReturn(null);

        entityUUID = UUID.randomUUID();
        pet = new Pet(entityUUID, UUID.randomUUID(), "Daniel", mockServerProvider);
        pet.setAssignedID(42);
        petRecordRepository = new PetRecordRepository();
    }

    @Test
    public void testInitializeEmptyRepository() {
        // verify
        assertTrue(petRecordRepository.getPetRecords().isEmpty());
    }

    @Test
    public void testAddPetRecord() throws PetRecordNotFoundException {
        // execute
        boolean added = petRecordRepository.addPetRecord(pet);

        // verify
        assertTrue(added);
        assertEquals(1, petRecordRepository.getPetRecords().size());
        assertEquals(42, petRecordRepository.getPetRecord(entityUUID).getAssignedID());
    }

    @Test(expected = PetRecordNotFoundException.class)
    public void testGetPetRecordThrowsWhenAbsent() throws PetRecordNotFoundException {
        // execute
        petRecordRepository.getPetRecord(UUID.randomUUID());
    }

    @Test
    public void testRemovePetRecord() {
        // prepare
        petRecordRepository.addPetRecord(pet);

        // execute
        boolean removed = petRecordRepository.removePetRecord(entityUUID);

        // verify
        assertTrue(removed);
        assertTrue(petRecordRepository.getPetRecords().isEmpty());
    }

    @Test
    public void testRemovePetRecordReturnsFalseWhenAbsent() {
        // prepare
        petRecordRepository.addPetRecord(pet);

        // execute & verify
        assertFalse(petRecordRepository.removePetRecord(UUID.randomUUID()));
        assertEquals(1, petRecordRepository.getPetRecords().size());
    }

    @Test
    public void testRemovePetRecordOnlyRemovesTheMatchingRecord() throws PetRecordNotFoundException {
        // prepare
        Pet otherPet = new Pet(UUID.randomUUID(), UUID.randomUUID(), "Other", mockServerProvider);
        petRecordRepository.addPetRecord(pet);
        petRecordRepository.addPetRecord(otherPet);

        // execute
        petRecordRepository.removePetRecord(entityUUID);

        // verify
        assertEquals(1, petRecordRepository.getPetRecords().size());
        assertEquals(otherPet.getUniqueID(), petRecordRepository.getPetRecord(otherPet.getUniqueID()).getUniqueID());
    }

    @Test
    public void testGetOrCreatePetRecordCreatesWhenAbsent() throws PetRecordNotFoundException {
        // execute
        PetRecord record = petRecordRepository.getOrCreatePetRecord(pet);

        // verify
        assertEquals(entityUUID, record.getUniqueID());
        assertEquals(1, petRecordRepository.getPetRecords().size());
    }

    @Test
    public void testGetOrCreatePetRecordReturnsExistingRecord() throws PetRecordNotFoundException {
        // prepare - an existing record whose state has since diverged from the live pet
        petRecordRepository.addPetRecord(pet);
        petRecordRepository.getPetRecord(entityUUID).setName("Stored_Name");
        pet.setName("Live_Name");

        // execute
        PetRecord record = petRecordRepository.getOrCreatePetRecord(pet);

        // verify - the stored record wins; no second record is created
        assertEquals("Stored_Name", record.getName());
        assertEquals(1, petRecordRepository.getPetRecords().size());
    }

    @Test
    public void testAddPetRecordTwiceDoesNotStoreDuplicateRecords() {
        // PetRecord keys both equals() and hashCode() on uniqueID, so the backing HashSet
        // recognises that two records describe the same entity and keeps only one.

        // execute
        petRecordRepository.addPetRecord(pet);
        boolean addedAgain = petRecordRepository.addPetRecord(pet);

        // verify
        assertFalse(addedAgain);
        assertEquals(1, petRecordRepository.getPetRecords().size());
    }

    @Test
    public void testRepeatedAddsAfterRenameKeepASingleRecord() {
        // prepare - RenameCommand re-adds the record on every successful rename
        petRecordRepository.addPetRecord(pet);

        // execute
        for (int rename = 0; rename < 5; rename++) {
            pet.setName("Renamed_" + rename);
            petRecordRepository.addPetRecord(pet);
        }

        // verify - the record set does not grow with each rename
        assertEquals(1, petRecordRepository.getPetRecords().size());
    }
}
