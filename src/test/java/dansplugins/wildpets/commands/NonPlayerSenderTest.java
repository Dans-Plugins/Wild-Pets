package dansplugins.wildpets.commands;

import dansplugins.wildpets.config.ConfigService;
import dansplugins.wildpets.data.EphemeralData;
import dansplugins.wildpets.pet.list.PetListRepository;
import dansplugins.wildpets.pet.record.PetRecordRepository;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Covers the non-player guard of subcommands that have no dedicated test class.
 */
public class NonPlayerSenderTest {
    private static final String PLAYERS_ONLY = "Only players can use this command.";

    @Mock
    private CommandSender mockConsoleSender;

    @Mock
    private EphemeralData mockEphemeralData;

    @Mock
    private ConfigService mockConfigService;

    @Mock
    private PetListRepository mockPetListRepository;

    @Mock
    private PetRecordRepository mockPetRecordRepository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSetFreeRejectsNonPlayerSender() {
        SetFreeCommand command = new SetFreeCommand(mockEphemeralData, mockPetListRepository);

        assertFalse(command.execute(mockConsoleSender));

        verify(mockConsoleSender).sendMessage(PLAYERS_ONLY);
    }

    @Test
    public void testInfoRejectsNonPlayerSender() {
        InfoCommand command = new InfoCommand(mockEphemeralData, mockConfigService, mockPetRecordRepository);

        assertFalse(command.execute(mockConsoleSender));

        verify(mockConsoleSender).sendMessage(PLAYERS_ONLY);
    }

    @Test
    public void testSelectRejectsNonPlayerSender() {
        SelectCommand command = new SelectCommand(mockConfigService, mockEphemeralData, mockPetListRepository);

        assertFalse(command.execute(mockConsoleSender));
        assertFalse(command.execute(mockConsoleSender, new String[]{"Rex"}));

        verify(mockConsoleSender, times(2)).sendMessage(PLAYERS_ONLY);
    }

    @Test
    public void testLocateRejectsNonPlayerSender() {
        LocateCommand command = new LocateCommand(mockEphemeralData);

        assertFalse(command.execute(mockConsoleSender));

        verify(mockConsoleSender).sendMessage(PLAYERS_ONLY);
    }

    @Test
    public void testRenameRejectsNonPlayerSender() {
        RenameCommand command = new RenameCommand(mockEphemeralData, mockPetListRepository, mockPetRecordRepository, mockConfigService);

        assertFalse(command.execute(mockConsoleSender, new String[]{"Rex"}));

        verify(mockConsoleSender).sendMessage(PLAYERS_ONLY);
    }

    @Test
    public void testCallRejectsNonPlayerSender() {
        CallCommand command = new CallCommand(mockEphemeralData);

        assertFalse(command.execute(mockConsoleSender));

        verify(mockConsoleSender).sendMessage(PLAYERS_ONLY);
    }

    @Test
    public void testListRejectsNonPlayerSender() {
        ListCommand command = new ListCommand(mockPetListRepository);

        assertFalse(command.execute(mockConsoleSender));
        assertFalse(command.execute(mockConsoleSender, new String[]{"Steve"}));

        verify(mockConsoleSender, times(2)).sendMessage(PLAYERS_ONLY);
    }

    @Test
    public void testTradeRejectsNonPlayerSender() {
        TradeCommand command = new TradeCommand(mockEphemeralData, mockPetListRepository, mockPetRecordRepository);

        assertFalse(command.execute(mockConsoleSender, new String[]{"Steve"}));

        verify(mockConsoleSender).sendMessage(PLAYERS_ONLY);
    }
}
