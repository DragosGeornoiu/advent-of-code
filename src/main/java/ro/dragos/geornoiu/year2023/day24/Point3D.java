package ro.dragos.geornoiu.year2023.day24;

public record Point3D(Long x, Long y, Long z) implements Cloneable {

    public Point3D(int x, int y, int z) {
        this(Long.valueOf(x),Long.valueOf(y), Long.valueOf(z));
    }

    public Point3D add(Point3D delta) {
        return new Point3D(x + delta.x, y + delta.y, z + delta.z);
    }

    public Point3D sub(Point3D delta) {
        return new Point3D(x - delta.x, y - delta.y, z - delta.z);
    }

    @Override
    public String toString() {
        return "Point3D[x=" + x + ",y=" + y + ",z=" + z + "]";
    }

    @Override
    public Point3D clone() {
        try {
            return (Point3D) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}