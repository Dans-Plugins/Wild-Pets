package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import dansplugins.wildpets.exceptions.PetRecordNotFoundException;
import org.bukkit.ChatColor;
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

public class RenameCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private Entity mockEntity;

    private UUID playerUUID;
    private Pet pet;
    private EphemeralData ephemeralData;
    private PetListRepository petListRepository;
    private PetRecordRepository petRecordRepository;
    private RenameCommand renameCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockConfigService.getInt("petNameCharacterLimit")).thenReturn(20);
        pet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        ephemeralData = new EphemeralData();
        petListRepository = new PetListRepository(mockConfigService, mockServerProvider);
        petListRepository.addExistingPet(pet);
        petRecordRepository = new PetRecordRepository();
        renameCommand = new RenameCommand(ephemeralData, petListRepository, petRecordRepository, mockConfigService, mockServerProvider);
    }

    private List<String> captureMessagesSentToPlayer() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockPlayer, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    private void assertRejectedWith(String expectedMessage, String newName) {
        assertFalse(renameCommand.execute(mockPlayer, new String[]{newName}));

        assertEquals("Rex", pet.getName());
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains(expectedMessage)));
    }

    @Test
    public void testRegistersRenameSubcommandAndPermission() {
        assertEquals(1, renameCommand.getNames().size());
        assertTrue(renameCommand.getNames().contains("rename"));
        assertEquals(1, renameCommand.getPermissions().size());
        assertTrue(renameCommand.getPermissions().contains("wp.rename"));
    }

    @Test
    public void testExecuteWithoutArgumentsSendsUsage() {
        assertFalse(renameCommand.execute(mockPlayer));

        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Usage: /wp rename (new name)")));
    }

    @Test
    public void testExecuteWithEmptyArgumentsSendsUsage() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertFalse(renameCommand.execute(mockPlayer, new String[]{}));

        assertEquals("Rex", pet.getName());
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Usage: /wp rename (new name)")));
    }

    @Test
    public void testExecuteRejectsWhenNoPetSelected() {
        assertRejectedWith("No pet selected.", "Max");
    }

    @Test
    public void testExecuteRejectsNameAlreadyTakenByAnotherPet() {
        Pet otherPet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        otherPet.setName("Max");
        petListRepository.addExistingPet(otherPet);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertRejectedWith("That name is already taken by one of your pets.", "Max");
    }

    @Test
    public void testExecuteTreatsNameCollisionCaseInsensitively() {
        Pet otherPet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        otherPet.setName("Max");
        petListRepository.addExistingPet(otherPet);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertRejectedWith("That name is already taken by one of your pets.", "mAX");
    }

    @Test
    public void testExecuteRejectsSelectedPetsOwnCurrentName() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertRejectedWith("That name is already taken by one of your pets.", "REX");
    }

    @Test
    public void testExecuteRejectsReservedNameCancel() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertRejectedWith("You can't name your pet 'cancel'.", "CaNcEl");
    }

    @Test
    public void testExecuteRejectsNameOverCharacterLimit() {
        when(mockConfigService.getInt("petNameCharacterLimit")).thenReturn(5);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertRejectedWith("Your pet's name can't contain more than 5 characters.", "Sixsix");
    }

    @Test
    public void testExecuteRejectionLeavesPetRecordUntouched() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertFalse(renameCommand.execute(mockPlayer, new String[]{"cancel"}));

        assertTrue(petRecordRepository.getPetRecords().isEmpty());
    }

    @Test
    public void testExecuteRenamesPetEntityAndExistingRecord() throws PetRecordNotFoundException {
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(mockEntity);
        petRecordRepository.addPetRecord(pet);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(renameCommand.execute(mockPlayer, new String[]{"Max"}));

        assertEquals("Max", pet.getName());
        verify(mockEntity).setCustomName(ChatColor.GREEN + "Max");
        assertEquals(1, petRecordRepository.getPetRecords().size());
        assertEquals("Max", petRecordRepository.getPetRecord(pet.getUniqueID()).getName());
        assertTrue(captureMessagesSentToPlayer().stream()
                .anyMatch(message -> message.contains("Renamed.")));
    }

    @Test
    public void testExecuteRecreatesMissingPetRecordWithNewName() throws PetRecordNotFoundException {
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(mockEntity);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(renameCommand.execute(mockPlayer, new String[]{"Max"}));

        assertEquals(1, petRecordRepository.getPetRecords().size());
        assertEquals("Max", petRecordRepository.getPetRecord(pet.getUniqueID()).getName());
    }

    @Test
    public void testExecuteRenamesPetWhenEntityIsNotLoaded() throws PetRecordNotFoundException {
        when(mockServer.getEntity(pet.getUniqueID())).thenReturn(null);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(renameCommand.execute(mockPlayer, new String[]{"Max"}));

        assertEquals("Max", pet.getName());
        assertEquals("Max", petRecordRepository.getPetRecord(pet.getUniqueID()).getName());
    }
}
