package dansplugins.wildpets.storage;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.exceptions.PetRecordNotFoundException;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.location.WpLocation;
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

    private StorageService createLoadedStorageService(PetRecordRepository recordRepository) {
        StorageService storageService = createStorageService(recordRepository);
        assertTrue(storageService.readFromDisk());
        return storageService;
    }

    private static byte[] read(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    private static void write(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testSaveWritesRecordsFromTheInjectedRepository() throws IOException {
        // prepare
        StorageService storageService = createLoadedStorageService(petRecordRepository);
        petRecordRepository.addPetRecord(pet);

        // execute
        storageService.save();

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
        StorageService storageService = createLoadedStorageService(petRecordRepository);
        petRecordRepository.addExistingPetRecord(savedRecord);
        storageService.save();

        PetRecordRepository restoredRepository = new PetRecordRepository();

        // execute
        createStorageService(restoredRepository).readFromDisk();

        // verify
        PetRecord restored = restoredRepository.getPetRecord(pet.getUniqueID());
        assertEquals("Departed_Parent", restored.getName());
        assertEquals(tradedOwner, restored.getOwnerUUID());
        assertEquals(7, restored.getAssignedID());
    }

    @Test
    public void testLoadPetRecordsDiscardsRecordsNotOnDisk() {
        // prepare - the file on disk holds only the saved pet
        StorageService storageService = createLoadedStorageService(petRecordRepository);
        petRecordRepository.addPetRecord(pet);
        storageService.save();
        Pet stalePet = new Pet(UUID.randomUUID(), UUID.randomUUID(), "Stale", mockServerProvider);
        petRecordRepository.addPetRecord(stalePet);

        // execute
        storageService.readFromDisk();

        // verify
        assertEquals(1, petRecordRepository.getPetRecords().size());
        assertTrue(petRecordRepository.getPetRecords().contains(new PetRecord(pet)));
    }

    @Test
    public void testLoadPetRecordsWithNoFileLeavesRepositoryEmpty() {
        // execute
        boolean loaded = createStorageService(petRecordRepository).readFromDisk();

        // verify
        assertTrue(loaded);
        assertTrue(petRecordRepository.getPetRecords().isEmpty());
    }

    @Test
    public void testPetsSurviveASaveAndLoadRoundTrip() {
        // prepare - a pet with every saved field away from its default
        StorageService storageService = createLoadedStorageService(petRecordRepository);
        UUID friend = UUID.randomUUID();
        pet.setName("Biscuit");
        pet.setStaying();
        pet.setLocked(true);
        pet.addToAccessList(friend);
        pet.setLastKnownLocation(new WpLocation(12, 64, -300));
        petListRepository.addExistingPet(pet);
        petRecordRepository.addPetRecord(pet);
        assertTrue(storageService.save());

        PetListRepository restoredPets = new PetListRepository(mockConfigService, mockServerProvider);
        PetRecordRepository restoredRecords = new PetRecordRepository();

        // execute
        boolean loaded = new StorageService(mockConfigService, null, restoredPets, restoredRecords, filePath).readFromDisk();

        // verify
        assertTrue(loaded);
        assertEquals(1, restoredPets.getAllPets().size());
        Pet restored = restoredPets.getAllPets().get(0);
        assertEquals(pet.getUniqueID(), restored.getUniqueID());
        assertEquals(pet.getOwnerUUID(), restored.getOwnerUUID());
        assertEquals("Biscuit", restored.getName());
        assertEquals(7, restored.getAssignedID());
        assertEquals("Staying", restored.getMovementState());
        assertTrue(restored.isLocked());
        assertTrue(restored.getAccessList().contains(friend));
        assertTrue(restored.getAccessList().contains(pet.getOwnerUUID()));
        assertEquals(new WpLocation(12, 64, -300), restored.getLastKnownLocation());
        assertEquals(1, restoredRecords.getPetRecords().size());
    }

    @Test
    public void testSaveBeforeAnyLoadIsRefusedAndLeavesTheFilesAlone() throws IOException {
        // prepare - data from a previous run, and a service whose load never ran (onEnable failed before it)
        File petsFile = new File(filePath + "pets.json");
        String previousRun = "[{\"uniqueID\":\"\\\"" + UUID.randomUUID() + "\\\"\"}]";
        write(petsFile, previousRun);
        StorageService storageService = createStorageService(petRecordRepository);

        // execute
        boolean saved = storageService.save();

        // verify
        assertFalse(saved);
        assertTrue(storageService.isLoadFailed());
        assertEquals(previousRun, new String(read(petsFile), StandardCharsets.UTF_8));
        assertFalse(new File(filePath + "petRecords.json").exists());
    }

    @Test
    public void testUnparseablePetsFileFailsTheLoadAndIsNeverOverwritten() throws IOException {
        // prepare - a good save, then pets.json cut off mid-write
        StorageService storageService = createLoadedStorageService(petRecordRepository);
        petListRepository.addExistingPet(pet);
        petRecordRepository.addPetRecord(pet);
        storageService.save();
        File petsFile = new File(filePath + "pets.json");
        File recordsFile = new File(filePath + "petRecords.json");
        String full = new String(read(petsFile), StandardCharsets.UTF_8);
        write(petsFile, full.substring(0, full.length() / 2));
        byte[] truncated = read(petsFile);
        byte[] records = read(recordsFile);

        PetListRepository restartedPets = new PetListRepository(mockConfigService, mockServerProvider);
        PetRecordRepository restartedRecords = new PetRecordRepository();
        StorageService restarted = new StorageService(mockConfigService, null, restartedPets, restartedRecords, filePath);

        // execute - load on the next start, then the autosave / onDisable save
        boolean loaded = restarted.readFromDisk();
        boolean saved = restarted.save();

        // verify
        assertFalse(loaded);
        assertFalse(saved);
        assertTrue(restartedPets.getAllPets().isEmpty());
        assertTrue(restartedRecords.getPetRecords().isEmpty());
        assertArrayEquals(truncated, read(petsFile));
        assertArrayEquals(records, read(recordsFile));
    }

    @Test
    public void testMalformedEntryFailsTheWholeLoadAndLeavesMemoryUntouched() throws IOException {
        // prepare - an already loaded service, then a pet record whose UUID does not parse
        StorageService storageService = createLoadedStorageService(petRecordRepository);
        petListRepository.addExistingPet(pet);
        petRecordRepository.addPetRecord(pet);
        storageService.save();
        File recordsFile = new File(filePath + "petRecords.json");
        write(recordsFile, "[{\"uniqueID\":\"\\\"not-a-uuid\\\"\"}]");

        // execute
        boolean loaded = storageService.readFromDisk();

        // verify - the pets already in memory are kept, and the next save is refused
        assertFalse(loaded);
        assertEquals(1, petListRepository.getAllPets().size());
        assertEquals(1, petRecordRepository.getPetRecords().size());
        assertFalse(storageService.save());
        assertTrue(new String(read(recordsFile), StandardCharsets.UTF_8).contains("not-a-uuid"));
    }

    @Test
    public void testEmptyFilesLoadAsNoData() throws IOException {
        // prepare - zero-byte files, as a write cut off before it began would leave
        write(new File(filePath + "pets.json"), "");
        write(new File(filePath + "petRecords.json"), "");

        // execute
        boolean loaded = createStorageService(petRecordRepository).readFromDisk();

        // verify
        assertTrue(loaded);
        assertTrue(petListRepository.getAllPets().isEmpty());
    }

    @Test
    public void testSaveLeavesNoTemporaryFilesBehind() {
        // prepare
        StorageService storageService = createLoadedStorageService(petRecordRepository);
        petListRepository.addExistingPet(pet);
        petRecordRepository.addPetRecord(pet);

        // execute
        assertTrue(storageService.save());

        // verify
        String[] names = new File(filePath).list();
        assertNotNull(names);
        for (String name : names) {
            assertFalse(name, name.endsWith(".tmp"));
        }
    }
}
