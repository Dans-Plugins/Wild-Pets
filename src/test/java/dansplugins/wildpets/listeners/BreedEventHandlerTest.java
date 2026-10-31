package dansplugins.wildpets.listeners;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetList;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import org.bukkit.ChatColor;
import org.bukkit.entity.Cow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityBreedEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BreedEventHandlerTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private PetListRepository mockPetListRepository;

    @Mock
    private PetRecordRepository mockPetRecordRepository;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private EphemeralData mockEphemeralData;

    @Mock
    private PetList mockPetList;

    @Mock
    private EntityBreedEvent mockEvent;

    @Mock
    private Cow mockChild;

    @Mock
    private Cow mockFather;

    @Mock
    private Cow mockMother;

    private UUID playerUUID;
    private UUID childUUID;
    private UUID fatherUUID;
    private UUID motherUUID;
    private Pet fatherPet;
    private Pet motherPet;
    private Pet childPet;
    private BreedEventHandler handler;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        childUUID = UUID.randomUUID();
        fatherUUID = UUID.randomUUID();
        motherUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockChild.getUniqueId()).thenReturn(childUUID);
        when(mockFather.getUniqueId()).thenReturn(fatherUUID);
        when(mockMother.getUniqueId()).thenReturn(motherUUID);
        when(mockEvent.getBreeder()).thenReturn(mockPlayer);
        when(mockEvent.getEntity()).thenReturn(mockChild);
        when(mockEvent.getFather()).thenReturn(mockFather);
        when(mockEvent.getMother()).thenReturn(mockMother);

        fatherPet = new Pet(fatherUUID, playerUUID, "Daniel", mockServerProvider);
        motherPet = new Pet(motherUUID, playerUUID, "Daniel", mockServerProvider);
        childPet = new Pet(childUUID, playerUUID, "Daniel", mockServerProvider);
        childPet.setName("Calf");
        when(mockPetListRepository.getPet(mockFather)).thenReturn(fatherPet);
        when(mockPetListRepository.getPet(mockMother)).thenReturn(motherPet);
        when(mockPetListRepository.getPet(mockChild)).thenReturn(childPet);
        when(mockConfigService.getBoolean("bornPetsEnabled")).thenReturn(true);
        when(mockConfigService.getInt("petLimit")).thenReturn(10);
        when(mockPetList.getNumPets()).thenReturn(2);
        when(mockPetListRepository.getPetList(playerUUID)).thenReturn(mockPetList);

        handler = new BreedEventHandler(mockPetListRepository, mockPetRecordRepository, mockConfigService, mockEphemeralData);
    }

    @Test
    public void testNonPlayerBreederIsIgnored() {
        when(mockEvent.getBreeder()).thenReturn(mock(LivingEntity.class));

        handler.handle(mockEvent);

        verifyNoInteractions(mockPetListRepository, mockPetRecordRepository, mockConfigService, mockEphemeralData);
    }

    @Test
    public void testNullBreederIsIgnored() {
        when(mockEvent.getBreeder()).thenReturn(null);

        handler.handle(mockEvent);

        verifyNoInteractions(mockPetListRepository, mockPetRecordRepository, mockConfigService, mockEphemeralData);
    }

    @Test
    public void testBornPetIsTamedSelectedAndLinkedToBothPetParents() {
        handler.handle(mockEvent);

        assertTrue(fatherPet.getChildUUIDs().contains(childUUID));
        assertTrue(motherPet.getChildUUIDs().contains(childUUID));
        verify(mockPetListRepository).addNewPet(mockPlayer, mockChild);
        verify(mockPetRecordRepository).addPetRecord(childPet);
        verify(mockEphemeralData).selectPetForPlayer(childPet, playerUUID);
        verify(mockPlayer).sendMessage(contains(ChatColor.GREEN + "You have a new pet named Calf and it is now your selected pet."));
        assertTrue(childPet.getParentUUIDs().contains(fatherUUID));
        assertTrue(childPet.getParentUUIDs().contains(motherUUID));
    }

    @Test
    public void testBornPetsDisabledStillRecordsChildOnParentsButDoesNotTame() {
        when(mockConfigService.getBoolean("bornPetsEnabled")).thenReturn(false);

        handler.handle(mockEvent);

        assertTrue(fatherPet.getChildUUIDs().contains(childUUID));
        assertTrue(motherPet.getChildUUIDs().contains(childUUID));
        verify(mockPetListRepository, never()).addNewPet(any(), any());
        verifyNoInteractions(mockPetRecordRepository, mockEphemeralData);
        verify(mockPlayer, never()).sendMessage(anyString());
        assertTrue(childPet.getParentUUIDs().isEmpty());
    }

    @Test
    public void testOffspringOfTwoNonPetsIsNotTamed() {
        when(mockPetListRepository.getPet(mockFather)).thenReturn(null);
        when(mockPetListRepository.getPet(mockMother)).thenReturn(null);

        handler.handle(mockEvent);

        verify(mockPetListRepository, never()).addNewPet(any(), any());
        verifyNoInteractions(mockPetRecordRepository, mockEphemeralData);
        verify(mockPlayer, never()).sendMessage(anyString());
    }

    @Test
    public void testSinglePetParentIsEnoughToTameOffspringAndBothParentsAreLinked() {
        when(mockPetListRepository.getPet(mockMother)).thenReturn(null);

        handler.handle(mockEvent);

        assertTrue(fatherPet.getChildUUIDs().contains(childUUID));
        assertFalse(motherPet.getChildUUIDs().contains(childUUID));
        verify(mockPetListRepository).addNewPet(mockPlayer, mockChild);
        verify(mockEphemeralData).selectPetForPlayer(childPet, playerUUID);
        assertTrue(childPet.getParentUUIDs().contains(fatherUUID));
        assertTrue(childPet.getParentUUIDs().contains(motherUUID));
    }

    @Test
    public void testBreederAtPetLimitDoesNotTameOffspringButChildIsStillRecordedOnParents() {
        when(mockPetList.getNumPets()).thenReturn(10);

        handler.handle(mockEvent);

        assertTrue(fatherPet.getChildUUIDs().contains(childUUID));
        assertTrue(motherPet.getChildUUIDs().contains(childUUID));
        verify(mockPetListRepository, never()).addNewPet(any(), any());
        verifyNoInteractions(mockPetRecordRepository, mockEphemeralData);
        verify(mockPlayer).sendMessage(ChatColor.RED + "You have reached your pet limit, so the newborn was not added to your pets.");
        assertTrue(childPet.getParentUUIDs().isEmpty());
    }

    @Test
    public void testBreederOneBelowPetLimitStillTamesOffspring() {
        when(mockPetList.getNumPets()).thenReturn(9);

        handler.handle(mockEvent);

        verify(mockPetListRepository).addNewPet(mockPlayer, mockChild);
        verify(mockEphemeralData).selectPetForPlayer(childPet, playerUUID);
    }
}
