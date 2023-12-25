package ro.dragos.geornoiu.year2023.day24;

import java.util.List;

public record Point(long x, long y) {

    public Point add(Point delta) {
        return new Point(x + delta.x, y + delta.y);
    }

    public String toString() {
        return "Point[x=" + x + ",y=" + y + "]";
    }
}