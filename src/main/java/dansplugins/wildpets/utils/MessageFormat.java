package dansplugins.wildpets.utils;

import org.bukkit.ChatColor;

/**
 * Utility class providing standardized message formatting following
 * Activity Tracker's command output style guide.
 */
public final class MessageFormat {

    private static final String FOOTER = ChatColor.GOLD + "\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500";

    private MessageFormat() {
        // Prevent instantiation
    }

    /**
     * Builds a box-drawing header line.
     * @param title The bold title text.
     * @param subtitle The subtitle text.
     * @return The formatted header string.
     */
    public static String header(String title, String subtitle) {
        return ChatColor.GOLD + "\u250C\u2500 " + ChatColor.YELLOW + "" + ChatColor.BOLD + title +
                ChatColor.RESET + ChatColor.GOLD + " \u2500 " + subtitle;
    }

    /**
     * Builds a box-drawing body line.
     * @param content The content to display.
     * @return The formatted body line string.
     */
    public static String line(String content) {
        return ChatColor.GOLD + "\u2502 " + content;
    }

    /**
     * Returns the standard box-drawing footer.
     * @return The formatted footer string with 25 horizontal dashes.
     */
    public static String footer() {
        return FOOTER;
    }

    /**
     * Sends a single-line success box (blank line, header, body line, footer) to a CommandSender.
     * @param sender The command sender to receive the messages.
     * @param subtitle The subtitle for the header.
     * @param message The body message content.
     */
    public static void sendSuccessBox(org.bukkit.command.CommandSender sender, String subtitle, String message) {
        sender.sendMessage("");
        sender.sendMessage(header("Wild Pets", subtitle));
        sender.sendMessage(line(message));
        sender.sendMessage(footer());
    }

    /**
     * Creates a visual progress bar based on a value relative to its maximum.
     * @param value The current value.
     * @param max The maximum value.
     * @return A 10-character bar enclosed in brackets.
     */
    public static String createBar(double value, double max) {
        int barLength = 10;
        int filled = 0;
        if (max > 0) {
            filled = (int) Math.min(barLength, (value / max) * barLength);
        }
        StringBuilder bar = new StringBuilder(ChatColor.DARK_GRAY + "[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "\u2588" : "\u2591");
        }
        bar.append("]" + ChatColor.RESET);
        return bar.toString();
    }

    /**
     * Creates a visual ranking bar where rank 1 is the best (fullest bar).
     * @param rank The current rank (1-based).
     * @param total The total number of entries.
     * @return A 10-character bar enclosed in brackets.
     */
    public static String createRankBar(int rank, int total) {
        int barLength = 10;
        int filled = 0;
        if (total > 0) {
            filled = (int) Math.round(((double) (total - rank + 1) / total) * barLength);
            filled = Math.max(0, Math.min(barLength, filled));
        }
        StringBuilder bar = new StringBuilder(ChatColor.DARK_GRAY + "[");
        for (int i = 0; i < barLength; i++) {
            bar.append(i < filled ? "\u2588" : "\u2591");
        }
        bar.append("]" + ChatColor.RESET);
        return bar.toString();
    }
}
