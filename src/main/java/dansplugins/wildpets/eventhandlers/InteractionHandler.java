package dansplugins.wildpets.eventhandlers;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.data.PersistentData;
import dansplugins.wildpets.managers.ConfigManager;
import dansplugins.wildpets.managers.EntityConfigManager;
import dansplugins.wildpets.objects.EntityConfig;
import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.Scheduler;
import dansplugins.wildpets.utils.UUIDChecker;
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
import java.util.UUID;

public class InteractionHandler implements Listener {

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

            if (WildPets.getInstance().isDebugEnabled() && entityConfig.getType().equalsIgnoreCase("default")) {
                player.sendMessage(ChatColor.BLUE + "[DEBUG] This entity doesn't have a configuration.");
            }

            if (!entityConfig.isEnabled()) {
                player.sendMessage(ChatColor.RED + "Taming has been disabled for this entity.");
                return;
            }

            int numPets = PersistentData.getInstance().getPetList(player.getUniqueId()).getNumPets();
            int petLimit = ConfigManager.getInstance().getInt("petLimit");
            if (WildPets.getInstance().isDebugEnabled()) {
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
                if (ConfigManager.getInstance().getBoolean("cancelTamingAfterFailedAttempt")) {
                    EphemeralData.getInstance().setPlayerAsNotTaming(player);
                }
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
        else if (EphemeralData.getInstance().isPlayerLocking(player)) {

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "This entity isn't a pet.");
                EphemeralData.getInstance().setPlayerAsNotLocking(player);
                return;
            }

            if (!pet.getOwnerUUID().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "This is not your pet.");
                return;
            }

            boolean locked = pet.getLocked();
            if (locked) {
                player.sendMessage(ChatColor.RED + "This pet is already locked.");
                return;
            }
            pet.setLocked(true);
            player.sendMessage(ChatColor.GREEN + "This pet has been locked.");
        }
        else if (EphemeralData.getInstance().isPlayerUnlocking(player)) {

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "This entity isn't a pet.");
                EphemeralData.getInstance().setPlayerAsNotLocking(player);
                return;
            }

            if (!pet.getOwnerUUID().equals(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "This is not your pet.");
                return;
            }

            boolean locked = pet.getLocked();
            if (!locked) {
                player.sendMessage(ChatColor.RED + "This pet is already unlocked.");
                return;
            }
            pet.setLocked(false);
            player.sendMessage(ChatColor.GREEN + "This pet has been unlocked.");
        }
        else if (EphemeralData.getInstance().isPlayerCheckingAccess(player)) {

            if (pet == null) {
                player.sendMessage(ChatColor.RED + "This entity isn't a pet.");
                EphemeralData.getInstance().setPlayerAsNotLocking(player);
                return;
            }

            boolean locked = pet.getLocked();
            if (!locked) {
                player.sendMessage(ChatColor.RED + "This pet isn't locked.");
                return;
            }

            player.sendMessage(ChatColor.AQUA + "The following players have access to this pet:");
            for (UUID uuid : pet.getAccessList()) {
                String playerName = UUIDChecker.getInstance().findPlayerNameBasedOnUUID(uuid);
                if (playerName != null) {
                    player.sendMessage(ChatColor.AQUA + playerName);
                }
            }
        }
        else {
            if (pet == null) {
                return;
            }

            if (!EphemeralData.getInstance().hasRightClickCooldown(player)) {
                setRightClickCooldown(player, ConfigManager.getInstance().getInt("rightClickViewCooldown"));

                pet.sendInfoToPlayer(player);

                if (ConfigManager.getInstance().getBoolean("rightClickToSelect")) {
                    if (!pet.getOwnerUUID().equals(player.getUniqueId())) {
                        return;
                    }

                    Pet petSelection = EphemeralData.getInstance().getPetSelectionForPlayer(player);
                    if (petSelection == null || !petSelection.getUniqueID().equals(pet.getUniqueID())) {
                        EphemeralData.getInstance().selectPetForPlayer(pet, player);
                        player.sendMessage(ChatColor.GREEN + pet.getName() + " selected.");
                    }
                }
            }
        }

    }

    private void setRightClickCooldown(Player player, int seconds) {
        EphemeralData.getInstance().setRightClickCooldown(player, true);
        Scheduler.scheduleRightClickCooldownSetter(player, seconds);
    }

    private boolean rollDice(double chanceToSucceed) {
        double chanceToFail = 1 - chanceToSucceed;
        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("Rolling dice! Chance to fail: " + chanceToFail * 100 + "%"); }
        Random random = new Random();
        double generatedNumber = random.nextDouble();
        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("Dice landed on " + generatedNumber * 100 + ". " + chanceToFail * 100 + " was required."); }
        return generatedNumber > chanceToFail;
    }

}