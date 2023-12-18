package ro.dragos.geornoiu.year2023.day18;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Solution {
  private enum Direction {
    RIGHT,
    LEFT,
    UP,
    DOWN;

    public static Direction getDirection(String shortDir) {
      if ("R".equals(shortDir)) {
        return RIGHT;
      }

      if ("L".equals(shortDir)) {
        return LEFT;
      }

      if ("U".equals(shortDir)) {
        return UP;
      }

      if ("D".equals(shortDir)) {
        return DOWN;
      }

      throw new IllegalArgumentException();
    }
  }

  private record Instruction(Direction direction, int length, String rgbColour) {
    Instruction convertColor() {
      String num = rgbColour.substring(2, rgbColour.length()-2);
      char codedDir = rgbColour.charAt(rgbColour.length()-2);
      int codedLength = Integer.parseInt(num, 16);
      return switch(codedDir) {
        case '0' -> new Instruction(Direction.RIGHT, codedLength, "");
        case '1' -> new Instruction(Direction.DOWN, codedLength, "");
        case '2' -> new Instruction(Direction.LEFT, codedLength, "");
        case '3' -> new Instruction(Direction.UP, codedLength, "");
        default -> throw new IllegalStateException("unknown coded dir: "+codedDir);
      };
  }
}
  private record Point(long x, long y) {
    Point move(Direction direction, int length) {
      return switch(direction) {
        case LEFT -> new Point(x-length, y);
        case RIGHT -> new Point(x+length, y);
        case UP -> new Point(x, y-length);
        case DOWN -> new Point(x, y+length);
        default -> throw new IllegalArgumentException("Unknown direction: " + direction);
      };
    }
  }

  public static void main(String[] args) {
    List<Instruction> instructions = readInputInstructions();

    long result = calcArea(instructions);
    System.out.println("Result first part: " + result);

    var decoded = instructions.stream().map(Instruction::convertColor).toList();
    long resultPart2 = calcArea(decoded);
    System.out.println("Result second part: " + resultPart2);
  }

  /**
   * Calculates the area enclosed by a set of instructions.
   *
   * The function takes a list of instructions, where each instruction specifies a direction
   * and a length. It calculates the area enclosed by the path formed by connecting the points
   * based on these instructions.
   *
   * @param instructions A list of instructions specifying the direction and length of each line segment.
   * @return The calculated area enclosed by the path formed by connecting the points.
   */
  private static long calcArea(List<Instruction> instructions) {
    long line = 0L;  // Total length of the path
    long area = 0L;  // Enclosed area
    List<Point> points = new ArrayList<>();  // List to store points on the path

    // Start from the origin (0, 0)
    points.add(new Point(0, 0));

    // Calculate points on the path based on instructions
    for (var instruction : instructions) {
      line += instruction.length();
      points.add(points.get(points.size() - 1).move(instruction.direction(), instruction.length()));
    }

    // Calculate the area using the shoelace formula
    for (int i = 0; i < points.size(); ++i) {
      Point firstPoint = points.get(i);
      Point secondPoint = points.get((i + 1) % points.size());
      area += ((long) firstPoint.y + secondPoint.y) * (firstPoint.x - secondPoint.x);
    }

    // The absolute value of the area is calculated and divided by 2, then 1 is added.
    return (Math.abs(area) + line) / 2 + 1;
  }

  private static List<Instruction> readInputInstructions() {
    List<String> lines = readInputAsLines();

    return lines.stream()
        .map(
            line -> {
              String[] splits = line.split(" ");
              Direction direction = Direction.getDirection(splits[0]);
              return new Instruction(
                  direction,
                  Integer.parseInt(splits[1]),
                  splits[2]);
            })
        .toList();
  }

  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day18/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }
}
