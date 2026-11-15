package dansplugins.wildpets.listeners;

import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.event.world.ChunkLoadEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.*;

public class ChunkLoadHandlerTest {
    @Mock
    private PetListRepository mockPetListRepository;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Chunk mockChunk;

    @Mock
    private Mob mockPetMob;

    @Mock
    private Entity mockPetEntity;

    @Mock
    private Mob mockWildMob;

    private ChunkLoadEvent event;
    private ChunkLoadHandler handler;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        event = new ChunkLoadEvent(mockChunk, false);
        handler = new ChunkLoadHandler(mockPetListRepository);
    }

    private Pet newPet() {
        return new Pet(UUID.randomUUID(), UUID.randomUUID(), "Daniel", mockServerProvider);
    }

    @Test
    public void skipsChunkScanWhenNoPetsAreTracked() {
        when(mockPetListRepository.hasNoTrackedPets()).thenReturn(true);

        handler.handle(event);

        verify(mockChunk, never()).getEntities();
        verify(mockPetListRepository, never()).getPet(any());
    }

    @Test
    public void appliesPersistenceFlagsToPetMobsInLoadedChunk() {
        when(mockPetListRepository.hasNoTrackedPets()).thenReturn(false);
        when(mockChunk.getEntities()).thenReturn(new Entity[]{mockPetMob});
        when(mockPetListRepository.getPet(mockPetMob)).thenReturn(newPet());

        handler.handle(event);

        verify(mockPetMob).setPersistent(true);
        verify(mockPetMob).setRemoveWhenFarAway(false);
    }

    @Test
    public void setsPersistentOnNonMobPetEntity() {
        when(mockPetListRepository.hasNoTrackedPets()).thenReturn(false);
        when(mockChunk.getEntities()).thenReturn(new Entity[]{mockPetEntity});
        when(mockPetListRepository.getPet(mockPetEntity)).thenReturn(newPet());

        handler.handle(event);

        verify(mockPetEntity).setPersistent(true);
    }

    @Test
    public void leavesEntitiesThatAreNotPetsUntouched() {
        when(mockPetListRepository.hasNoTrackedPets()).thenReturn(false);
        when(mockChunk.getEntities()).thenReturn(new Entity[]{mockWildMob, mockPetMob});
        when(mockPetListRepository.getPet(mockWildMob)).thenReturn(null);
        when(mockPetListRepository.getPet(mockPetMob)).thenReturn(newPet());

        handler.handle(event);

        verify(mockWildMob, never()).setPersistent(anyBoolean());
        verify(mockWildMob, never()).setRemoveWhenFarAway(anyBoolean());
        verify(mockPetMob).setPersistent(true);
        verify(mockPetMob).setRemoveWhenFarAway(false);
    }

    @Test
    public void handlesEmptyChunk() {
        when(mockPetListRepository.hasNoTrackedPets()).thenReturn(false);
        when(mockChunk.getEntities()).thenReturn(new Entity[0]);

        handler.handle(event);

        verify(mockPetListRepository, never()).getPet(any());
    }
}
