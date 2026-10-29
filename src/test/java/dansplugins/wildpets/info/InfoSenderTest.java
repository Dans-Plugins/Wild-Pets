package dansplugins.wildpets.info;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
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

public class InfoSenderTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private Player mockOwner;

    @Mock
    private OfflinePlayer mockOfflineOwner;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    private UUID ownerUUID;
    private Pet pet;
    private PetRecordRepository petRecordRepository;
    private InfoSender infoSender;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ownerUUID = UUID.randomUUID();
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getPlayer(ownerUUID)).thenReturn(mockOwner);
        when(mockOwner.getName()).thenReturn("Daniel");
        pet = new Pet(UUID.randomUUID(), ownerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        pet.setAssignedID(7);
        petRecordRepository = new PetRecordRepository();
        infoSender = new InfoSender(mockConfigService, petRecordRepository, mockServerProvider);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    private boolean playerWasSent(String expectedMessage) {
        return captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains(expectedMessage));
    }

    private Pet petNamed(String name) {
        Pet relative = new Pet(UUID.randomUUID(), ownerUUID, "Daniel", mockServerProvider);
        relative.setName(name);
        return relative;
    }

    @Test
    public void testSendInfoShowsNameOwnerStateAndLockStatus() {
        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertTrue(playerWasSent("Pet Info"));
        assertTrue(playerWasSent("Name:     " + ChatColor.WHITE + "Rex"));
        assertTrue(playerWasSent("Owner:    " + ChatColor.WHITE + "Daniel"));
        assertTrue(playerWasSent("State:    " + ChatColor.WHITE + "Wandering"));
        assertTrue(playerWasSent("Locked:   " + ChatColor.GREEN + "false"));
    }

    @Test
    public void testSendInfoShowsLockedPetAsTrue() {
        pet.setLocked(true);

        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertTrue(playerWasSent("Locked:   " + ChatColor.RED + "true"));
    }

    @Test
    public void testSendInfoResolvesOfflineOwnerName() {
        when(mockServer.getPlayer(ownerUUID)).thenReturn(null);
        when(mockServer.getOfflinePlayer(ownerUUID)).thenReturn(mockOfflineOwner);
        when(mockOfflineOwner.getName()).thenReturn("Alex");
        when(mockOfflineOwner.hasPlayedBefore()).thenReturn(true);

        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertTrue(playerWasSent("Owner:    " + ChatColor.WHITE + "Alex"));
    }

    @Test
    public void testSendInfoShowsUnknownWhenOwnerNameCannotBeResolved() {
        when(mockServer.getPlayer(ownerUUID)).thenReturn(null);
        when(mockServer.getOfflinePlayer(ownerUUID)).thenReturn(mockOfflineOwner);
        when(mockOfflineOwner.getName()).thenReturn(null);

        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertTrue(playerWasSent("Owner:    " + ChatColor.WHITE + "Unknown"));
        assertFalse(playerWasSent("null"));
    }

    @Test
    public void testSendInfoShowsUnknownWhenOfflineOwnerHasNeverPlayed() {
        when(mockServer.getPlayer(ownerUUID)).thenReturn(null);
        when(mockServer.getOfflinePlayer(ownerUUID)).thenReturn(mockOfflineOwner);
        when(mockOfflineOwner.getName()).thenReturn("Alex");
        when(mockOfflineOwner.hasPlayedBefore()).thenReturn(false);

        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertTrue(playerWasSent("Owner:    " + ChatColor.WHITE + "Unknown"));
    }

    @Test
    public void testSendInfoHidesLineageWhenShowLineageInfoDisabled() {
        Pet parent = petNamed("Mother");
        petRecordRepository.addPetRecord(parent);
        pet.addParent(parent.getUniqueID());
        pet.addChild(UUID.randomUUID());

        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertFalse(playerWasSent("Parents:"));
        assertFalse(playerWasSent("Children:"));
    }

    @Test
    public void testSendInfoShowsLineageNamesWhenShowLineageInfoEnabled() {
        when(mockConfigService.getBoolean("showLineageInfo")).thenReturn(true);
        Pet parent = petNamed("Mother");
        Pet child = petNamed("Pup");
        petRecordRepository.addPetRecord(parent);
        petRecordRepository.addPetRecord(child);
        pet.addParent(parent.getUniqueID());
        pet.addChild(child.getUniqueID());

        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertTrue(playerWasSent("Parents:  " + ChatColor.WHITE + "Mother"));
        assertTrue(playerWasSent("Children: " + ChatColor.WHITE + "Pup"));
    }

    @Test
    public void testSendInfoOmitsEmptyLineageLinesWhenShowLineageInfoEnabled() {
        when(mockConfigService.getBoolean("showLineageInfo")).thenReturn(true);

        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertFalse(playerWasSent("Parents:"));
        assertFalse(playerWasSent("Children:"));
    }

    @Test
    public void testSendInfoHidesDebugLinesWhenDebugModeDisabled() {
        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertFalse(playerWasSent("[DEBUG]"));
    }

    @Test
    public void testSendInfoShowsDebugLinesWhenDebugModeEnabled() {
        when(mockConfigService.getBoolean("debugMode")).thenReturn(true);
        UUID parentUUID = UUID.randomUUID();
        UUID childUUID = UUID.randomUUID();
        pet.addParent(parentUUID);
        pet.addChild(childUUID);

        infoSender.sendInfoToPlayer(mockPlayer, pet);

        assertTrue(playerWasSent("[DEBUG] uniqueID:   " + ChatColor.WHITE + pet.getUniqueID()));
        assertTrue(playerWasSent("[DEBUG] ownerUUID:  " + ChatColor.WHITE + ownerUUID));
        assertTrue(playerWasSent("[DEBUG] assignedID: " + ChatColor.WHITE + "7"));
        assertTrue(playerWasSent("[DEBUG] Parents:    " + ChatColor.WHITE + parentUUID));
        assertTrue(playerWasSent("[DEBUG] Children:   " + ChatColor.WHITE + childUUID));
    }

    @Test
    public void testGetParentNamesReturnsEmptyStringWithNoParents() {
        assertEquals("", infoSender.getParentNamesSeparatedByCommas(pet));
    }

    @Test
    public void testGetParentNamesSeparatesKnownParentsWithCommas() {
        Pet mother = petNamed("Mother");
        Pet father = petNamed("Father");
        petRecordRepository.addPetRecord(mother);
        petRecordRepository.addPetRecord(father);
        pet.addParent(mother.getUniqueID());
        pet.addParent(father.getUniqueID());

        String names = infoSender.getParentNamesSeparatedByCommas(pet);

        assertTrue(names.equals("Mother, Father") || names.equals("Father, Mother"));
    }

    @Test
    public void testGetParentNamesShowsUnknownForMissingRecord() {
        Pet mother = petNamed("Mother");
        petRecordRepository.addPetRecord(mother);
        pet.addParent(mother.getUniqueID());
        pet.addParent(UUID.randomUUID());

        String names = infoSender.getParentNamesSeparatedByCommas(pet);

        assertTrue(names.equals("Mother, Unknown") || names.equals("Unknown, Mother"));
    }

    @Test
    public void testGetChildrenNamesReturnsEmptyStringWithNoChildren() {
        assertEquals("", infoSender.getChildrenNamesSeparatedByCommas(pet));
    }

    @Test
    public void testGetChildrenNamesSeparatesKnownChildrenWithCommas() {
        Pet pup = petNamed("Pup");
        Pet kit = petNamed("Kit");
        petRecordRepository.addPetRecord(pup);
        petRecordRepository.addPetRecord(kit);
        pet.addChild(pup.getUniqueID());
        pet.addChild(kit.getUniqueID());

        String names = infoSender.getChildrenNamesSeparatedByCommas(pet);

        assertTrue(names.equals("Pup, Kit") || names.equals("Kit, Pup"));
    }

    @Test
    public void testGetChildrenNamesShowsUnknownForMissingRecord() {
        pet.addChild(UUID.randomUUID());

        assertEquals("Unknown", infoSender.getChildrenNamesSeparatedByCommas(pet));
    }
}
