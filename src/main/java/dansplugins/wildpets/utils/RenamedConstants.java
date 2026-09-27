package dansplugins.wildpets.utils;

import org.bukkit.Material;

import java.util.Arrays;

/**
 * Constants that later Minecraft versions renamed, looked up by name so that one jar works on
 * every version listed in minecraft-versions.json. A direct reference such as
 * {@code Material.GRASS} names a field that only exists before the rename: newer servers
 * rewrite it when they load the plugin, but nothing guarantees they keep doing so, and the
 * build's API-compatibility check rejects it. Each lookup tries the current name first, then
 * the older one.
 */
public final class RenamedConstants {

    private RenamedConstants() {
    }

    /** The first of {@code names} this server's Material has. */
    public static Material material(String... names) {
        for (String name : names) {
            Material material = Material.getMaterial(name);
            if (material != null) {
                return material;
            }
        }
        throw new IllegalStateException("None of " + Arrays.toString(names) + " is a Material on this server");
    }

    /** Short grass: {@code SHORT_GRASS} from 1.20.3, {@code GRASS} before. */
    public static Material shortGrass() {
        return material("SHORT_GRASS", "GRASS");
    }
}
