package dansplugins.wildpets.data;

import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.objects.PetList;
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

        petList.getPets().add(newPet);
        return true;
    }

    public boolean isPet(Entity entity) {
        for (PetList petList : playerPetLists.values()) {
            if (petList.containsPet(entity.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPetList(Player player) {
        return getPlayerPetLists().containsKey(player);
    }

    public void createPetListForPlayer(Player player) {
        PetList newPetList = new PetList(player);
        getPlayerPetLists().put(player, newPetList);
    }
}
