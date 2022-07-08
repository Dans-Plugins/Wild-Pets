package dansplugins.wildpets.objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import dansplugins.wildpets.services.ConfigService;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class PetList {
    private final ConfigService configService;

    private final UUID ownerUUID;
    private final ArrayList<Pet> pets = new ArrayList<>();

    public PetList(ConfigService configService, UUID playerUUID) {
        this.configService = configService;
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

    public boolean removePet(Pet petToRemove) {
        petToRemove.deconstruct();
        return getPets().remove(petToRemove);
    }

    public void sendListOfPetsToPlayer(Player player) {
        if (getNumPets() == 0) {
            if (player.getUniqueId().equals(ownerUUID)) {
                player.sendMessage(ChatColor.RED + "You don't have any pets yet.");
            }
            else {
                player.sendMessage(ChatColor.AQUA + "That player doesn't have any pets yet.");
            }

            return;
        }

        player.sendMessage(ChatColor.AQUA + "=== List of Pets ===");
        for (Pet pet : getPets()) {
            Entity entity = Bukkit.getEntity(pet.getUniqueID());

            if (entity != null) {
                player.sendMessage(ChatColor.AQUA + pet.getName());
            }
            else {
                player.sendMessage(ChatColor.AQUA + pet.getName() + ChatColor.RED + " [not found]");
            }

        }
    }

    public int getNumPets() {
        return getPets().size();
    }

    public int getNewID() {
        int newID = -1;
        do {
            Random random = new Random();
            newID = random.nextInt(configService.getInt("petLimit") * 10);
        } while (isIDTaken(newID));

        return newID;
    }

    public boolean isNameTaken(String name) {
        for (Pet pet : getPets()) {
            if (pet.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Pet> getFollowingPets() {
        ArrayList<Pet> toReturn = new ArrayList<>();
        for (Pet pet : getPets()) {
            if (pet.getMovementState().equals("Following")) {
                toReturn.add(pet);
            }
        }
        return toReturn;
    }

    private boolean isIDTaken(int ID) {
        for (Pet pet : getPets()) {
            if (pet.getAssignedID() == ID) {
                return true;
            }
        }
        return false;
    }
}