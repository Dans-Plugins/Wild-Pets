package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
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

public class StayCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private CommandSender mockConsoleSender;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Mob mockMob;

    private UUID playerUUID;
    private Pet pet;
    private EphemeralData ephemeralData;
    private StayCommand stayCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockServerProvider.get()).thenReturn(mockServer);
        UUID petUUID = UUID.randomUUID();
        when(mockServer.getEntity(petUUID)).thenReturn(mockMob);
        pet = new Pet(petUUID, playerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        ephemeralData = new EphemeralData();
        stayCommand = new StayCommand(ephemeralData);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersStaySubcommandAndPermission() {
        assertEquals(1, stayCommand.getNames().size());
        assertTrue(stayCommand.getNames().contains("stay"));
        assertEquals(1, stayCommand.getPermissions().size());
        assertTrue(stayCommand.getPermissions().contains("wp.stay"));
    }

    @Test
    public void testExecuteSetsSelectedPetToStayingAndDisablesAI() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(stayCommand.execute(mockPlayer));

        assertEquals("Staying", pet.getMovementState());
        verify(mockMob).setAware(false);
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Rex is now staying.")));
    }

    @Test
    public void testExecuteWithArgumentsIgnoresArguments() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(stayCommand.execute(mockPlayer, new String[]{"extra"}));

        assertEquals("Staying", pet.getMovementState());
    }

    @Test
    public void testExecuteWithNoSelectionSendsErrorAndReturnsFalse() {
        assertFalse(stayCommand.execute(mockPlayer));

        assertEquals("Wandering", pet.getMovementState());
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("No pet selected.")));
    }

    @Test
    public void testExecuteReturnsFalseSilentlyForNonPlayerSender() {
        assertFalse(stayCommand.execute(mockConsoleSender));

        verify(mockConsoleSender, never()).sendMessage(anyString());
    }
}
