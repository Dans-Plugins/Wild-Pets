package dansplugins.wildpets.commands;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class ArgumentParserTest {
    private ArgumentParser argumentParser;

    @Before
    public void setup() {
        argumentParser = new ArgumentParser();
    }

    @Test
    public void testDropFirstArgumentRemovesOnlyTheFirstElement() {
        String[] result = argumentParser.dropFirstArgument(new String[]{"rename", "Rex", "Buddy"});

        assertArrayEquals(new String[]{"Rex", "Buddy"}, result);
    }

    @Test
    public void testDropFirstArgumentOfSingleArgumentReturnsEmptyArray() {
        String[] result = argumentParser.dropFirstArgument(new String[]{"list"});

        assertEquals(0, result.length);
    }

    @Test
    public void testDropFirstArgumentDoesNotModifyInput() {
        String[] args = {"rename", "Rex"};

        argumentParser.dropFirstArgument(args);

        assertArrayEquals(new String[]{"rename", "Rex"}, args);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDropFirstArgumentRejectsEmptyArray() {
        argumentParser.dropFirstArgument(new String[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDropFirstArgumentRejectsNull() {
        argumentParser.dropFirstArgument(null);
    }

    @Test
    public void testGetArgumentsInsideDoubleQuotesJoinsMultiWordArguments() {
        String[] args = {"\"Old", "Name\"", "\"New", "Name\""};

        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(args);

        assertEquals(Arrays.asList("Old Name", "New Name"), result);
    }

    @Test
    public void testGetArgumentsInsideDoubleQuotesIgnoresUnquotedArguments() {
        String[] args = {"trade", "\"Rex\"", "Steve"};

        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(args);

        assertEquals(Arrays.asList("Rex"), result);
    }

    @Test
    public void testGetArgumentsInsideDoubleQuotesReturnsEmptyListWhenNothingIsQuoted() {
        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(new String[]{"Rex", "Buddy"});

        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetArgumentsInsideDoubleQuotesKeepsEmptyQuotedArgument() {
        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(new String[]{"\"\"", "\"Rex\""});

        assertEquals(Arrays.asList("", "Rex"), result);
    }

    @Test
    public void testGetArgumentsInsideDoubleQuotesIgnoresUnterminatedQuote() {
        ArrayList<String> result = argumentParser.getArgumentsInsideDoubleQuotes(new String[]{"\"Rex\"", "\"Buddy"});

        assertEquals(Arrays.asList("Rex"), result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgumentsInsideDoubleQuotesRejectsEmptyArray() {
        argumentParser.getArgumentsInsideDoubleQuotes(new String[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetArgumentsInsideDoubleQuotesRejectsNull() {
        argumentParser.getArgumentsInsideDoubleQuotes(null);
    }
}
