package dansplugins.wildpets.config;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Pins the bundled entity defaults and how a key is matched to an entity type. (WildPets is
 * final, so EntityConfigService is exercised through its static buildDefaults and
 * findConfiguration steps; getDefaults and acquireConfiguration add only the debug lines and
 * the fallback.)
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

    @Test
    public void testBuildDefaultsKeysContainNoSpaces() {
        // execute
        ArrayList<EntityConfig> defaults = EntityConfigService.buildDefaults();

        // verify - issue #392: an entity type name has no spaces, so such a key can never match
        for (EntityConfig entityConfig : defaults) {
            assertFalse("Unmatchable key: " + entityConfig.getType(), entityConfig.getType().contains(" "));
        }
    }

    @Test
    public void testWanderingTraderUsesItsBundledConfiguration() {
        // execute
        EntityConfig entityConfig = EntityConfigService.findConfiguration(
                EntityConfigService.buildDefaults(), EntityType.WANDERING_TRADER.name());

        // verify - issue #392: "Wandering Trader" never matched, so the fallback was used instead
        assertNotNull(entityConfig);
        assertEquals(Material.GOLD_INGOT, entityConfig.getRequiredTamingItem());
        assertEquals(8, entityConfig.getTamingItemAmount());
        assertEquals(0.5, entityConfig.getChanceToSucceed(), 0.0);
    }

    @Test
    public void testFindConfigurationIgnoresCase() {
        // setup
        EntityConfig glowSquid = new EntityConfig("Glow_Squid", 0.5, Material.KELP, 24, true);

        // execute & verify
        assertSame(glowSquid, EntityConfigService.findConfiguration(Collections.singletonList(glowSquid), "GLOW_SQUID"));
    }

    @Test
    public void testFindConfigurationReturnsNullWhenNoKeyMatches() {
        // setup
        EntityConfig glowSquid = new EntityConfig("Glow_Squid", 0.5, Material.KELP, 24, true);

        // execute & verify
        assertNull(EntityConfigService.findConfiguration(Collections.singletonList(glowSquid), "SQUID"));
    }
}
