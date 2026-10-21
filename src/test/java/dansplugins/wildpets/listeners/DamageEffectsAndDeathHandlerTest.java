package dansplugins.wildpets.listeners;

import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Covers the EntityDeathEvent handler. WildPets is final and cannot be mocked, so the handler is
 * built with a null plugin; the removal-failure path (which only logs when debug is enabled) is
 * therefore not exercised here.
 */
public class DamageEffectsAndDeathHandlerTest {
    @Mock
    private Player mockOwner;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private PetListRepository mockPetListRepository;

    @Mock
    private EntityDeathEvent mockEvent;

    @Mock
    private LivingEntity mockEntity;

    private UUID ownerUUID;
    private Pet pet;
    private DamageEffectsAndDeathHandler handler;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ownerUUID = UUID.randomUUID();
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getPlayer(ownerUUID)).thenReturn(mockOwner);
        when(mockEvent.getEntity()).thenReturn(mockEntity);
        pet = new Pet(UUID.randomUUID(), ownerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        when(mockPetListRepository.getPet(mockEntity)).thenReturn(pet);
        when(mockPetListRepository.removePet(pet)).thenReturn(true);
        handler = new DamageEffectsAndDeathHandler(null, mockPetListRepository, null, mockServerProvider);
    }

    @Test
    public void testPetDeathRemovesPetAndNotifiesOnlineOwner() {
        handler.handle(mockEvent);

        verify(mockPetListRepository).removePet(pet);
        verify(mockServer).getPlayer(ownerUUID);
        verify(mockOwner).sendMessage(ChatColor.RED + "Rex has died.");
    }

    @Test
    public void testPetDeathWithOfflineOwnerStillRemovesPet() {
        when(mockServer.getPlayer(ownerUUID)).thenReturn(null);

        handler.handle(mockEvent);

        verify(mockPetListRepository).removePet(pet);
        verifyNoInteractions(mockOwner);
    }

    @Test
    public void testNonPetDeathIsIgnored() {
        when(mockPetListRepository.getPet(mockEntity)).thenReturn(null);

        handler.handle(mockEvent);

        verify(mockPetListRepository, never()).removePet(any());
        verifyNoInteractions(mockServerProvider);
    }
}
