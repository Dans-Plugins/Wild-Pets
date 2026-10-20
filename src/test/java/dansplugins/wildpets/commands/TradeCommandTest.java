package dansplugins.wildpets.commands;

import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.exceptions.PetRecordNotFoundException;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.record.PetRecord;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
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

public class TradeCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private Player mockTargetPlayer;

    @Mock
    private CommandSender mockConsoleSender;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    @Mock
    private PetListRepository mockPetListRepository;

    @Mock
    private PetRecordRepository mockPetRecordRepository;

    private UUID playerUUID;
    private UUID targetUUID;
    private Pet pet;
    private PetRecord petRecord;
    private EphemeralData ephemeralData;
    private TradeCommand tradeCommand;

    @Before
    public void setup() throws PetRecordNotFoundException {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        targetUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("Daniel");
        when(mockTargetPlayer.getUniqueId()).thenReturn(targetUUID);
        when(mockTargetPlayer.getName()).thenReturn("Alex");
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getPlayerExact("Alex")).thenReturn(mockTargetPlayer);
        pet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        petRecord = new PetRecord(pet);
        when(mockPetListRepository.transferPet(pet, targetUUID)).thenReturn(true);
        when(mockPetRecordRepository.getOrCreatePetRecord(pet)).thenReturn(petRecord);
        ephemeralData = new EphemeralData();
        tradeCommand = new TradeCommand(ephemeralData, mockPetListRepository, mockPetRecordRepository, mockServerProvider);
    }

    private List<String> captureMessagesSentTo(Player player) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(player, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersTradeSubcommandAndPermission() {
        assertEquals(1, tradeCommand.getNames().size());
        assertTrue(tradeCommand.getNames().contains("trade"));
        assertEquals(1, tradeCommand.getPermissions().size());
        assertTrue(tradeCommand.getPermissions().contains("wp.trade"));
    }

    @Test
    public void testExecuteTransfersSelectedPetToOnlineTarget() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(tradeCommand.execute(mockPlayer, new String[]{"Alex"}));

        verify(mockServer).getPlayerExact("Alex");
        verify(mockPetListRepository).transferPet(pet, targetUUID);
        assertEquals(targetUUID, petRecord.getOwnerUUID());
        assertNull(ephemeralData.getPetSelectionForPlayer(playerUUID));
        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("Rex has been traded to Alex.")));
        assertTrue(captureMessagesSentTo(mockTargetPlayer).stream()
                .anyMatch(message -> message.contains("Daniel has traded Rex to you.")));
    }

    @Test
    public void testExecuteWithoutArgumentsSendsUsage() {
        assertFalse(tradeCommand.execute(mockPlayer));

        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("Usage: /wp trade (playerName)")));
    }

    @Test
    public void testExecuteWithEmptyArgumentsSendsUsage() {
        assertFalse(tradeCommand.execute(mockPlayer, new String[]{}));

        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("Usage: /wp trade (playerName)")));
        verifyNoInteractions(mockPetListRepository);
    }

    @Test
    public void testExecuteWithNoSelectionSendsErrorAndReturnsFalse() {
        assertFalse(tradeCommand.execute(mockPlayer, new String[]{"Alex"}));

        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("No pet selected. Use /wp select first.")));
        verifyNoInteractions(mockPetListRepository);
    }

    @Test
    public void testExecuteRejectsPetOwnedBySomeoneElseAndClearsSelection() {
        Pet otherPet = new Pet(UUID.randomUUID(), UUID.randomUUID(), "Steve", mockServerProvider);
        ephemeralData.selectPetForPlayer(otherPet, playerUUID);

        assertFalse(tradeCommand.execute(mockPlayer, new String[]{"Alex"}));

        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("You don't own that pet.")));
        assertNull(ephemeralData.getPetSelectionForPlayer(playerUUID));
        verifyNoInteractions(mockPetListRepository);
    }

    @Test
    public void testExecuteRejectsOfflineTarget() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertFalse(tradeCommand.execute(mockPlayer, new String[]{"Nobody"}));

        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("That player is not online.")));
        assertSame(pet, ephemeralData.getPetSelectionForPlayer(playerUUID));
        verifyNoInteractions(mockPetListRepository);
    }

    @Test
    public void testExecuteRejectsTradeToSelf() {
        when(mockServer.getPlayerExact("Daniel")).thenReturn(mockPlayer);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertFalse(tradeCommand.execute(mockPlayer, new String[]{"Daniel"}));

        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("You cannot trade a pet to yourself.")));
        verifyNoInteractions(mockPetListRepository);
    }

    @Test
    public void testExecuteReportsFailedTransferAndKeepsSelection() {
        when(mockPetListRepository.transferPet(pet, targetUUID)).thenReturn(false);
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertFalse(tradeCommand.execute(mockPlayer, new String[]{"Alex"}));

        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("Failed to transfer pet.")));
        assertSame(pet, ephemeralData.getPetSelectionForPlayer(playerUUID));
        assertEquals(playerUUID, petRecord.getOwnerUUID());
        verify(mockTargetPlayer, never()).sendMessage(anyString());
    }

    @Test
    public void testExecuteReportsMissingPetRecord() throws PetRecordNotFoundException {
        when(mockPetRecordRepository.getOrCreatePetRecord(pet)).thenThrow(new PetRecordNotFoundException("missing"));
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertFalse(tradeCommand.execute(mockPlayer, new String[]{"Alex"}));

        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("Pet record wasn't found")));
        verify(mockTargetPlayer, never()).sendMessage(anyString());
    }

    @Test
    public void testExecuteRejectsNonPlayerSender() {
        assertFalse(tradeCommand.execute(mockConsoleSender, new String[]{"Alex"}));

        verify(mockConsoleSender).sendMessage("Only players can use this command.");
    }
}
