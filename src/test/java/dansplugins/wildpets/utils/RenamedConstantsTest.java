package dansplugins.wildpets.utils;

import org.bukkit.Material;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RenamedConstantsTest {

    @Test
    public void takesTheFirstNameThisServerHas() {
        // the build's spigot-api predates the rename, so only the old name exists here
        assertEquals(Material.getMaterial("GRASS"), RenamedConstants.shortGrass());
        assertEquals(Material.DIRT, RenamedConstants.material("NOT_A_MATERIAL", "DIRT"));
    }

    @Test(expected = IllegalStateException.class)
    public void refusesWhenNoNameExists() {
        RenamedConstants.material("NOT_A_MATERIAL");
    }
}
