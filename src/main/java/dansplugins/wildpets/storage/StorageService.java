package dansplugins.wildpets.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.record.PetRecord;
import dansplugins.wildpets.pet.record.PetRecordRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void save() {
        savePetRecords();
        savePets();
        if (configService.hasBeenAltered()) {
            wildPets.saveConfig();
        }
    }

    public void load() {
        loadPetRecords();
        loadPets();
    }

    private void savePets() {
        // save each pet object individually
        List<Map<String, String>> pets = new ArrayList<>();
        for (Pet pet : petListRepository.getAllPets()){
            pets.add(pet.save());
        }

        writeOutFiles(pets, PETS_FILE_NAME);
    }

    private void savePetRecords() {
        List<Map<String, String>> petRecords = new ArrayList<>();
        for (PetRecord petRecord : petRecordRepository.getPetRecords()){
            petRecords.add(petRecord.save());
        }

        writeOutFiles(petRecords, PET_RECORDS_FILE_NAME);
    }

    private void writeOutFiles(List<Map<String, String>> saveData, String fileName) {
        try {
            File parentFolder = new File(filePath);
            parentFolder.mkdir();
            File file = new File(filePath + fileName);
            file.createNewFile();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(gson.toJson(saveData));
            outputStreamWriter.close();
        } catch(IOException e) {
            System.out.println("ERROR: " + e.toString());
        }
    }

    private void loadPets() {
        // load each pet individually and reconstruct pet list objects
        petListRepository.clearAll();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(filePath + PETS_FILE_NAME);

        ArrayList<Pet> allPets = new ArrayList<>();

        for (Map<String, String> petData : data){
            Pet pet = new Pet(petData);
            allPets.add(pet);
        }

        for (Pet pet : allPets) {
            petListRepository.addExistingPet(pet);
            petRecordRepository.addPetRecord(pet); // no-op when a persisted record already exists, because petRecords is a hashset and PetRecord implements equals()/hashCode() on uniqueID
        }
        
        // Apply AI state to loaded pets
        applyAIStateToPets();
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

    // package-private for testing; load() also schedules AI-state work that needs a running server
    void loadPetRecords() {
        // records outlive their pets (e.g. a dead parent's name), so they cannot be rebuilt from the pet list alone
        petRecordRepository.clearAll();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(filePath + PET_RECORDS_FILE_NAME);

        for (Map<String, String> petRecordData : data) {
            petRecordRepository.addExistingPetRecord(new PetRecord(petRecordData));
        }
    }

    private ArrayList<HashMap<String, String>> loadDataFromFilename(String filename) {
        try{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();;
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
            return gson.fromJson(reader, LIST_MAP_TYPE);
        } catch (FileNotFoundException e) {
            // Fail silently because this can actually happen in normal use
        }
        return new ArrayList<>();
    }
}