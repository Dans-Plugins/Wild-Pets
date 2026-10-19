package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
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

public class CallCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Entity mockEntity;

    @Mock
    private Location mockPlayerLocation;

    private UUID playerUUID;
    private Pet pet;
    private EphemeralData ephemeralData;
    private CallCommand callCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getLocation()).thenReturn(mockPlayerLocation);
        when(mockServerProvider.get()).thenReturn(mockServer);
        pet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        ephemeralData = new EphemeralData();
        callCommand = new CallCommand(ephemeralData, mockServerProvider);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersCallSubcommandAndPermission() {
        assertEquals(1, callCommand.getNames().size());
        assertTrue(callCommand.getNames().contains("call"));
        assertEquals(1, callCommand.getPermissions().size());
        assertTrue(callCommand.getPermissions().contains("wp.call"));
    }

    @Test
    public void testExecuteRejectsWhenNoPetSelected() {
        assertFalse(callCommand.execute(mockPlayer));

        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("No pet selected.")));
    }

    @Test
    public void testExecuteTeleportsSelectedPetToPlayer() {
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(mockEntity);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(callCommand.execute(mockPlayer));

        verify(mockEntity).teleport(mockPlayerLocation);
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Rex has answered your call.")));
    }

    @Test
    public void testExecuteReportsPetThatCannotBeFound() {
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(null);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertFalse(callCommand.execute(mockPlayer));

        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Rex cannot be found.")));
    }

    @Test
    public void testExecuteWithArgumentsDelegatesToExecute() {
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(mockEntity);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(callCommand.execute(mockPlayer, new String[]{"ignored"}));

        verify(mockEntity).teleport(mockPlayerLocation);
    }
}
