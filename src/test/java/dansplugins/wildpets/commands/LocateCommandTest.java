package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.location.WpLocation;
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

public class LocateCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Entity mockEntity;

    @Mock
    private Location mockEntityLocation;

    private UUID playerUUID;
    private Pet pet;
    private EphemeralData ephemeralData;
    private LocateCommand locateCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockEntity.getLocation()).thenReturn(mockEntityLocation);
        when(mockEntityLocation.getX()).thenReturn(12.7);
        when(mockEntityLocation.getY()).thenReturn(64.0);
        when(mockEntityLocation.getZ()).thenReturn(-30.2);
        pet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        pet.setLastKnownLocation(new WpLocation(1, 2, 3));
        ephemeralData = new EphemeralData();
        locateCommand = new LocateCommand(ephemeralData, mockServerProvider);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersLocateSubcommandAndPermission() {
        assertEquals(1, locateCommand.getNames().size());
        assertTrue(locateCommand.getNames().contains("locate"));
        assertEquals(1, locateCommand.getPermissions().size());
        assertTrue(locateCommand.getPermissions().contains("wp.locate"));
    }

    @Test
    public void testExecuteRejectsWhenNoPetSelected() {
        assertFalse(locateCommand.execute(mockPlayer));

        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("No pet selected.")));
    }

    @Test
    public void testExecuteUpdatesLastKnownLocationFromLoadedEntity() {
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(mockEntity);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(locateCommand.execute(mockPlayer));

        assertEquals(new WpLocation(12.7, 64.0, -30.2), pet.getLastKnownLocation());
        WpLocation expected = pet.getLastKnownLocation();
        String expectedCoordinates = String.format("(%s, %s, %s)", expected.getX(), expected.getY(), expected.getZ());
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains(expectedCoordinates)));
    }

    @Test
    public void testExecuteReportsStoredLocationWhenEntityIsNotLoaded() {
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(null);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(locateCommand.execute(mockPlayer));

        assertEquals(new WpLocation(1, 2, 3), pet.getLastKnownLocation());
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("(1, 2, 3)")));
    }
}
