package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class BreedEventHandler implements Listener {

    @EventHandler()
    public void handle(EntityBreedEvent event) {
        if (!(event.getBreeder() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getBreeder();

        Entity child = event.getEntity();

        Entity parent1 = event.getFather();
        Entity parent2 = event.getMother();

        PersistentData.getInstance().addNewPet(player, child);

        Pet newPet = PersistentData.getInstance().getPet(child);

        player.sendMessage(ChatColor.AQUA + "You have a new pet! Type /wp select " + newPet.getName() + " to select it.");

        Pet petParent1 = PersistentData.getInstance().getPet(parent1);
        Pet petParent2 = PersistentData.getInstance().getPet(parent2);

        if (petParent1 != null) {
            newPet.addParent(petParent1.getAssignedID());
            petParent1.addChild(newPet.getAssignedID());
        }
        else {
            newPet.addParent(-1);
        }

        if (petParent2 != null) {
            newPet.addParent(petParent2.getAssignedID());
            petParent2.addChild(newPet.getAssignedID());
        }
        else {
            newPet.addParent(-1);
        }

    }

}
