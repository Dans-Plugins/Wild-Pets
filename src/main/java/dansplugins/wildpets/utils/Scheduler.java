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
}