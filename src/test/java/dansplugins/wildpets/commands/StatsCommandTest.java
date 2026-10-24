package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.Pet;
import dansplugins.wildpets.pet.list.PetListRepository;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StatsCommandTest {
    @Mock
    private CommandSender mockSender;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private ServerProvider mockServerProvider;

    @Mock
    private Server mockServer;

    private PetListRepository petListRepository;
    private StatsCommand statsCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(mockServerProvider.get()).thenReturn(mockServer);
        petListRepository = new PetListRepository(mockConfigService, mockServerProvider);
        statsCommand = new StatsCommand(petListRepository);
    }

    private List<String> captureMessagesSentToSender() {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockSender, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    private boolean senderWasSent(String expectedMessage) {
        return captureMessagesSentToSender().stream()
                .anyMatch(message -> message.contains(expectedMessage));
    }

    private void addPet(UUID ownerUUID, String ownerName) {
        petListRepository.addExistingPet(new Pet(UUID.randomUUID(), ownerUUID, ownerName, mockServerProvider));
    }

    @Test
    public void testRegistersStatsSubcommandAndPermission() {
        assertEquals(1, statsCommand.getNames().size());
        assertTrue(statsCommand.getNames().contains("stats"));
        assertEquals(1, statsCommand.getPermissions().size());
        assertTrue(statsCommand.getPermissions().contains("wp.stats"));
    }

    @Test
    public void testReportsZeroPetsWhenNoneExist() {
        assertTrue(statsCommand.execute(mockSender));

        assertTrue(senderWasSent("Statistics"));
        assertTrue(senderWasSent("Number of Pets: "));
        assertTrue(captureMessagesSentToSender().stream()
                .anyMatch(message -> message.matches(".*Number of Pets: \\S*0$")));
    }

    @Test
    public void testCountsPetsAcrossAllOwners() {
        UUID firstOwner = UUID.randomUUID();
        addPet(firstOwner, "Daniel");
        addPet(firstOwner, "Daniel");
        addPet(UUID.randomUUID(), "Stranger");

        assertTrue(statsCommand.execute(mockSender));

        assertTrue(captureMessagesSentToSender().stream()
                .anyMatch(message -> message.matches(".*Number of Pets: \\S*3$")));
    }

    @Test
    public void testArgumentsAreIgnored() {
        addPet(UUID.randomUUID(), "Daniel");

        assertTrue(statsCommand.execute(mockSender, new String[]{"anything", "else"}));

        assertTrue(captureMessagesSentToSender().stream()
                .anyMatch(message -> message.matches(".*Number of Pets: \\S*1$")));
    }
}
