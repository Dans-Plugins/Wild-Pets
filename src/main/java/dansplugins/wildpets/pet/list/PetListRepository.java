package dansplugins.wildpets.pet.list;

import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.location.WpLocation;

import org.bukkit.ChatColor;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class PetListRepository {
    private final ConfigService configService;

    private final ArrayList<PetList> petLists = new ArrayList<>();
    private final HashMap<UUID, Pet> petsByEntityUUID = new HashMap<>();

    public PetListRepository(ConfigService configService) {
        this.configService = configService;
    }

    public ArrayList<PetList> getPetLists() {
        return petLists;
    }

    public boolean addNewPet(Player player, Entity entity) {
        // create pet
        Pet newPet = new Pet(entity.getUniqueId(), player.getUniqueId(), player.getName());
        newPet.setAssignedID(getPetList(player.getUniqueId()).getNewID());
        Location bukkitLocation = entity.getLocation();
        WpLocation wpLocation = new WpLocation(bukkitLocation.getX(), bukkitLocation.getY(), bukkitLocation.getZ());
        newPet.setLastKnownLocation(wpLocation);
        entity.setCustomName(ChatColor.GREEN + newPet.getName());
        newPet.ensurePersistence(entity);
        entity.playEffect(EntityEffect.LOVE_HEARTS);

        // add pet to pet list
        PetList petList = getPetList(player.getUniqueId());
        petList.addPet(newPet);
        petsByEntityUUID.put(newPet.getUniqueID(), newPet);
        return true;
    }

    public boolean removePet(Pet petToRemove) {
        PetList ownerPetList = getPetList(petToRemove.getOwnerUUID());
        if (ownerPetList == null) {
            return false;
        }
        boolean removed = ownerPetList.removePet(petToRemove);
        if (removed) {
            petsByEntityUUID.remove(petToRemove.getUniqueID());
        }
        return removed;
    }

    public Pet getPet(Entity entity) {
        return petsByEntityUUID.get(entity.getUniqueId());
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

    /**
     * Adds an already-constructed Pet to the appropriate PetList and the UUID index.
     * Used when loading pets from storage.
     */
    public void addExistingPet(Pet pet) {
        PetList petList = getPetList(pet.getOwnerUUID());
        if (petList == null) {
            createPetListForPlayer(pet.getOwnerUUID());
            petList = getPetList(pet.getOwnerUUID());
        }
        petList.addPet(pet);
        petsByEntityUUID.put(pet.getUniqueID(), pet);
    }

    /**
     * Clears all pet lists and the UUID index.
     */
    public void clearAll() {
        petLists.clear();
        petsByEntityUUID.clear();
    }

    /**
     * Returns true if no pets are currently tracked in the UUID index.
     * Useful for cheaply short-circuiting work on hot event paths (e.g. chunk loads)
     * when the plugin has no pets to act on.
     */
    public boolean hasNoTrackedPets() {
        return petsByEntityUUID.isEmpty();
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