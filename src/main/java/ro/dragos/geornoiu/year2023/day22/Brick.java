package ro.dragos.geornoiu.year2023.day22;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Brick {
  private final Coordinate start;
  private final Coordinate end;
  private final Set<Brick> supportedBricks = new HashSet<>();
  private final Set<Brick> supportingBricks = new HashSet<>();

  public Brick(Coordinate start, Coordinate end) {
    this.start = start;
    this.end = end;
  }

  public static Brick of(String input) {
    String[] parts = input.split("~");
    String[] startCoordinates = parts[0].split(",");
    Coordinate start =
        new Coordinate(
            Integer.parseInt(startCoordinates[0]),
            Integer.parseInt(startCoordinates[1]),
            Integer.parseInt(startCoordinates[2]));
    String[] endCoordinatse = parts[1].split(",");
    Coordinate end =
        new Coordinate(
            Integer.parseInt(endCoordinatse[0]),
            Integer.parseInt(endCoordinatse[1]),
            Integer.parseInt(endCoordinatse[2]));
    return new Brick(start, end);
  }

  public Brick moveDown() {
    return new Brick(start.addToZ(-1), end.addToZ(-1));
  }

  public int getLength() {
    return Math.abs(start.x() - end.x())
        + Math.abs(start.y() - end.y())
        + Math.abs(start.z() - end.z());
  }

  public List<Coordinate> getAllCubes() {
    List<Coordinate> cubes = new ArrayList<>();
    for (int x = start.x(); x <= end.x(); x++) {
      for (int y = start.y(); y <= end.y(); y++) {
        for (int z = start.z(); z <= end.z(); z++) {
          cubes.add(new Coordinate(x, y, z));
        }
      }
    }
    return cubes;
  }

  public List<Coordinate> getMaxZCoordinates() {
    List<Coordinate> cubes = new ArrayList<>();
    int z = getMaxHeight();
    for (int x = start.x(); x <= end.x(); x++) {
      for (int y = start.y(); y <= end.y(); y++) {
        cubes.add(new Coordinate(x, y, z));
      }
    }
    return cubes;
  }

  public int getMaxHeight() {
    return Math.max(start.z(), end.z());
  }

  public int getMinHeight() {
    return Math.min(start.z(), end.z());
  }

  public Set<Brick> getSupportedBricks() {
    return supportedBricks;
  }

  public Set<Brick> getSupportingBricks() {
    return supportingBricks;
  }
}
