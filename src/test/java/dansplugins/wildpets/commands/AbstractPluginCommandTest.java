package dansplugins.wildpets.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AbstractPluginCommandTest {
    @Mock
    private CommandSender mockSender;

    private AbstractPluginCommand command;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        command = new AbstractPluginCommand(
                new ArrayList<>(Arrays.asList("rename")),
                new ArrayList<>(Arrays.asList("wp.rename"))) {
            @Override
            public boolean execute(CommandSender sender) {
                return false;
            }

            @Override
            public boolean execute(CommandSender sender, String[] args) {
                return false;
            }
        };
    }

    @Test
    public void testGetNamesAndPermissionsReturnConstructorValues() {
        assertEquals(Arrays.asList("rename"), command.getNames());
        assertEquals(Arrays.asList("wp.rename"), command.getPermissions());
    }

    @Test
    public void testSendMessageIfNoArgumentsSendsColoredMessageWhenArgsEmpty() {
        assertTrue(command.sendMessageIfNoArguments("Usage: /wp rename", new String[0], mockSender, ChatColor.RED));

        verify(mockSender).sendMessage(ChatColor.RED + "Usage: /wp rename");
    }

    @Test
    public void testSendMessageIfNoArgumentsIsSilentWhenArgsPresent() {
        assertFalse(command.sendMessageIfNoArguments("Usage: /wp rename", new String[]{"Rex"}, mockSender, ChatColor.RED));

        verify(mockSender, never()).sendMessage(anyString());
    }

    @Test
    public void testGetIntSafeParsesValidInteger() {
        assertEquals(42, command.getIntSafe("42", -1));
        assertEquals(-7, command.getIntSafe("-7", 0));
    }

    @Test
    public void testGetIntSafeReturnsFallbackForInvalidInput() {
        assertEquals(-1, command.getIntSafe("abc", -1));
        assertEquals(-1, command.getIntSafe("", -1));
        assertEquals(-1, command.getIntSafe(null, -1));
        assertEquals(-1, command.getIntSafe("99999999999", -1));
    }

    @Test
    public void testSafeEqualsMatchCaseRequiresExactMatch() {
        assertTrue(command.safeEquals(true, "Rex", "Buddy", "Rex"));
        assertFalse(command.safeEquals(true, "rex", "Buddy", "Rex"));
    }

    @Test
    public void testSafeEqualsIgnoreCaseMatchesAnyCase() {
        assertTrue(command.safeEquals(false, "rex", "Buddy", "REX"));
        assertFalse(command.safeEquals(false, "Max", "Buddy", "Rex"));
    }

    @Test
    public void testSafeEqualsWithNoGoalsReturnsFalse() {
        assertFalse(command.safeEquals(false, "Rex"));
    }

    @Test
    public void testExtractArgumentsInsideDoubleQuotesReturnsAllQuotedArguments() throws Exception {
        String[] args = {"\"Old", "Name\"", "\"New\""};

        assertEquals(Arrays.asList("Old Name", "New"), command.extractArgumentsInsideDoubleQuotes(args));
    }

    @Test(expected = Exception.class)
    public void testExtractArgumentsInsideDoubleQuotesRejectsFewerThanTwoQuotedArguments() throws Exception {
        command.extractArgumentsInsideDoubleQuotes(new String[]{"\"Rex\"", "Buddy"});
    }
}
