package dansplugins.wildpets.config;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Pins the bundled entity defaults. (WildPets is final, so EntityConfigService is exercised
 * through its static buildDefaults step; getDefaults only adds the debug line.)
 */
public class EntityConfigServiceTest {

    @Test
    public void testBuildDefaultsListsEveryEntityTypeOnce() {
        // execute
        ArrayList<EntityConfig> defaults = EntityConfigService.buildDefaults();

        // verify - a type that appears twice would be silently shadowed by acquireConfiguration
        Set<String> seen = new HashSet<>();
        for (EntityConfig entityConfig : defaults) {
            assertTrue("Duplicate default entity config for " + entityConfig.getType(), seen.add(entityConfig.getType()));
        }
    }

    @Test
    public void testBuildDefaultsListsEndermiteOnce() {
        // execute
        ArrayList<EntityConfig> defaults = EntityConfigService.buildDefaults();

        // verify - issue #316: Endermite was added twice, once out of alphabetical order
        int endermiteCount = 0;
        for (EntityConfig entityConfig : defaults) {
            if (entityConfig.getType().equals("Endermite")) {
                endermiteCount++;
            }
        }
        assertEquals(1, endermiteCount);
    }
}
