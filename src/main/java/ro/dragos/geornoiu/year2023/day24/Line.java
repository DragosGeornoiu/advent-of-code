package ro.dragos.geornoiu.year2023.day24;

public record Line(Point point, Point delta) {

  public Line(Point point, long deltaX, long deltaY) {
    this(point, new Point(deltaX, deltaY));
  }

  public Point intersects(Line line) {
    double line1Slope = (double) delta.y() / delta.x();
    double line2Slope = (double) line.delta.y() / line.delta.x();

    double line1b = point.y() - line1Slope * point.x();
    double line2b = line.point.y() - line2Slope * line.point.x();

    if (line1Slope == line2Slope) {
      return line1b == line2b ? point : null;
    }

    var intersectionX = Math.round((line2b - line1b) / (line1Slope - line2Slope));
    var intersectionY = Math.round(line1Slope * intersectionX + line1b);

    return new Point(intersectionX, intersectionY);
  }
}
