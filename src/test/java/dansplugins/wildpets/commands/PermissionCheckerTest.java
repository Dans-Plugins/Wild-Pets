package dansplugins.wildpets.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PermissionCheckerTest {
    @Mock
    private CommandSender mockSender;

    private PermissionChecker permissionChecker;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        permissionChecker = new PermissionChecker();
    }

    @Test
    public void testCheckSinglePermissionDelegatesToSender() {
        when(mockSender.hasPermission("wp.tame")).thenReturn(true);

        assertTrue(permissionChecker.checkPermission(mockSender, "wp.tame"));
        assertFalse(permissionChecker.checkPermission(mockSender, "wp.config"));
    }

    @Test
    public void testCheckSinglePermissionDoesNotMessageSender() {
        permissionChecker.checkPermission(mockSender, "wp.tame");

        verify(mockSender, never()).sendMessage(anyString());
    }

    @Test
    public void testCheckPermissionListPassesWhenSenderHasAnyPermission() {
        when(mockSender.hasPermission("wp.default")).thenReturn(false);
        when(mockSender.hasPermission("wp.tame")).thenReturn(true);

        assertTrue(permissionChecker.checkPermission(mockSender, new ArrayList<>(Arrays.asList("wp.default", "wp.tame"))));
        verify(mockSender, never()).sendMessage(anyString());
    }

    @Test
    public void testCheckPermissionListStopsAtFirstGrantedPermission() {
        when(mockSender.hasPermission("wp.default")).thenReturn(true);

        assertTrue(permissionChecker.checkPermission(mockSender, new ArrayList<>(Arrays.asList("wp.default", "wp.tame"))));
        verify(mockSender, never()).hasPermission("wp.tame");
    }

    @Test
    public void testCheckPermissionListFailsAndListsRequiredPermissions() {
        assertFalse(permissionChecker.checkPermission(mockSender, new ArrayList<>(Arrays.asList("wp.lock", "wp.unlock"))));

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockSender).sendMessage(captor.capture());
        assertEquals(ChatColor.RED + "In order to use this command, you need one of the following permissions: wp.lock, wp.unlock",
                captor.getValue());
    }

    @Test
    public void testCheckEmptyPermissionListFailsAndMessagesSender() {
        assertFalse(permissionChecker.checkPermission(mockSender, new ArrayList<>()));

        verify(mockSender).sendMessage(ChatColor.RED + "In order to use this command, you need one of the following permissions: ");
    }
}
