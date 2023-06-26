package dansplugins.wildpets.pet.list;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.record.PetRecord;
import dansplugins.wildpets.config.ConfigService;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class PetListRepository {
    private final WildPets wildPets;
    private final ConfigService configService;

    private final ArrayList<PetList> petLists = new ArrayList<>();

    public PetListRepository(WildPets wildPets, ConfigService configService) {
        this.wildPets = wildPets;
        this.configService = configService;
    }

    public ArrayList<PetList> getPetLists() {
        return petLists;
    }

    public boolean addNewPet(Player player, Entity entity) {
        Pet newPet = new Pet(entity, player.getUniqueId());
        newPet.setAssignedID(getPetList(player.getUniqueId()).getNewID());
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
        PetList newPetList = new PetList(configService, playerUUID);
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
}