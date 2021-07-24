package dansplugins.wildpets.objects;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class PetList {

    private UUID ownerUUID;

    private ArrayList<Pet> pets = new ArrayList<>();

    public PetList(UUID playerUUID) {
        ownerUUID = playerUUID;
    }

    public ArrayList<Pet> getPets() {
        return pets;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public Pet getPet(UUID uuid) {
        for (Pet pet : getPets()) {
            if (pet.getUniqueID().equals(uuid)) {
                return pet;
            }
        }
        return null;
    }

    public Pet getPet(String name) {
        for (Pet pet : getPets()) {
            if (pet.getName().equalsIgnoreCase(name)) {
                return pet;
            }
        }
        return null;
    }

    public void addPet(Pet newPet) {
        getPets().add(newPet);
    }

    public void removePet(Pet petToRemove) {
        petToRemove.deconstruct();
        getPets().remove(petToRemove);
    }

    public void sendListOfPetsToPlayer(Player player) {
        if (getPets().size() == 0) {
            player.sendMessage(ChatColor.RED + "You don't have any pets yet.");
            return;
        }

        player.sendMessage(ChatColor.AQUA + "=== List of Pets ===");
        for (Pet pet : getPets()) {
            player.sendMessage(ChatColor.AQUA + pet.getName());
        }
    }
}
