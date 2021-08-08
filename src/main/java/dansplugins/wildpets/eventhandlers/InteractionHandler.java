package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.managers.EntityConfigManager;
import dansplugins.wildpets.objects.EntityConfig;
import dansplugins.wildpets.objects.Pet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class InteractionHandler implements Listener {

    private boolean debug = WildPets.getInstance().isDebugEnabled();

    @EventHandler()
    public void handle(PlayerInteractEntityEvent event) {
        Entity clickedEntity = event.getRightClicked();

        EntityConfig entityConfig = EntityConfigManager.getInstance().acquireConfiguration(clickedEntity);

        Player player = event.getPlayer();
        
        Pet pet = PersistentData.getInstance().getPet(clickedEntity);

        if (EphemeralData.getInstance().isPlayerTaming(player)) {
            setRightClickCooldown(player, 1);

            if (clickedEntity instanceof Player) {
                player.sendMessage(ChatColor.RED + "You can't tame players.");
                return;
            }

            if (!(clickedEntity instanceof LivingEntity)) {
                player.sendMessage(ChatColor.RED + "You can only tame living entities.");
                EphemeralData.getInstance().setPlayerAsNotTaming(player);
                return;
            }

            if (pet != null) {
                player.sendMessage(ChatColor.RED + "That entity is already a pet.");
                EphemeralData.getInstance().setPlayerAsNotTaming(player);
                return;
            }

            if (debug && entityConfig.getType().equalsIgnoreCase("default")) {
                player.sendMessage(ChatColor.BLUE + "[DEBUG] This entity doesn't have a configuration.");
            }

            if (!entityConfig.isEnabled()) {
                player.sendMessage(ChatColor.RED + "Taming has been disabled for this entity.");
                return;
            }

            int numPets = PersistentData.getInstance().getPetList(player.getUniqueId()).getNumPets();
            int petLimit = WildPets.getInstance().getConfig().getInt("configOptions." + "petLimit");
            if (debug) {
                System.out.println("[DEBUG] Number of pets: " + numPets);
                System.out.println("[DEBUG] Pet Limit: " + petLimit);
            }
            if (numPets >= petLimit) {
                player.sendMessage(ChatColor.RED + "You have reached your pet limit.");
                EphemeralData.getInstance().setPlayerAsNotTaming(player);
                return;
            }

            ItemStack itemStack = player.getInventory().getItemInMainHand();
            Material requiredMaterial = entityConfig.getRequiredTamingItem();
            int requiredAmount = entityConfig.getTamingItemAmount();
            if (itemStack.getType() != requiredMaterial || itemStack.getAmount() < requiredAmount) {
                player.sendMessage(ChatColor.RED + "You need to use " + requiredAmount + " " + requiredMaterial.name().toLowerCase() + " to tame this entity.");
                EphemeralData.getInstance().setPlayerAsNotTaming(player);
                return;
            }

            // handle chance to tame
            if (!rollDice(entityConfig.getChanceToSucceed())) {
                player.sendMessage(ChatColor.RED + "Taming failed.");
                EphemeralData.getInstance().setPlayerAsNotTaming(player);
                if (itemStack.getAmount() > requiredAmount) {
                    player.getInventory().setItemInMainHand(new ItemStack(itemStack.getType(), itemStack.getAmount() - requiredAmount));
                }
                else {
                    player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                }
                return;
            }

            PersistentData.getInstance().addNewPet(player, clickedEntity);
            player.sendMessage(ChatColor.GREEN + "Tamed.");
            EphemeralData.getInstance().setPlayerAsNotTaming(player);

            if (itemStack.getAmount() > requiredAmount) {
                player.getInventory().setItemInMainHand(new ItemStack(itemStack.getType(), itemStack.getAmount() - requiredAmount));
            }
            else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            EphemeralData.getInstance().selectPetForPlayer(PersistentData.getInstance().getPet(clickedEntity), player);
        }
        else if (EphemeralData.getInstance().isPlayerSelecting(player)) {
            setRightClickCooldown(player, 1);

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "That entity is not a pet.");
                EphemeralData.getInstance().setPlayerAsNotSelecting(player);
                return;
            }

            if (PersistentData.getInstance().getPlayersPet(player, clickedEntity) == null) {
                player.sendMessage(ChatColor.RED + "That entity is not your pet.");
                EphemeralData.getInstance().setPlayerAsNotSelecting(player);
                return;
            }

            EphemeralData.getInstance().selectPetForPlayer(pet, player);
            player.sendMessage(ChatColor.GREEN + pet.getName() + " selected.");
            EphemeralData.getInstance().setPlayerAsNotSelecting(player);
        }
        else {
            if (pet == null) {
                return;
            }

            if (!EphemeralData.getInstance().hasRightClickCooldown(player)) {
                setRightClickCooldown(player, 3);

                pet.sendInfoToPlayer(player);
            }
        }

    }

    private void setRightClickCooldown(Player player, int seconds) {
        EphemeralData.getInstance().setRightClickCooldown(player, true);

        WildPets.getInstance().getServer().getScheduler().runTaskLater(WildPets.getInstance(), new Runnable() {
            @Override
            public void run() {
                EphemeralData.getInstance().setRightClickCooldown(player, false);

            }
        }, seconds * 20);
    }

    private boolean rollDice(double chanceToSucceed) {
        double chanceToFail = 1 - chanceToSucceed;
        if (debug) { System.out.println("Rolling dice! Chance to fail: " + chanceToFail * 100 + "%"); }
        Random random = new Random();
        double generatedNumber = random.nextDouble();
        if (debug) { System.out.println("Dice landed on " + generatedNumber * 100 + ". " + chanceToFail * 100 + " was required."); }
        return generatedNumber > chanceToFail;
    }

}