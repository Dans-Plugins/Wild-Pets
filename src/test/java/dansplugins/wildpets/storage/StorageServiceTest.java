package dansplugins.wildpets.storage;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.exceptions.PetRecordNotFoundException;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.record.PetRecord;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import org.bukkit.Server;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StorageServiceTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    private String filePath;
    private PetListRepository petListRepository;
    private PetRecordRepository petRecordRepository;
    private Pet pet;

    @Before
    public void setup() throws IOException {
        MockitoAnnotations.openMocks(this);
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getEntity(any(UUID.class))).thenReturn(null);
        when(mockConfigService.hasBeenAltered()).thenReturn(false);

        filePath = temporaryFolder.newFolder("WildPets").getPath() + File.separator;
        petListRepository = new PetListRepository(mockConfigService, mockServerProvider);
        petRecordRepository = new PetRecordRepository();

        pet = new Pet(UUID.randomUUID(), UUID.randomUUID(), "Daniel", mockServerProvider);
        pet.setAssignedID(7);
    }

    private StorageService createStorageService(PetRecordRepository recordRepository) {
        return new StorageService(mockConfigService, null, petListRepository, recordRepository, filePath);
    }

    @Test
    public void testSaveWritesRecordsFromTheInjectedRepository() throws IOException {
        // prepare
        petRecordRepository.addPetRecord(pet);

        // execute
        createStorageService(petRecordRepository).save();

        // verify
        String written = new String(Files.readAllBytes(new File(filePath + "petRecords.json").toPath()), StandardCharsets.UTF_8);
        assertTrue(written.contains(pet.getUniqueID().toString()));
    }

    @Test
    public void testLoadPetRecordsRestoresSavedRecordsIntoTheInjectedRepository() throws PetRecordNotFoundException {
        // prepare - a record whose state is no longer derivable from any live pet (e.g. a dead parent)
        PetRecord savedRecord = new PetRecord(pet);
        savedRecord.setName("Departed_Parent");
        UUID tradedOwner = UUID.randomUUID();
        savedRecord.setOwnerUUID(tradedOwner);
        petRecordRepository.addExistingPetRecord(savedRecord);
        createStorageService(petRecordRepository).save();

        PetRecordRepository restoredRepository = new PetRecordRepository();

        // execute
        createStorageService(restoredRepository).loadPetRecords();

        // verify
        PetRecord restored = restoredRepository.getPetRecord(pet.getUniqueID());
        assertEquals("Departed_Parent", restored.getName());
        assertEquals(tradedOwner, restored.getOwnerUUID());
        assertEquals(7, restored.getAssignedID());
    }

    @Test
    public void testLoadPetRecordsDiscardsRecordsNotOnDisk() {
        // prepare - the file on disk holds only the saved pet
        petRecordRepository.addPetRecord(pet);
        StorageService storageService = createStorageService(petRecordRepository);
        storageService.save();
        Pet stalePet = new Pet(UUID.randomUUID(), UUID.randomUUID(), "Stale", mockServerProvider);
        petRecordRepository.addPetRecord(stalePet);

        // execute
        storageService.loadPetRecords();

        // verify
        assertEquals(1, petRecordRepository.getPetRecords().size());
        assertTrue(petRecordRepository.getPetRecords().contains(new PetRecord(pet)));
    }

    @Test
    public void testLoadPetRecordsWithNoFileLeavesRepositoryEmpty() {
        // execute
        createStorageService(petRecordRepository).loadPetRecords();

        // verify
        assertTrue(petRecordRepository.getPetRecords().isEmpty());
    }
}
