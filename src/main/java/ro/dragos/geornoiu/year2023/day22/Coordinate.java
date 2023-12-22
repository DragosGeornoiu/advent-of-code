package ro.dragos.geornoiu.year2023.day22;

public record Coordinate(int x, int y, int z) {
  public Coordinate addToZ(int z) {
    return new Coordinate(x, y, this.z + z);
  }
}
