package dansplugins.wildpets;

import dansplugins.wildpets.bstats.Metrics;
import dansplugins.wildpets.commands.*;
import dansplugins.wildpets.eventhandlers.*;
import dansplugins.wildpets.services.LocalConfigService;
import dansplugins.wildpets.services.LocalEntityConfigService;
import dansplugins.wildpets.services.LocalStorageService;
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
    private CommandService commandService = new CommandService(getPonder());

     /**
     * This can be used to get the instance of the main class that is managed by itself.
     * @return The managed instance of the main class.
     */
    public static WildPets getInstance() {
        return instance;
    }

     /**
     * This runs when the server starts.
     */
    @Override
    public void onEnable() {
        instance = this;
        registerEventHandlers();
        initializeCommandService();
        initializeConfig();
        Scheduler.getInstance().scheduleAutosave();
        LocalStorageService.getInstance().load();
        handlebStatsIntegration();
    }

    private void initializeConfig() {
        if (configFileExists()) {
            performCompatibilityChecks();
        }
        else {
            LocalEntityConfigService.getInstance().initializeWithDefaults();
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
    }

    private boolean configFileExists() {
        return new File("./plugins/" + getName() + "/config.yml").exists();
    }

    private void performCompatibilityChecks() {
        if (isVersionMismatched()) {
            LocalConfigService.getInstance().saveMissingConfigDefaultsIfNotPresent();
        }
        reloadConfig();
        LocalEntityConfigService.getInstance().initializeWithConfig();
    }

    /**
     * This runs when the server stops.
     */
    @Override
    public void onDisable() {
        LocalStorageService.getInstance().save();
    }

    /**
     * This method handles commands sent to the minecraft server and interprets them if the label matches one of the core commands.
     * @param sender The sender of the command.
     * @param cmd The command that was sent. This is unused.
     * @param label The core command that has been invoked.
     * @param args Arguments of the core command. Often sub-commands.
     * @return A boolean indicating whether the execution of the command was successful.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            DefaultCommand defaultCommand = new DefaultCommand();
            return defaultCommand.execute(sender);
        }

        return commandService.interpretAndExecuteCommand(sender, label, args);
    }

    /**
     * This can be used to get the version of the plugin.
     * @return A string containing the version preceded by 'v'
     */
    public String getVersion() {
        return pluginVersion;
    }

    /**
     * Checks if debug is enabled.
     * @return Whether debug is enabled.
     */
    public boolean isDebugEnabled() {
        return LocalConfigService.getInstance().getBoolean("debugMode");
    }

    /**
     * Checks if the version is mismatched.
     * @return A boolean indicating if the version is mismatched.
     */
    public boolean isVersionMismatched() {
        String configVersion = this.getConfig().getString("version");
        if (configVersion == null || this.getVersion() == null) {
            return false;
        } else {
            return !configVersion.equalsIgnoreCase(this.getVersion());
        }
    }

    private void handlebStatsIntegration() {
        int pluginId = 12332;
        new Metrics(this, pluginId);
    }

    /**
     * Registers the event handlers of the plugin using Ponder.
     */
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

    /**
     * Initializes Ponder's command service with the plugin's commands.
     */
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