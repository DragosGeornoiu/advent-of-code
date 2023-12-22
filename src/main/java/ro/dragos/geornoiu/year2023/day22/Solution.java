package ro.dragos.geornoiu.year2023.day22;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solution {

  public static void main(String[] args) {
    List<String> inputLines = readInputAsLines();

    int resultPart1 = solvePart1(inputLines);
    System.out.println("Result first part: " + resultPart1);

    int resultPart2 = solvePart2(inputLines);
    System.out.println("Result second part: " + resultPart2);
  }

  private static int solvePart1(List<String> lines) {
    Set<Brick> bricks = getFallenBricks(lines);

    int bricksToDisintegrate = 0;

    for (Brick brick : bricks) {
      boolean shouldContinue = true;

      for (Brick supportedBrick : brick.getSupportedBricks()) {
        if (supportedBrick.getSupportingBricks().size() <= 1) {
          shouldContinue = false;
          break;
        }
      }

      if (!shouldContinue) {
        continue;
      }

      bricksToDisintegrate++;
    }

    return bricksToDisintegrate;
  }

  private static int solvePart2(List<String> lines) {
    Set<Brick> bricks = getFallenBricks(lines);

    int sum = 0;
    for (Brick brick : bricks) {
      sum += getMaxFallingBricks(brick, new ArrayList<>(bricks));
    }

    return sum;
  }

  private static int getMaxFallingBricks(Brick brick, List<Brick> bricks) {
    bricks.remove(brick);
    int fallingBricks = 0;
    Set<Brick> checkNext = brick.getSupportedBricks();

    while (!checkNext.isEmpty()) {
      Set<Brick> nextCheckNext = new HashSet<>();
      Set<Brick> bricksToRemove = new HashSet<>();

      for (Brick checking : checkNext) {
        if (!bricks.contains(checking)) {
          continue;
        }

        boolean canRemove = true;

        for (Brick supportingBrick : checking.getSupportingBricks()) {
          if (bricks.contains(supportingBrick)) {
            canRemove = false;
            break;
          }
        }

        if (canRemove) {
          fallingBricks++;
          bricksToRemove.add(checking);
          nextCheckNext.addAll(checking.getSupportedBricks());
        }
      }

      bricks.removeAll(bricksToRemove);
      checkNext = nextCheckNext;
    }

    return fallingBricks;
  }

  private static Set<Brick> getFallenBricks(List<String> lines) {
    List<Brick> bricks = new ArrayList<>();
    for (String line : lines) {
      Brick brick = Brick.of(line);
      bricks.add(brick);
    }

    bricks.sort(Comparator.comparingInt(Brick::getMinHeight));

    Map<Coordinate, Brick> cubes = new HashMap<>();
    Set<Brick> fallenBricks = new HashSet<>();

    Iterator<Brick> brickIterator = bricks.iterator();
    while (brickIterator.hasNext()) {
      Brick brick = brickIterator.next();
      while (brick.getMinHeight() > 1) {
        Brick nextBrick = brick.moveDown();
        boolean canMoveDown = nextBrick.getAllCubes().stream().noneMatch(cubes::containsKey);
        if (!canMoveDown) {
          break;
        }
        brick = nextBrick;
      }
      fallenBricks.add(brick);
      for (Coordinate cube : brick.getAllCubes()) {
        cubes.put(cube, brick);
      }
      brickIterator.remove();
    }

    for (Brick brick : fallenBricks) {
      for (Coordinate cube : brick.getMaxZCoordinates()) {
        Coordinate pos = new Coordinate(cube.x(), cube.y(), cube.z() + 1);
        if (cubes.containsKey(pos)) {
          cubes.get(pos).getSupportingBricks().add(brick);
          brick.getSupportedBricks().add(cubes.get(pos));
        }
      }
    }

    return fallenBricks;
  }

  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day22/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }
}
