package dansplugins.wildpets.pet.list;

import dansplugins.wildpets.pet.Pet;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.utils.MessageFormat;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

/**
 * @author Daniel McCoy Stephenson
 */
public class PetList {
    private final ConfigService configService;
    private final ServerProvider serverProvider;

    private final UUID ownerUUID;
    private final ArrayList<Pet> pets = new ArrayList<>();

    public PetList(ConfigService configService, UUID playerUUID) {
        this(configService, playerUUID, new ServerProvider());
    }

    public PetList(ConfigService configService, UUID playerUUID, ServerProvider serverProvider) {
        this.configService = configService;
        this.serverProvider = serverProvider;
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
        Entity entity = getLoadedEntity(petToRemove.getUniqueID());

        if (entity != null) {
            entity.setCustomName("");
            Pet.applyPersistenceFlags(entity, false);
            entity.setInvulnerable(false);
        }
        return getPets().remove(petToRemove);
    }

    /**
     * Removes a pet from this list without clearing its entity state (custom name,
     * persistence, invulnerability). Used when the pet is moving to another owner's
     * list rather than being permanently released.
     */
    public boolean removePetForTransfer(Pet petToRemove) {
        return getPets().remove(petToRemove);
    }

    public void sendListOfPetsToPlayer(Player player) {
        if (getNumPets() == 0) {
            if (player.getUniqueId().equals(ownerUUID)) {
                player.sendMessage(ChatColor.RED + "You don't have any pets yet.");
            }
            else {
                player.sendMessage(ChatColor.RED + "That player doesn't have any pets yet.");
            }

            return;
        }

        player.sendMessage("");
        player.sendMessage(MessageFormat.header("Wild Pets", "List of Pets"));
        for (Pet pet : getPets()) {
            Entity entity = getLoadedEntity(pet.getUniqueID());

            if (entity != null) {
                player.sendMessage(MessageFormat.line(ChatColor.WHITE + pet.getName()));
            }
            else {
                player.sendMessage(MessageFormat.line(ChatColor.WHITE + pet.getName() + ChatColor.RED + " [not found]"));
            }

        }
        player.sendMessage(MessageFormat.footer());
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

    /**
     * Resolves the live entity for the given pet UUID, if it is currently loaded.
     * Returns null if the server provider/server is unavailable or the entity isn't loaded.
     */
    private Entity getLoadedEntity(UUID entityUUID) {
        if (serverProvider == null) {
            // Server provider not available; cannot resolve the entity safely
            return null;
        }
        Server server = serverProvider.get();
        if (server == null) {
            // Server not available; cannot resolve the entity safely
            return null;
        }
        // Entity not found (e.g., in unloaded chunk) results in null being returned
        return server.getEntity(entityUUID);
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