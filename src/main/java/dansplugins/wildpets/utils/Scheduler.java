package dansplugins.wildpets.utils;

import dansplugins.wildpets.WildPets;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.objects.Pet;
import dansplugins.wildpets.services.LocalConfigService;
import dansplugins.wildpets.services.LocalStorageService;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * @author Daniel McCoy Stephenson
 */
public class Scheduler {

    private static Scheduler instance;

    private Scheduler() {

    }

    public static Scheduler getInstance() {
        if (instance == null) {
            instance = new Scheduler();
        }
        return instance;
    }

    public static void scheduleRightClickCooldownSetter(Player player, int seconds) {
        WildPets.getInstance().getServer().getScheduler().runTaskLater(WildPets.getInstance(), new Runnable() {
            @Override
            public void run() {
                EphemeralData.getInstance().setRightClickCooldown(player.getUniqueId(), false);

            }
        }, seconds * 20);
    }

    public void scheduleAutosave() {
        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("Scheduling hourly autosave."); }
        int delay = 60 * 60; // 1 hour
        int secondsUntilRepeat = 60 * 60; // 1 hour
        Bukkit.getScheduler().scheduleSyncRepeatingTask(WildPets.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (WildPets.getInstance().isDebugEnabled()) { System.out.println("Wild Pets is saving. This will happen hourly."); }
                LocalStorageService.getInstance().save();
            }
        }, delay * 20, secondsUntilRepeat * 20);
    }

    public void attemptToScheduleTeleportTask(Pet pet) {
        if (pet.getTeleportTaskID() != -1) {
            return;
        }

        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Attempting to scheduling teleport task for " + pet.getName() + "."); }

        int secondsUntilRepeat = LocalConfigService.getInstance().getInt("secondsBetweenSchedulingAttempts");
        pet.setSchedulerTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(WildPets.getInstance(), new Runnable() {
            @Override
            public void run() {
                Entity entity = Bukkit.getEntity(pet.getUniqueID());

                if (entity != null) {
                    if (pet.getTeleportTaskID() != -1) {
                        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Teleport task has already been scheduled! Cancelling scheduling task!"); }
                        cancelSchedulingTask(pet);
                        return;
                    }
                    if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] The entity " + pet.getName() + " was found! Scheduling teleport now."); }
                    scheduleTeleport(entity, pet);

                    cancelSchedulingTask(pet);
                }
                else {
                    pet.incrementScheduleAttempts();
                    if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] The entity '" + pet.getName() + "' cannot be found! Cannot schedule teleport task. Will retry in " + secondsUntilRepeat + " seconds. Attempt: " + pet.getScheduleAttempts()); }

                    int maxScheduleAttempts = LocalConfigService.getInstance().getInt("maxScheduleAttempts");
                    if (pet.getScheduleAttempts() > maxScheduleAttempts) {
                        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] The entity '" + pet.getName() + "' wasn't able to be found more than " + maxScheduleAttempts + " times. Cannot schedule. Setting pet to wander."); }
                        cancelSchedulingTask(pet);
                        pet.setWandering();
                    }
                }
            }
        }, 0, secondsUntilRepeat * 20));
    }

    public void cancelTeleportTask(Pet pet) {
        Bukkit.getScheduler().cancelTask(pet.getTeleportTaskID());
        pet.setTeleportTaskID(-1);
    }

    private void cancelSchedulingTask(Pet pet) {
        if (WildPets.getInstance().isDebugEnabled()) { System.out.println("[DEBUG] Cancelling scheduling task with ID " + pet.getSchedulerTaskID()); }
        Bukkit.getScheduler().cancelTask(pet.getSchedulerTaskID());
        pet.setSchedulerTaskID(-1);
    }

    private void scheduleTeleport(Entity entity, Pet pet) {
        pet.setStayingLocation(entity.getLocation());

        double secondsUntilRepeat = LocalConfigService.getInstance().getDouble("secondsBetweenStayTeleports");
        pet.setTeleportTaskID(Bukkit.getScheduler().scheduleSyncRepeatingTask(WildPets.getInstance(), new Runnable() {
            @Override
            public void run() {
                float yaw = entity.getLocation().getYaw();
                float pitch = entity.getLocation().getPitch();
                entity.teleport(new Location(pet.getStayingLocation().getWorld(), pet.getStayingLocation().getX(), pet.getStayingLocation().getY(), pet.getStayingLocation().getZ(), yaw, pitch));
            }
        }, 0, (int)(secondsUntilRepeat * 20)));
        if (WildPets.getInstance().isDebugEnabled()) {
            if (pet.getTeleportTaskID() != -1) {
                System.out.println("[DEBUG] Scheduled!");
            }
        }
    }
}