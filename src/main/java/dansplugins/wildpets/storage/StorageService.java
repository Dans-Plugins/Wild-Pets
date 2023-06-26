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

    private final static String FILE_PATH = "./plugins/WildPets/";
    private final static String PETS_FILE_NAME = "pets.json";
    private final static String PET_RECORDS_FILE_NAME = "petRecords.json";

    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    public StorageService(ConfigService configService, WildPets wildPets, PetListRepository petListRepository, PetRecordRepository petRecordRepository) {
        this.configService = configService;
        this.wildPets = wildPets;
        this.petListRepository = petListRepository;
        this.petRecordRepository = new PetRecordRepository();
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
            File parentFolder = new File(FILE_PATH);
            parentFolder.mkdir();
            File file = new File(FILE_PATH + fileName);
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
        petListRepository.getPetLists().clear();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + PETS_FILE_NAME);

        ArrayList<Pet> allPets = new ArrayList<>();

        for (Map<String, String> petData : data){
            Pet pet = new Pet(petData);
            allPets.add(pet);
        }

        for (Pet pet : allPets) {
            if (petListRepository.getPetList(pet.getOwnerUUID()) == null) {
                petListRepository.createPetListForPlayer(pet.getOwnerUUID());

            }
            petListRepository.getPetList(pet.getOwnerUUID()).addPet(pet);
            petRecordRepository.addPetRecord(pet); // will not result in duplicates because petRecords is a hashset
        }
    }

    private void loadPetRecords() {
        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + PET_RECORDS_FILE_NAME);

        ArrayList<PetRecord> petRecords = new ArrayList<>();

        for (Map<String, String> petRecordData : data) {
            PetRecord petRecord = new PetRecord(petRecordData);
            petRecords.add(petRecord);
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