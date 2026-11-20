package dansplugins.wildpets.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.record.PetRecord;
import dansplugins.wildpets.pet.record.PetRecordRepository;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Daniel McCoy Stephenson
 */
public class StorageService {
    private final ConfigService configService;
    private final WildPets wildPets;
    private final PetListRepository petListRepository;
    private final PetRecordRepository petRecordRepository;

    private final static String DEFAULT_FILE_PATH = "./plugins/WildPets/";
    private final static String PETS_FILE_NAME = "pets.json";
    private final static String PET_RECORDS_FILE_NAME = "petRecords.json";

    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    private final String filePath;

    // Cleared only by a successful load. Until then save() refuses to run (see save()).
    private boolean loadFailed = true;

    public StorageService(ConfigService configService, WildPets wildPets, PetListRepository petListRepository, PetRecordRepository petRecordRepository) {
        this(configService, wildPets, petListRepository, petRecordRepository, DEFAULT_FILE_PATH);
    }

    StorageService(ConfigService configService, WildPets wildPets, PetListRepository petListRepository, PetRecordRepository petRecordRepository, String filePath) {
        this.configService = configService;
        this.wildPets = wildPets;
        this.petListRepository = petListRepository;
        this.petRecordRepository = petRecordRepository;
        this.filePath = filePath;
    }

    /**
     * Writes every pet record and pet to the data folder. Refused, with the files on disk left as
     * they were, when {@link #load()} has not succeeded: the in-memory state is then empty (or
     * incomplete), and writing it would replace the very data that could not be read. This
     * covers the hourly autosave and {@code onDisable()}, which Bukkit still runs for a plugin
     * whose {@code onEnable()} failed.
     * @return Whether the data was written.
     */
    public boolean save() {
        if (loadFailed) {
            logger().severe("Pet data was not saved because it was not loaded when the plugin was enabled. "
                    + "The files in " + filePath + " have been left as they were.");
            return false;
        }
        boolean saved = savePetRecords();
        saved &= savePets();
        // config.yml is not written here (on every autosave and on shutdown): /wp config set
        // already saved each change, and writing the loaded config again would undo anything
        // edited by hand while the server was running -- including usage-reporting.enabled: false.
        return saved;
    }

    /**
     * Reads the stored pets and pet records, then schedules their AI state to be applied once the
     * server has loaded the entities.
     * @return Whether the data was loaded. On failure the cause has been logged, nothing in memory
     *         has changed, and {@link #save()} is refused until a later load succeeds.
     */
    public boolean load() {
        if (!readFromDisk()) {
            return false;
        }
        applyAIStateToPets();
        return true;
    }

    /**
     * Whether the last {@link #load()} failed (or none has run yet), in which case
     * {@link #save()} is refused.
     */
    public boolean isLoadFailed() {
        return loadFailed;
    }

    private boolean savePets() {
        // save each pet object individually
        List<Map<String, String>> pets = new ArrayList<>();
        for (Pet pet : petListRepository.getAllPets()){
            pets.add(pet.save());
        }

        return writeOutFiles(pets, PETS_FILE_NAME);
    }

    private boolean savePetRecords() {
        List<Map<String, String>> petRecords = new ArrayList<>();
        for (PetRecord petRecord : petRecordRepository.getPetRecords()){
            petRecords.add(petRecord.save());
        }

        return writeOutFiles(petRecords, PET_RECORDS_FILE_NAME);
    }

    // Written to a sibling temp file and moved into place, so a server killed or a disk filled
    // mid-write leaves the previous file intact instead of a truncated one.
    private boolean writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(filePath);
            if (!parentFolder.isDirectory() && !parentFolder.mkdirs()) {
                throw new IOException(parentFolder + " could not be created");
            }
            Path target = new File(filePath + fileName).toPath();
            Path temp = target.resolveSibling(fileName + ".tmp");
            try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
                gson.toJson(saveData, writer);
            }
            try {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException | RuntimeException e) {
            logger().log(Level.SEVERE, "Could not save " + filePath + fileName + ".", e);
            return false;
        }
    }

    /**
     * Reads both files into memory without touching the repositories, and only replaces their
     * contents once every pet and record has been parsed. A file that cannot be read or holds a
     * malformed entry fails the whole load: the repositories are left as they were and
     * {@link #save()} is refused, so the unreadable file is never overwritten.
     * Package-private for testing; {@link #load()} also schedules AI-state work that needs a
     * running server.
     * @return Whether the data was loaded.
     */
    boolean readFromDisk() {
        List<PetRecord> records = new ArrayList<>();
        List<Pet> pets = new ArrayList<>();
        try {
            for (Map<String, String> petRecordData : loadDataFromFilename(filePath + PET_RECORDS_FILE_NAME)) {
                records.add(new PetRecord(petRecordData));
            }
            for (Map<String, String> petData : loadDataFromFilename(filePath + PETS_FILE_NAME)) {
                pets.add(new Pet(petData));
            }
        } catch (IOException | RuntimeException e) {
            // RuntimeException covers Gson's syntax errors and an entry with a missing or
            // malformed field (a UUID or a number that does not parse).
            loadFailed = true;
            logger().log(Level.SEVERE, "Pet data could not be loaded from " + filePath + ". The files have been "
                    + "left untouched and will not be overwritten; fix or restore them and restart the server.", e);
            return false;
        }

        // records outlive their pets (e.g. a dead parent's name), so they cannot be rebuilt from the pet list alone
        petRecordRepository.clearAll();
        for (PetRecord record : records) {
            petRecordRepository.addExistingPetRecord(record);
        }

        petListRepository.clearAll();
        for (Pet pet : pets) {
            petListRepository.addExistingPet(pet);
            petRecordRepository.addPetRecord(pet); // no-op when a persisted record already exists, because petRecords is a hashset and PetRecord implements equals()/hashCode() on uniqueID
        }
        loadFailed = false;
        // One "<n> <label> loaded" line per stored type: the release gates' save-compatibility
        // check compares these counts between the stable release and the candidate.
        logger().info(pets.size() + " pets loaded");
        logger().info(records.size() + " pet records loaded");
        return true;
    }
    
    private void applyAIStateToPets() {
        // Determine delay before applying AI state; default to 100 ticks but allow configuration
        long delayTicks = 100L;
        String configuredDelay = System.getProperty("wildpets.aiStateDelayTicks");
        if (configuredDelay != null) {
            try {
                long parsed = Long.parseLong(configuredDelay);
                if (parsed >= 0L) {
                    delayTicks = parsed;
                }
            } catch (NumberFormatException ignored) {
                // Ignore invalid values and keep the default delay
            }
        }

        // Schedule this to run after server is fully loaded to ensure entities are available
        wildPets.getServer().getScheduler().runTaskLater(wildPets, () -> {
            for (Pet pet : petListRepository.getAllPets()) {
                pet.applyAIState();
                pet.ensurePersistence();
            }
        }, delayTicks); // Default: wait 100 ticks (5 seconds at 20 TPS) for entities to be loaded
    }

    // A file that does not exist yet holds no data. So does an empty one: it can only be left
    // by a write that was cut off before this class wrote through a temp file, and there is
    // nothing in it to keep.
    private List<HashMap<String, String>> loadDataFromFilename(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            List<HashMap<String, String>> data = gson.fromJson(reader, LIST_MAP_TYPE);
            return data == null ? new ArrayList<>() : data;
        }
    }

    private Logger logger() {
        return wildPets != null ? wildPets.getLogger() : Logger.getLogger(StorageService.class.getName());
    }
}