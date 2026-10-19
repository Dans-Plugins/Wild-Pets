package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
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

public class GatherCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Entity mockLoadedEntity;

    @Mock
    private Location mockPlayerLocation;

    private UUID playerUUID;
    private PetListRepository petListRepository;
    private GatherCommand gatherCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getLocation()).thenReturn(mockPlayerLocation);
        when(mockServerProvider.get()).thenReturn(mockServer);
        petListRepository = new PetListRepository(mockConfigService, mockServerProvider);
        gatherCommand = new GatherCommand(petListRepository, mockServerProvider);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    private Pet addPet(String name) {
        Pet pet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        pet.setName(name);
        petListRepository.addExistingPet(pet);
        return pet;
    }

    @Test
    public void testRegistersGatherSubcommandAndPermission() {
        assertEquals(1, gatherCommand.getNames().size());
        assertTrue(gatherCommand.getNames().contains("gather"));
        assertEquals(1, gatherCommand.getPermissions().size());
        assertTrue(gatherCommand.getPermissions().contains("wp.gather"));
    }

    @Test
    public void testExecuteRejectsPlayerWithNoPets() {
        petListRepository.createPetListForPlayer(playerUUID);

        assertFalse(gatherCommand.execute(mockPlayer));

        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("You don't have any pets.")));
    }

    @Test
    public void testExecuteTeleportsLoadedPetsAndSkipsUnloadedOnes() {
        Pet loadedPet = addPet("Rex");
        Pet unloadedPet = addPet("Max");
        when(mockServer.getEntity(loadedPet.getUniqueID())).thenReturn(mockLoadedEntity);
        when(mockServer.getEntity(unloadedPet.getUniqueID())).thenReturn(null);

        assertTrue(gatherCommand.execute(mockPlayer));

        verify(mockLoadedEntity).teleport(mockPlayerLocation);
        verify(mockServer).getEntity(unloadedPet.getUniqueID());
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Your pets gather nearby.")));
    }

    @Test
    public void testExecuteWithArgumentsDelegatesToExecute() {
        Pet pet = addPet("Rex");
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(mockLoadedEntity);

        assertTrue(gatherCommand.execute(mockPlayer, new String[]{"ignored"}));

        verify(mockLoadedEntity).teleport(mockPlayerLocation);
    }
}
