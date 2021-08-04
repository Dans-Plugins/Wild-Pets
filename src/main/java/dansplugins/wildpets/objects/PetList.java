package dansplugins.wildpets.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dansplugins.wildpets.data.PersistentData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Type;
import java.util.*;

public class PetList {

    private UUID ownerUUID;

    private ArrayList<Pet> pets = new ArrayList<>();

    public PetList(UUID playerUUID) {
        ownerUUID = playerUUID;
    }

    public PetList(Map<String, String> data) {
        this.load(data);
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
        if (getPets().size() == 0) {
            player.sendMessage(ChatColor.RED + "You don't have any pets yet.");
            return;
        }

        player.sendMessage(ChatColor.AQUA + "=== List of Pets ===");
        for (Pet pet : getPets()) {
            player.sendMessage(ChatColor.AQUA + pet.getName());
        }
    }

    public int getNumPets() {
        return pets.size();
    }

    public Map<String, String> save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Map<String, String> saveMap = new HashMap<>();

        // save owner
        saveMap.put("owner", gson.toJson(ownerUUID));

        // save pets
        ArrayList<String> petsList = new ArrayList<>();
        for (Pet pet : PersistentData.getInstance().getPetList(ownerUUID).getPets()){
            Map<String, String> map = pet.save();
            petsList.add(gson.toJson(map));
        }

        saveMap.put("pets", gson.toJson(petsList));

        return saveMap;
    }

    public void load(Map<String, String> data) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Type arrayListTypeString = new TypeToken<ArrayList<String>>(){}.getType();

        // load owner
        ownerUUID = UUID.fromString(gson.fromJson(data.get("owner"), String.class));

        // load pets
        ArrayList<String> petsList = gson.fromJson(data.get("pets"), arrayListTypeString);
        if (petsList != null) {
            for (String item : petsList) {
                Pet pet = Pet.load(item);
                pets.add(pet);
            }
        }
        else {
            System.out.println("Missing pets list JSON collection.");
        }
    }
}
