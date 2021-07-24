package dansplugins.wildpets.objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PetList {

    private Player owner;

    private ArrayList<Pet> pets = new ArrayList<>();

    public PetList(Player player) {
        owner = player;
    }

    public ArrayList<Pet> getPets() {
        return pets;
    }

    public boolean containsPet(UUID uuid) {
        for (Pet pet : getPets()) {
            if (pet.getUniqueID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

}
