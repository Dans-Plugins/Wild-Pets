package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import org.bukkit.ChatColor;
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

public class InfoCommandTest {
    @Mock
    private Player mockPlayer;

    @Mock
    private CommandSender mockConsole;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    private UUID playerUUID;
    private Pet pet;
    private EphemeralData ephemeralData;
    private InfoCommand infoCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        playerUUID = UUID.randomUUID();
        when(mockPlayer.getUniqueId()).thenReturn(playerUUID);
        when(mockPlayer.getName()).thenReturn("Daniel");
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getPlayer(playerUUID)).thenReturn(mockPlayer);
        pet = new Pet(UUID.randomUUID(), playerUUID, "Daniel", mockServerProvider);
        pet.setName("Rex");
        ephemeralData = new EphemeralData();
        infoCommand = new InfoCommand(ephemeralData, mockConfigService, new PetRecordRepository(), mockServerProvider);
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

    @Test
    public void testRegistersInfoSubcommandAndPermission() {
        assertEquals(1, infoCommand.getNames().size());
        assertTrue(infoCommand.getNames().contains("info"));
        assertEquals(1, infoCommand.getPermissions().size());
        assertTrue(infoCommand.getPermissions().contains("wp.info"));
    }

    @Test
    public void testInfoSendsSelectedPetsInfo() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(infoCommand.execute(mockPlayer));

        assertTrue(playerWasSent("Pet Info"));
        assertTrue(playerWasSent("Name:     " + ChatColor.WHITE + "Rex"));
        assertTrue(playerWasSent("Owner:    " + ChatColor.WHITE + "Daniel"));
    }

    @Test
    public void testInfoWithArgumentsBehavesLikeNoArguments() {
        ephemeralData.selectPetForPlayer(pet, playerUUID);

        assertTrue(infoCommand.execute(mockPlayer, new String[]{"ignored"}));

        assertTrue(playerWasSent("Name:     " + ChatColor.WHITE + "Rex"));
    }

    @Test
    public void testInfoWithoutSelectionSendsError() {
        assertFalse(infoCommand.execute(mockPlayer));

        assertTrue(playerWasSent("No pet selected."));
        assertFalse(playerWasSent("Pet Info"));
    }

    @Test
    public void testInfoFromNonPlayerIsRejected() {
        assertFalse(infoCommand.execute(mockConsole));

        verify(mockConsole).sendMessage("Only players can use this command.");
    }
}
