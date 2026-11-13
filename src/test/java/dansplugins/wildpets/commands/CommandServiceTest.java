package dansplugins.wildpets.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CommandServiceTest {
    @Mock
    private CommandSender mockSender;

    @Mock
    private Server mockServer;

    @Mock
    private AbstractPluginCommand mockTameCommand;

    @Mock
    private AbstractPluginCommand mockListCommand;

    private CommandService commandService;

    /**
     * JavaPlugin#getDescription is final, so the plugin is built through the
     * test-only initialization constructor with a real description instead of mocked.
     */
    private static class TestPlugin extends JavaPlugin {
        @SuppressWarnings("deprecation")
        TestPlugin(Server server, PluginDescriptionFile description) {
            super(new JavaPluginLoader(server), description, new File("target/test-plugin"), new File("target/test-plugin.jar"));
        }
    }

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockServer.getLogger()).thenReturn(Logger.getLogger("CommandServiceTest"));

        PluginDescriptionFile description = new PluginDescriptionFile(new StringReader(
                "name: WildPets\n" +
                "version: '1.0'\n" +
                "main: dansplugins.wildpets.WildPets\n" +
                "commands:\n" +
                "  wildpets:\n" +
                "  wp:\n"));
        commandService = new CommandService(new TestPlugin(mockServer, description));

        when(mockTameCommand.getNames()).thenReturn(new ArrayList<>(Arrays.asList("tame")));
        when(mockTameCommand.getPermissions()).thenReturn(new ArrayList<>(Arrays.asList("wp.tame")));
        when(mockListCommand.getNames()).thenReturn(new ArrayList<>(Arrays.asList("list", "ls")));
        when(mockListCommand.getPermissions()).thenReturn(new ArrayList<>(Arrays.asList("wp.list")));
        when(mockSender.hasPermission(anyString())).thenReturn(true);

        commandService.initialize(new ArrayList<>(Arrays.asList(mockTameCommand, mockListCommand)), "That command wasn't found.");
    }

    @Test
    public void testUnregisteredLabelIsIgnored() {
        assertFalse(commandService.interpretAndExecuteCommand(mockSender, "pets", new String[]{"tame"}));

        verify(mockTameCommand, never()).execute(any(CommandSender.class));
        verify(mockSender, never()).sendMessage(anyString());
    }

    @Test
    public void testLabelMatchIsCaseSensitive() {
        assertFalse(commandService.interpretAndExecuteCommand(mockSender, "WP", new String[]{"tame"}));

        verify(mockTameCommand, never()).execute(any(CommandSender.class));
        verify(mockSender, never()).sendMessage(anyString());
    }

    @Test
    public void testBothRegisteredLabelsDispatch() {
        when(mockTameCommand.execute(mockSender)).thenReturn(true);

        assertTrue(commandService.interpretAndExecuteCommand(mockSender, "wp", new String[]{"tame"}));
        assertTrue(commandService.interpretAndExecuteCommand(mockSender, "wildpets", new String[]{"tame"}));

        verify(mockTameCommand, times(2)).execute(mockSender);
    }

    @Test
    public void testNoArgumentsReturnsFalseWithoutMessage() {
        assertFalse(commandService.interpretAndExecuteCommand(mockSender, "wp", new String[0]));

        verify(mockTameCommand, never()).execute(any(CommandSender.class));
        verify(mockSender, never()).sendMessage(anyString());
    }

    @Test
    public void testSubcommandWithoutArgumentsUsesSingleArgumentExecute() {
        when(mockTameCommand.execute(mockSender)).thenReturn(true);

        assertTrue(commandService.interpretAndExecuteCommand(mockSender, "wp", new String[]{"tame"}));

        verify(mockTameCommand).execute(mockSender);
        verify(mockTameCommand, never()).execute(any(CommandSender.class), any(String[].class));
    }

    @Test
    public void testSubcommandWithArgumentsPassesRemainingArguments() {
        when(mockTameCommand.execute(eq(mockSender), any(String[].class))).thenReturn(true);

        assertTrue(commandService.interpretAndExecuteCommand(mockSender, "wp", new String[]{"tame", "Rex", "Jr"}));

        verify(mockTameCommand).execute(mockSender, new String[]{"Rex", "Jr"});
        verify(mockTameCommand, never()).execute(mockSender);
    }

    @Test
    public void testSubcommandMatchIsCaseInsensitive() {
        when(mockTameCommand.execute(mockSender)).thenReturn(true);

        assertTrue(commandService.interpretAndExecuteCommand(mockSender, "wp", new String[]{"TaMe"}));

        verify(mockTameCommand).execute(mockSender);
    }

    @Test
    public void testAnyOfACommandsNamesDispatches() {
        when(mockListCommand.execute(mockSender)).thenReturn(true);

        assertTrue(commandService.interpretAndExecuteCommand(mockSender, "wp", new String[]{"ls"}));

        verify(mockListCommand).execute(mockSender);
        verify(mockTameCommand, never()).execute(any(CommandSender.class));
    }

    @Test
    public void testCommandResultIsReturnedUnchanged() {
        when(mockTameCommand.execute(mockSender)).thenReturn(false);

        assertFalse(commandService.interpretAndExecuteCommand(mockSender, "wp", new String[]{"tame"}));

        verify(mockTameCommand).execute(mockSender);
    }

    @Test
    public void testMissingPermissionBlocksExecutionAndNamesRequiredPermission() {
        when(mockSender.hasPermission("wp.tame")).thenReturn(false);

        assertFalse(commandService.interpretAndExecuteCommand(mockSender, "wp", new String[]{"tame", "Rex"}));

        verify(mockTameCommand, never()).execute(any(CommandSender.class));
        verify(mockTameCommand, never()).execute(any(CommandSender.class), any(String[].class));
        verify(mockSender).sendMessage(ChatColor.RED + "In order to use this command, you need one of the following permissions: wp.tame");
    }

    @Test
    public void testUnknownSubcommandSendsNotFoundMessage() {
        assertFalse(commandService.interpretAndExecuteCommand(mockSender, "wp", new String[]{"fly", "high"}));

        verify(mockSender).sendMessage(ChatColor.RED + "That command wasn't found.");
        verify(mockTameCommand, never()).execute(any(CommandSender.class));
        verify(mockListCommand, never()).execute(any(CommandSender.class));
    }
}
