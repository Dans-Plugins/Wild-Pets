package dansplugins.wildpets.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.objects.PetRecord;

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
public class StorageManager {

    private static StorageManager instance;

    private final static String FILE_PATH = "./plugins/WildPets/";
    private final static String PETS_FILE_NAME = "pets.json";
    private final static String PET_RECORDS_FILE_NAME = "petRecords.json";

    private final static Type LIST_MAP_TYPE = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();;

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void save() {
        savePetRecords();
        savePets();
        if (ConfigManager.getInstance().hasBeenAltered()) {
            WildPets.getInstance().saveConfig();
        }
    }

    public void load() {
        loadPetRecords();
        loadPets();
    }

    private void savePets() {
        // save each pet object individually
        List<Map<String, String>> pets = new ArrayList<>();
        for (Pet pet : PersistentData.getInstance().getAllPets()){
            pets.add(pet.save());
        }

        writeOutFiles(pets, PETS_FILE_NAME);
    }

    private void savePetRecords() {
        List<Map<String, String>> petRecords = new ArrayList<>();
        for (PetRecord petRecord : PersistentData.getInstance().getPetRecords()){
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
        PersistentData.getInstance().getPetLists().clear();

        ArrayList<HashMap<String, String>> data = loadDataFromFilename(FILE_PATH + PETS_FILE_NAME);

        ArrayList<Pet> allPets = new ArrayList<>();

        for (Map<String, String> petData : data){
            Pet pet = new Pet(petData);
            allPets.add(pet);
        }

        for (Pet pet : allPets) {
            if (PersistentData.getInstance().getPetList(pet.getOwnerUUID()) == null) {
                PersistentData.getInstance().createPetListForPlayer(pet.getOwnerUUID());

            }
            PersistentData.getInstance().getPetList(pet.getOwnerUUID()).addPet(pet);
            PersistentData.getInstance().addPetRecord(pet); // will not result in duplicates because petRecords is a hashset
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