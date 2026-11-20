package dansplugins.wildpets.commands;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Every permission a command checks must be declared in plugin.yml. Bukkit gives an undeclared
 * permission to operators only, so a player command whose permission is missing there silently
 * becomes op-only (#416: wp.stay, wp.checkaccess, wp.default).
 */
public class DeclaredPermissionsTest {
    private static final Pattern PERMISSION = Pattern.compile("\"(wp(?:\\.[a-z]+)*)\"");

    @Test
    public void everyPermissionACommandChecksIsDeclaredInPluginYml() throws IOException, InvalidConfigurationException {
        Set<String> used = new TreeSet<>();
        try (Stream<Path> files = Files.list(Paths.get("src/main/java/dansplugins/wildpets/commands"))) {
            for (Path file : (Iterable<Path>) files::iterator) {
                Matcher m = PERMISSION.matcher(new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
                while (m.find()) {
                    used.add(m.group(1));
                }
            }
        }
        // Permission names contain dots, which Bukkit would otherwise read as nested sections.
        YamlConfiguration pluginYml = new YamlConfiguration();
        pluginYml.options().pathSeparator('/');
        pluginYml.load(new File("src/main/resources/plugin.yml"));
        Set<String> declared = pluginYml.getConfigurationSection("permissions").getKeys(false);

        Set<String> undeclared = new TreeSet<>(used);
        undeclared.removeAll(declared);
        assertEquals("permissions checked by a command but not declared in plugin.yml", new TreeSet<String>(), undeclared);
    }
}
