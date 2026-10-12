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

public class FollowCommandTest {
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
    private FollowCommand followCommand;

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
        // Start from a non-default state so the switch to following is observable
        pet.setStaying();
        reset(mockMob);
        ephemeralData = new EphemeralData();
        followCommand = new FollowCommand(ephemeralData);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersFollowSubcommandAndPermission() {
        assertEquals(1, followCommand.getNames().size());
        assertTrue(followCommand.getNames().contains("follow"));
        assertEquals(1, followCommand.getPermissions().size());
        assertTrue(followCommand.getPermissions().contains("wp.follow"));
    }

    @Test
    public void testExecuteSetsSelectedPetToFollowingAndEnablesAI() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(followCommand.execute(mockPlayer));

        assertEquals("Following", pet.getMovementState());
        verify(mockMob).setAware(true);
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Rex is now following you.")));
    }

    @Test
    public void testExecuteWithArgumentsIgnoresArguments() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(followCommand.execute(mockPlayer, new String[]{"extra"}));

        assertEquals("Following", pet.getMovementState());
    }

    @Test
    public void testExecuteWithNoSelectionSendsErrorAndReturnsFalse() {
        assertFalse(followCommand.execute(mockPlayer));

        assertEquals("Staying", pet.getMovementState());
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("No pet selected.")));
    }

    @Test
    public void testExecuteReturnsFalseSilentlyForNonPlayerSender() {
        assertFalse(followCommand.execute(mockConsoleSender));

        verify(mockConsoleSender, never()).sendMessage(anyString());
    }
}
