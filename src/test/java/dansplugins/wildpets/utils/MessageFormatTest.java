package dansplugins.wildpets.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class MessageFormatTest {

    @Mock
    private CommandSender mockSender;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHeader() {
        String header = MessageFormat.header("Wild Pets", "Commands");
        assertTrue(header.contains("Wild Pets"));
        assertTrue(header.contains("Commands"));
        assertTrue(header.startsWith(ChatColor.GOLD.toString()));
    }

    @Test
    public void testLine() {
        String line = MessageFormat.line("Some content");
        assertTrue(line.contains("Some content"));
        assertTrue(line.startsWith(ChatColor.GOLD.toString()));
    }

    @Test
    public void testFooter() {
        String footer = MessageFormat.footer();
        assertTrue(footer.startsWith(ChatColor.GOLD.toString()));
        assertEquals(25, footer.chars().filter(c -> c == '─').count());
    }

    @Test
    public void testSendSuccessBox() {
        MessageFormat.sendSuccessBox(mockSender, "Config", "Value set.");

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(mockSender, times(4)).sendMessage(captor.capture());

        assertEquals("", captor.getAllValues().get(0));
        assertTrue(captor.getAllValues().get(1).contains("Config"));
        assertTrue(captor.getAllValues().get(2).contains("Value set."));
        assertEquals(MessageFormat.footer(), captor.getAllValues().get(3));
    }

    @Test
    public void testCreateBar_zeroMax() {
        String bar = MessageFormat.createBar(5, 0);
        assertTrue(bar.contains("[░░░░░░░░░░]"));
    }

    @Test
    public void testCreateBar_halfFilled() {
        String bar = MessageFormat.createBar(5, 10);
        assertTrue(bar.contains("[█████░░░░░]"));
    }

    @Test
    public void testCreateBar_fullyFilled() {
        String bar = MessageFormat.createBar(10, 10);
        assertTrue(bar.contains("[██████████]"));
    }

    @Test
    public void testCreateRankBar_zeroTotal() {
        String bar = MessageFormat.createRankBar(1, 0);
        assertTrue(bar.contains("[░░░░░░░░░░]"));
    }

    @Test
    public void testCreateRankBar_topRankIsFullest() {
        String bar = MessageFormat.createRankBar(1, 10);
        assertTrue(bar.contains("[██████████]"));
    }

    @Test
    public void testCreateRankBar_bottomRankIsSparsest() {
        String bar = MessageFormat.createRankBar(10, 10);
        assertTrue(bar.contains("[█░░░░░░░░░]"));
    }
}
