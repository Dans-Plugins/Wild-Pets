package dansplugins.wildpets.data;

import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.objects.PetList;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PersistentData {

    private static PersistentData instance;

    private HashMap<Player, PetList> playerPetLists = new HashMap<>();

    private PersistentData() {

    }

    public static PersistentData getInstance() {
        if (instance == null) {
            instance = new PersistentData();
        }
        return instance;
    }

    private HashMap<Player, PetList> getPlayerPetLists() {
        return playerPetLists;
    }

    public boolean addNewPet(Player player, Entity entity) {
        Pet newPet = new Pet(entity, player);
        PetList petList = getPlayerPetLists().get(player);

        petList.addPet(newPet);
        return true;
    }

    public Pet getPet(Entity entity) {
        for (PetList petList : playerPetLists.values()) {
            Pet pet = petList.getPet(entity.getUniqueId());
            if (pet != null) {
                return pet;
            }
        }
        return null;
    }

    public PetList getPetList(Player player) {
        return getPlayerPetLists().get(player);
    }

    public void createPetListForPlayer(Player player) {
        PetList newPetList = new PetList(player);
        getPlayerPetLists().put(player, newPetList);
    }

    public Pet getPlayersPet(Player player, Entity entity) {
        PetList petList = getPlayerPetLists().get(player);
        return petList.getPet(entity.getUniqueId());
    }

    public void sendListOfPetsToPlayer(Player player) {
        PetList petList = getPetList(player);
        petList.sendListOfPetsToPlayer(player);
    }
}
