package dansplugins.wildpets.ponder.misc;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for parsing command arguments.
 */
public class ArgumentParser {

    /**
     * @param args to modify.
     * @return Modified Array of Strings with the first argument dropped.
     * @throws IllegalArgumentException if the arguments given are invalid.
     */
    public String[] dropFirstArgument(String[] args) {
        ensureArgumentsExist(args);
        String[] toReturn = new String[args.length - 1];
        System.arraycopy(args, 1, toReturn, 0, args.length - 1);
        return toReturn;
    }

    /**
     * @param args to compile and scan.
     * @return {@link ArrayList} of {@link String} which were surrounded by double quotes.
     * @throws IllegalArgumentException if the arguments given are invalid.
     */
    public ArrayList<String> getArgumentsInsideDoubleQuotes(String[] args) {
        ensureArgumentsExist(args);
        return parseForArguments(args);
    }

    private ArrayList<String> parseForArguments(String[] args) {
        ArrayList<String> toReturn = new ArrayList<>();
        final String argumentString = String.join(" ", args);
        final Matcher matcher = Pattern.compile("\"[^\"]*\"").matcher(argumentString);
        while (matcher.find()) {
            toReturn.add(matcher.group().replace("\"", ""));
        }
        return toReturn;
    }

    private void ensureArgumentsExist(String[] args) {
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Arguments not valid.");
        }
    }
}
