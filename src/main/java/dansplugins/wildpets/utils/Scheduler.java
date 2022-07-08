package dansplugins.wildpets.utils;

import dansplugins.wildpets.WildPets;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.services.StorageService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Daniel McCoy Stephenson
 */
public class Scheduler {
    private final WildPets wildPets;
    private final EphemeralData ephemeralData;
    private final StorageService storageService;

    public Scheduler(WildPets wildPets, EphemeralData ephemeralData, StorageService storageService) {
        this.wildPets = wildPets;
        this.ephemeralData = ephemeralData;
        this.storageService = storageService;
    }

    public void scheduleRightClickCooldownSetter(Player player, int seconds) {
        wildPets.getServer().getScheduler().runTaskLater(wildPets, new Runnable() {
            @Override
            public void run() {
                ephemeralData.setRightClickCooldown(player.getUniqueId(), false);

            }
        }, seconds * 20L);
    }

    public void scheduleAutosave() {
        if (wildPets.isDebugEnabled()) { System.out.println("Scheduling hourly autosave."); }
        int delay = 60 * 60; // 1 hour
        int secondsUntilRepeat = 60 * 60; // 1 hour
        Bukkit.getScheduler().scheduleSyncRepeatingTask(wildPets, new Runnable() {
            @Override
            public void run() {
                if (wildPets.isDebugEnabled()) { System.out.println("Wild Pets is saving. This will happen hourly."); }
                storageService.save();
            }
        }, delay * 20, secondsUntilRepeat * 20);
    }
}