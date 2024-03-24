package dansplugins.wildpets.location;

import org.junit.Test;

public class WpLocationTest {

    @Test
    public void testInitialization() {
        WpLocation location = new WpLocation(0, 0, 0);
        assert(location.getX() == 0);
        assert(location.getY() == 0);
        assert(location.getZ() == 0);
    }

    @Test
    public void testSetters() {
        WpLocation location = new WpLocation(0, 0, 0);
        location.setX(1);
        location.setY(2);
        location.setZ(3);
        assert(location.getX() == 1);
        assert(location.getY() == 2);
        assert(location.getZ() == 3);
    }

    @Test
    public void testEquals() {
        WpLocation location1 = new WpLocation(0, 0, 0);
        WpLocation location2 = new WpLocation(0, 0, 0);
        WpLocation location3 = new WpLocation(1, 2, 3);
        assert(location1.equals(location2));
        assert(!location1.equals(location3));
    }
}
