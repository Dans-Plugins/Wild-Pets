package dansplugins.wildpets;

import dansplugins.wildpets.bstats.Metrics;
import dansplugins.wildpets.commands.*;
import dansplugins.wildpets.eventhandlers.*;
import dansplugins.wildpets.managers.ConfigManager;
import dansplugins.wildpets.managers.EntityConfigManager;
import dansplugins.wildpets.managers.StorageManager;
import dansplugins.wildpets.utils.Scheduler;
import preponderous.ponder.minecraft.bukkit.abs.AbstractPluginCommand;
import preponderous.ponder.minecraft.bukkit.abs.PonderBukkitPlugin;
import preponderous.ponder.minecraft.bukkit.services.CommandService;
import preponderous.ponder.minecraft.bukkit.tools.EventHandlerRegistry;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Daniel McCoy Stephenson
 */
public final class WildPets extends PonderBukkitPlugin {
    private static WildPets instance;
    private final String pluginVersion = "v" + getDescription().getVersion();
    private CommandService commandService;

    public static WildPets getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerEventHandlers();
        initializeCommandService();

        // create/load config
        if (!(new File("./plugins/WildPets/config.yml").exists())) {
            EntityConfigManager.getInstance().initializeWithDefaults();
            ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        else {
            // pre load compatibility checks
            if (isVersionMismatched()) {
                ConfigManager.getInstance().saveMissingConfigDefaultsIfNotPresent();
            }
            reloadConfig();
            EntityConfigManager.getInstance().initializeWithConfig();
        }
        Scheduler.getInstance().scheduleAutosave();
        StorageManager.getInstance().load();
        handlebStatsIntegration();
    }

    private void handlebStatsIntegration() {
        int pluginId = 12332;
        Metrics metrics = new Metrics(this, pluginId);
    }

    @Override
    public void onDisable() {
        StorageManager.getInstance().save();
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return commandService.interpretAndExecuteCommand(sender, label, args);
    }

    public String getVersion() {
        return pluginVersion;
    }

    public boolean isDebugEnabled() {
        return getConfig().getBoolean("configOptions.debugMode");
    }

    public boolean isVersionMismatched() {
        return !getConfig().getString("version").equalsIgnoreCase(getVersion());
    }

    private void registerEventHandlers() {
        ArrayList<Listener> listeners = new ArrayList<>();
        listeners.add(new DamageEffectsAndDeathHandler());
        listeners.add(new InteractionHandler());
        listeners.add(new JoinAndQuitHandler());
        listeners.add(new MoveHandler());
        listeners.add(new BreedEventHandler());
        EventHandlerRegistry eventHandlerRegistry = new EventHandlerRegistry();
        eventHandlerRegistry.registerEventHandlers(listeners, this);
    }

    private void initializeCommandService() {
        ArrayList<AbstractPluginCommand> commands = new ArrayList<>(Arrays.asList(
                new CallCommand(), new CheckAccessCommand(), new ConfigCommand(),
                new FollowCommand(), new GrantAccessCommand(), new HelpCommand(),
                new InfoCommand(), new ListCommand(), new LocateCommand(),
                new LockCommand(), new RenameCommand(), new RevokeAccessCommand(),
                new SelectCommand(), new SetFreeCommand(), new StatsCommand(),
                new StayCommand(), new TameCommand(), new UnlockCommand(),
                new WanderCommand(), new GatherCommand()
        ));
        commandService.initialize(commands, "That command wasn't found.");
    }
}