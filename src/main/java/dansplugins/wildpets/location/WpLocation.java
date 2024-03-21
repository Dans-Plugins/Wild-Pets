package dansplugins.wildpets.location;

public class WpLocation {
    private int x;
    private int y;
    private int z;

    public WpLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public WpLocation(double x, double y, double z) {
        this.x = (int) x;
        this.y = (int) y;
        this.z = (int) z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof WpLocation)) {
            return false;
        }
        WpLocation location = (WpLocation) obj;
        return location.getX() == x && location.getY() == y && location.getZ() == z;
    }
}
