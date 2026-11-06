package dansplugins.wildpets.listeners;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Covers the EntityDeathEvent handler and the two damage handlers. WildPets is final and cannot be
 * mocked, so the handler is built with a debug flag instead; the debug-only logging is not exercised
 * here.
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

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private EntityDamageEvent mockDamageEvent;

    @Mock
    private EntityDamageByEntityEvent mockDamageByEntityEvent;

    @Mock
    private Entity mockDamager;

    @Mock
    private Arrow mockArrow;

    @Mock
    private LivingEntity mockShooter;

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
        when(mockDamageEvent.getEntity()).thenReturn(mockEntity);
        when(mockDamageByEntityEvent.getEntity()).thenReturn(mockEntity);
        when(mockDamageByEntityEvent.getDamager()).thenReturn(mockDamager);
        handler = new DamageEffectsAndDeathHandler(mockConfigService, mockPetListRepository, () -> false, mockServerProvider);
    }

    private void setDamageOptions(boolean damageToPetsEnabled, boolean damageFromPetsEnabled) {
        when(mockConfigService.getBoolean("damageToPetsEnabled")).thenReturn(damageToPetsEnabled);
        when(mockConfigService.getBoolean("damageFromPetsEnabled")).thenReturn(damageFromPetsEnabled);
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

    @Test
    public void testPetIsProtectedFromDamageByDefault() {
        setDamageOptions(false, false);

        handler.handle(mockDamageEvent);

        verify(mockDamageEvent).setCancelled(true);
    }

    @Test
    public void testPetTakesDamageWhenDamageToPetsEnabledAndDamageFromPetsDisabled() {
        // issue #391: damageFromPetsEnabled must not decide whether a pet can be hurt
        setDamageOptions(true, false);

        handler.handle(mockDamageEvent);

        verify(mockDamageEvent, never()).setCancelled(true);
    }

    @Test
    public void testPetTakesDamageWhenBothDamageOptionsEnabled() {
        setDamageOptions(true, true);

        handler.handle(mockDamageEvent);

        verify(mockDamageEvent, never()).setCancelled(true);
    }

    @Test
    public void testNonPetDamageIsNotCancelled() {
        setDamageOptions(false, false);
        when(mockPetListRepository.getPet(mockEntity)).thenReturn(null);

        handler.handle(mockDamageEvent);

        verify(mockDamageEvent, never()).setCancelled(true);
    }

    @Test
    public void testAttackOnPetIsNotCancelledWhenDamageToPetsEnabled() {
        setDamageOptions(true, false);

        handler.handle(mockDamageByEntityEvent);

        verify(mockDamageByEntityEvent, never()).setCancelled(true);
    }

    @Test
    public void testAttackOnPetIsCancelledWhenDamageToPetsDisabled() {
        setDamageOptions(false, true);

        handler.handle(mockDamageByEntityEvent);

        verify(mockDamageByEntityEvent).setCancelled(true);
    }

    @Test
    public void testAttackByPetIsCancelledWhenDamageFromPetsDisabled() {
        setDamageOptions(true, false);
        when(mockPetListRepository.getPet(mockEntity)).thenReturn(null);
        when(mockPetListRepository.getPet(mockDamager)).thenReturn(pet);

        handler.handle(mockDamageByEntityEvent);

        verify(mockDamageByEntityEvent).setCancelled(true);
    }

    @Test
    public void testAttackByPetIsNotCancelledWhenDamageFromPetsEnabled() {
        setDamageOptions(true, true);
        when(mockPetListRepository.getPet(mockEntity)).thenReturn(null);
        when(mockPetListRepository.getPet(mockDamager)).thenReturn(pet);

        handler.handle(mockDamageByEntityEvent);

        verify(mockDamageByEntityEvent, never()).setCancelled(true);
    }

    @Test
    public void testProjectileFromPetIsCancelledWhenDamageFromPetsDisabled() {
        setDamageOptions(true, false);
        when(mockPetListRepository.getPet(mockEntity)).thenReturn(null);
        when(mockPetListRepository.getPet(mockShooter)).thenReturn(pet);
        when(mockDamageByEntityEvent.getDamager()).thenReturn(mockArrow);
        when(mockArrow.getShooter()).thenReturn(mockShooter);

        handler.handle(mockDamageByEntityEvent);

        verify(mockDamageByEntityEvent).setCancelled(true);
    }
}
