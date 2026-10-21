package dansplugins.wildpets.commands;

import dansplugins.wildpets.helpers.ServerProvider;
import dansplugins.wildpets.pet.list.PetList;
import dansplugins.wildpets.pet.list.PetListRepository;
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

public class ListCommandTest {
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
    private PetList mockTargetPetList;

    private ListCommand listCommand;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        UUID targetUUID = UUID.randomUUID();
        when(mockTargetPlayer.getUniqueId()).thenReturn(targetUUID);
        when(mockServerProvider.get()).thenReturn(mockServer);
        when(mockServer.getPlayer("Alex")).thenReturn(mockTargetPlayer);
        when(mockPetListRepository.getPetList(targetUUID)).thenReturn(mockTargetPetList);
        listCommand = new ListCommand(mockPetListRepository, mockServerProvider);
    }

    private List<String> captureMessagesSentTo(CommandSender sender) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(sender, atLeastOnce()).sendMessage(captor.capture());
        return captor.getAllValues();
    }

    @Test
    public void testRegistersListSubcommandAndPermission() {
        assertEquals(1, listCommand.getNames().size());
        assertTrue(listCommand.getNames().contains("list"));
        assertEquals(1, listCommand.getPermissions().size());
        assertTrue(listCommand.getPermissions().contains("wp.list"));
    }

    @Test
    public void testExecuteSendsOwnListToPlayer() {
        assertTrue(listCommand.execute(mockPlayer));

        verify(mockPetListRepository).sendListOfPetsToPlayer(mockPlayer);
    }

    @Test
    public void testExecuteFromConsoleIsRejected() {
        assertFalse(listCommand.execute(mockConsoleSender));

        assertTrue(captureMessagesSentTo(mockConsoleSender).contains("Only players can use this command."));
        verifyNoInteractions(mockPetListRepository);
    }

    @Test
    public void testExecuteWithArgsFromConsoleIsRejected() {
        assertFalse(listCommand.execute(mockConsoleSender, new String[]{"Alex"}));

        assertTrue(captureMessagesSentTo(mockConsoleSender).contains("Only players can use this command."));
        verifyNoInteractions(mockPetListRepository, mockServerProvider);
    }

    @Test
    public void testExecuteWithArgsWithoutOthersPermissionIsRejected() {
        when(mockPlayer.hasPermission("wp.list.others")).thenReturn(false);

        assertFalse(listCommand.execute(mockPlayer, new String[]{"Alex"}));

        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("you need the following permission: 'wp.list.others'")));
        verifyNoInteractions(mockPetListRepository, mockServerProvider);
    }

    @Test
    public void testExecuteWithArgsForUnknownPlayerSendsNotFound() {
        when(mockPlayer.hasPermission("wp.list.others")).thenReturn(true);

        assertFalse(listCommand.execute(mockPlayer, new String[]{"Nobody"}));

        verify(mockServer).getPlayer("Nobody");
        assertTrue(captureMessagesSentTo(mockPlayer).stream()
                .anyMatch(message -> message.contains("That player wasn't found.")));
        verifyNoInteractions(mockPetListRepository);
    }

    @Test
    public void testExecuteWithArgsSendsTargetsListToPlayer() {
        when(mockPlayer.hasPermission("wp.list.others")).thenReturn(true);

        assertTrue(listCommand.execute(mockPlayer, new String[]{"Alex"}));

        verify(mockServer).getPlayer("Alex");
        verify(mockTargetPetList).sendListOfPetsToPlayer(mockPlayer);
        verify(mockPetListRepository, never()).sendListOfPetsToPlayer(mockPlayer);
    }
}
