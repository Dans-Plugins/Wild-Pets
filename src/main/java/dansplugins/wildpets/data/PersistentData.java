package dansplugins.wildpets.data;

import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.objects.PetList;
import dansplugins.wildpets.objects.PetRecord;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class PersistentData {

    private static PersistentData instance;

    private ArrayList<PetList> petLists = new ArrayList<>();
    private HashSet<PetRecord> petRecords = new HashSet<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    public ArrayList<PetList> getPetLists() {
        return petLists;
    }

    public boolean addNewPet(Player player, Entity entity) {
        Pet newPet = new Pet(entity, player.getUniqueId());
        addPetRecord(newPet);
        PetList petList = getPetList(player.getUniqueId());

        petList.addPet(newPet);
        return true;
    }

    public boolean removePet(Pet petToRemove) {
        return getPetList(petToRemove.getOwnerUUID()).removePet(petToRemove);
    }

    public Pet getPet(Entity entity) {
        for (PetList petList : getPetLists()) {
            Pet pet = petList.getPet(entity.getUniqueId());
            if (pet != null) {
                return pet;
            }
        }
        return null;
    }

    public PetList getPetList(UUID playerUUID) {
        for (PetList petList : getPetLists()) {
            if (petList.getOwnerUUID().equals(playerUUID)) {
                return petList;
            }
        }
        return null;
    }

    public void createPetListForPlayer(UUID playerUUID) {
        PetList newPetList = new PetList(playerUUID);
        getPetLists().add(newPetList);
    }

    public Pet getPlayersPet(Player player, Entity entity) {
        PetList petList = getPetList(player.getUniqueId());
        return petList.getPet(entity.getUniqueId());
    }

    public Pet getPlayersPet(Player player, String petName) {
        PetList petList = getPetList(player.getUniqueId());
        return petList.getPet(petName);
    }

    public void sendListOfPetsToPlayer(Player player) {
        PetList petList = getPetList(player.getUniqueId());
        petList.sendListOfPetsToPlayer(player);
    }

    public ArrayList<Pet> getAllPets() {
        ArrayList<Pet> toReturn = new ArrayList<>();
        for (PetList petList : petLists) {
            toReturn.addAll(petList.getPets());
        }
        return toReturn;
    }

    // ---

    public HashSet<PetRecord> getPetRecords() {
        return petRecords;
    }

    public boolean addPetRecord(Pet pet) {
        return petRecords.add(new PetRecord(pet));
    }

    public boolean removePetRecord(UUID entityUUID) {
        PetRecord recordToRemove = null;
        for (PetRecord record : petRecords) {
            if (record.getUniqueID().equals(entityUUID)) {
                recordToRemove = record;
                break;
            }
        }
        if (recordToRemove == null) {
            return false;
        }
        return petRecords.remove(recordToRemove);
    }

    public PetRecord getPetRecord(UUID entityUUID) {
        for (PetRecord record : petRecords) {
            if (record.getUniqueID().equals(entityUUID)) {
                return record;
            }
        }
        return null;
    }
}