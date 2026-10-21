package dansplugins.wildpets.listeners;

import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetList;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Mockito.*;

public class MoveHandlerTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private PetListRepository mockPetListRepository;

    @Mock
    private PetList mockPetList;

    @Mock
    private Location mockFrom;

    @Mock
    private Location mockTo;

    @Mock
    private Chunk mockFromChunk;

    @Mock
    private Chunk mockToChunk;

    @Mock
    private Entity mockPetEntity;

    private UUID playerUUID;
    private PlayerMoveEvent event;
    private Pet pet;
    private MoveHandler moveHandler;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockServerProvider.get()).thenReturn(mockServer);
        event = new PlayerMoveEvent(mockPlayer, mockFrom, mockTo);
        when(mockFrom.getChunk()).thenReturn(mockFromChunk);
        when(mockTo.getChunk()).thenReturn(mockToChunk);
        when(mockFromChunk.getX()).thenReturn(0);
        when(mockFromChunk.getZ()).thenReturn(0);
        when(mockToChunk.getX()).thenReturn(1);
        when(mockToChunk.getZ()).thenReturn(0);
        pet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(mockPetEntity);
        when(mockPetListRepository.getPetList(playerUUID)).thenReturn(mockPetList);
        when(mockPetList.getFollowingPets()).thenReturn(new ArrayList<>(Arrays.asList(pet)));
        moveHandler = new MoveHandler(mockPetListRepository, mockServerProvider);
    }

    @Test
    public void testFollowingPetIsTeleportedWhenPlayerChangesChunk() {
        moveHandler.handle(event);

        verify(mockServer).getEntity(pet.getUniqueID());
        verify(mockPetEntity).teleport(mockPlayer);
    }

    @Test
    public void testUnloadedFollowingPetIsSkippedAndOthersStillTeleport() {
        Pet unloadedPet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        when(mockServer.getEntity(unloadedPet.getUniqueID())).thenReturn(null);
        when(mockPetList.getFollowingPets()).thenReturn(new ArrayList<>(Arrays.asList(unloadedPet, pet)));

        moveHandler.handle(event);

        verify(mockServer).getEntity(unloadedPet.getUniqueID());
        verify(mockPetEntity).teleport(mockPlayer);
    }

    @Test
    public void testNoFollowingPetsDoesNotQueryServer() {
        when(mockPetList.getFollowingPets()).thenReturn(new ArrayList<>());

        moveHandler.handle(event);

        verifyNoInteractions(mockServerProvider);
    }

    @Test
    public void testMovementWithinSameChunkIsIgnored() {
        when(mockToChunk.getX()).thenReturn(0);

        moveHandler.handle(event);

        verifyNoInteractions(mockPetListRepository, mockServerProvider);
    }

    @Test
    public void testMovementWithNoDestinationIsIgnored() {
        moveHandler.handle(new PlayerMoveEvent(mockPlayer, mockFrom, null));

        verifyNoInteractions(mockPetListRepository, mockServerProvider);
    }
}
