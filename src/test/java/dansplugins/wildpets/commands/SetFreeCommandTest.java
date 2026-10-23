package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.Server;
import org.bukkit.entity.Mob;
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

public class SetFreeCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Mob mockMob;

    private UUID playerUUID;
    private UUID petUUID;
    private Pet pet;
    private EphemeralData ephemeralData;
    private PetListRepository petListRepository;
    private SetFreeCommand setFreeCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockServerProvider.get()).thenReturn(mockServer);
        petUUID = UUID.randomUUID();
        when(mockServer.getEntity(petUUID)).thenReturn(mockMob);
        pet = new Pet(petUUID, playerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        ephemeralData = new EphemeralData();
        petListRepository = new PetListRepository(mockConfigService, mockServerProvider);
        petListRepository.addExistingPet(pet);
        setFreeCommand = new SetFreeCommand(ephemeralData, petListRepository);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersSetFreeSubcommandAndPermission() {
        assertEquals(1, setFreeCommand.getNames().size());
        assertTrue(setFreeCommand.getNames().contains("setfree"));
        assertEquals(1, setFreeCommand.getPermissions().size());
        assertTrue(setFreeCommand.getPermissions().contains("wp.setfree"));
    }

    @Test
    public void testExecuteRemovesSelectedPetAndClearsSelection() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(setFreeCommand.execute(mockPlayer));

        assertNull(petListRepository.getPlayersPet(mockPlayer, "Rex"));
        assertTrue(petListRepository.getAllPets().isEmpty());
        assertNull(ephemeralData.getPetSelectionForPlayer(playerUUID));
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Rex has been set free.")));
    }

    @Test
    public void testExecuteResetsLoadedEntityState() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(setFreeCommand.execute(mockPlayer));

        verify(mockMob).setCustomName("");
        verify(mockMob).setPersistent(false);
        verify(mockMob).setRemoveWhenFarAway(true);
        verify(mockMob).setInvulnerable(false);
    }

    @Test
    public void testExecuteWithArgumentsIgnoresArguments() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(setFreeCommand.execute(mockPlayer, new String[]{"extra"}));

        assertTrue(petListRepository.getAllPets().isEmpty());
    }

    @Test
    public void testExecuteWithNoSelectionSendsErrorAndKeepsPet() {
        assertFalse(setFreeCommand.execute(mockPlayer));

        assertSame(pet, petListRepository.getPlayersPet(mockPlayer, "Rex"));
        verify(mockMob, never()).setCustomName(anyString());
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("No pet selected.")));
    }
}
