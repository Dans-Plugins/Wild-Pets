package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SelectCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    private UUID playerUUID;
    private Pet pet;
    private EphemeralData ephemeralData;
    private PetListRepository petListRepository;
    private SelectCommand selectCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(true);
        pet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        ephemeralData = new EphemeralData();
        petListRepository = new PetListRepository(mockConfigService, mockServerProvider);
        petListRepository.addExistingPet(pet);
        selectCommand = new SelectCommand(mockConfigService, ephemeralData, petListRepository);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    private boolean playerWasSent(String expectedMessage) {
        return captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains(expectedMessage));
    }

    @Test
    public void testRegistersSelectSubcommandAndPermission() {
        assertEquals(1, selectCommand.getNames().size());
        assertTrue(selectCommand.getNames().contains("select"));
        assertEquals(1, selectCommand.getPermissions().size());
        assertTrue(selectCommand.getPermissions().contains("wp.select"));
    }

    @Test
    public void testSelectByNameSelectsPlayersPet() {
        assertTrue(selectCommand.execute(mockPlayer, new String[]{"Rex"}));

        assertSame(pet, ephemeralData.getPetSelectionForPlayer(playerUUID));
        assertTrue(playerWasSent("Rex selected."));
    }

    @Test
    public void testSelectByNameIsCaseInsensitive() {
        assertTrue(selectCommand.execute(mockPlayer, new String[]{"rEX"}));

        assertSame(pet, ephemeralData.getPetSelectionForPlayer(playerUUID));
    }

    @Test
    public void testSelectByNameReplacesExistingSelection() {
        Pet otherPet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        otherPet.setName("Fido");
        petListRepository.addExistingPet(otherPet);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(selectCommand.execute(mockPlayer, new String[]{"Fido"}));

        assertSame(otherPet, ephemeralData.getPetSelectionForPlayer(playerUUID));
    }

    @Test
    public void testSelectByUnknownNameSendsErrorAndLeavesSelectionEmpty() {
        assertFalse(selectCommand.execute(mockPlayer, new String[]{"Ghost"}));

        assertNull(ephemeralData.getPetSelectionForPlayer(playerUUID));
        assertTrue(playerWasSent("You don't have any pets named Ghost."));
    }

    @Test
    public void testSelectByNameDoesNotMatchAnotherPlayersPet() {
        Pet strangersPet = new Pet(UUID.randomUUID(), UUID.randomUUID(), "Stranger", mockServerProvider);
        strangersPet.setName("Spot");
        petListRepository.addExistingPet(strangersPet);

        assertFalse(selectCommand.execute(mockPlayer, new String[]{"Spot"}));

        assertNull(ephemeralData.getPetSelectionForPlayer(playerUUID));
    }

    @Test
    public void testSelectWithoutNameIsUsageErrorWhenRightClickToSelectEnabled() {
        assertFalse(selectCommand.execute(mockPlayer));

        assertFalse(ephemeralData.isPlayerSelecting(playerUUID));
        assertTrue(playerWasSent("Usage: /wp select (petName)"));
    }

    @Test
    public void testSelectWithoutNameEntersSelectionModeWhenRightClickToSelectDisabled() {
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(false);

        assertTrue(selectCommand.execute(mockPlayer));

        assertTrue(ephemeralData.isPlayerSelecting(playerUUID));
        assertTrue(playerWasSent("Right click on an entity to select it."));
    }

    @Test
    public void testSelectCancelLeavesSelectionModeWhenRightClickToSelectDisabled() {
        when(mockConfigService.getBoolean("rightClickToSelect")).thenReturn(false);
        ephemeralData.setPlayerAsSelecting(playerUUID);

        assertTrue(selectCommand.execute(mockPlayer, new String[]{"CANCEL"}));

        assertFalse(ephemeralData.isPlayerSelecting(playerUUID));
        assertTrue(playerWasSent("Selecting cancelled."));
    }

    @Test
    public void testSelectCancelIsUsageErrorWhenRightClickToSelectEnabled() {
        ephemeralData.setPlayerAsSelecting(playerUUID);

        assertFalse(selectCommand.execute(mockPlayer, new String[]{"cancel"}));

        assertTrue(ephemeralData.isPlayerSelecting(playerUUID));
        assertTrue(playerWasSent("Usage: /wp select (petName)"));
    }
}
