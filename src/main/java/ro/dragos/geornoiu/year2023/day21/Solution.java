package ro.dragos.geornoiu.year2023.day21;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

  private record Point(int x, int y) {}

  private static final int PART1_INDEX_LIMIT = 64;
  private static final int PART2_INDEX_LIMIT = 1000;

  public static void main(String[] args) {
    char[][] input = readInputAsChars();

    long resultPart1 = solvePart1(input);
    System.out.println("Result first part: " + resultPart1);

    long resultPart2 = solvePart2(input);
    System.out.println("Result second part: " + resultPart2);
  }

  public static long solvePart1(char[][] input) {
    long answer = 0;

    int x = 0;
    int y = 0;
    int[][] grid = new int[input[0].length][input.length];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (input[j][i] == 'S') {
          grid[i][j] = 9999;
          x = i;
          y = j;
        } else {
          grid[i][j] = input[j][i] == '#' ? -1 : 9999;
        }
      }
    }

    Map<Point, Integer> distances = new HashMap<>();
    List<Point> reachablePoints = new ArrayList<>();

    reachablePoints.add(new Point(x, y));
    distances.put(new Point(x, y), 0);
    int[] xs = new int[] {-1, 1, 0, 0};
    int[] ys = new int[] {0, 0, -1, 1};
    int index = 0;
    long totalReached = 0;

    while (index < PART1_INDEX_LIMIT) {
      index++;
      List<Point> points = new ArrayList<>();
      for (Point c : reachablePoints) {
        for (int i = 0; i < 4; i++) {
          int newX = c.x + xs[i];
          int newY = c.y + ys[i];
          Point candidate = new Point(newX, newY);
          if (distances.get(candidate) == null
              && grid[((newX % grid.length) + grid.length) % grid.length][
                      ((newY % grid.length) + grid.length) % grid.length]
                  != -1) {
            points.add(candidate);
            distances.put(candidate, index);
          }
        }
      }

      if (index % 2 == 0) {
        totalReached += points.size();
        if (index % 262 == 65) {
          break;
        }
      }
      reachablePoints = points;
    }

    answer = totalReached + 1; // to account for the origin location
    return answer;
  }

  public static long solvePart2(char[][] input) {

    int x = 0;
    int y = 0;
    int[][] grid = new int[input[0].length][input.length];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (input[j][i] == 'S') {
          grid[i][j] = 9999;
          x = i;
          y = j;
        } else {
          grid[i][j] = input[j][i] == '#' ? -1 : 9999;
        }
      }
    }

    Map<Point, Integer> distances = new HashMap<>();
    List<Point> reachablePoints = new ArrayList<>();

    reachablePoints.add(new Point(x, y));
    distances.put(new Point(x, y), 0);
    int[] xs = new int[] {-1, 1, 0, 0};
    int[] ys = new int[] {0, 0, -1, 1};
    int index = 0;
    List<Long> totals = new ArrayList<>();
    List<Long> deltas = new ArrayList<>();
    List<Long> deltaDeltas = new ArrayList<>();
    long totalReached = 0;

    while (index < PART2_INDEX_LIMIT) {
      index++;
      List<Point> tmp2 = new ArrayList<>();
      for (Point c : reachablePoints) {
        for (int i = 0; i < 4; i++) {
          int newx = c.x + xs[i];
          int newy = c.y + ys[i];
          Point candidate = new Point(newx, newy);
          if (distances.get(candidate) == null) {
            if (grid[((newx % grid.length) + grid.length) % grid.length][
                    ((newy % grid.length) + grid.length) % grid.length]
                != -1) {
              tmp2.add(candidate);
              distances.put(candidate, index);
            }
          }
        }
      }
      if (index % 2 == 1) {
        totalReached += tmp2.size();
        if (index % 262 == 65) {
          totals.add(totalReached);
          int currTotals = totals.size();
          if (currTotals > 1) {
            deltas.add(totals.get(currTotals - 1) - totals.get(currTotals - 2));
          }
          int currDeltas = deltas.size();
          if (currDeltas > 1) {
            deltaDeltas.add(deltas.get(currDeltas - 1) - deltas.get(currDeltas - 2));
          }
          if (deltaDeltas.size() > 1) {
            break;
          }
        }
      }
      reachablePoints = tmp2;
    }

    return calculateDeltaLoopCountTriangular(totals, deltas, deltaDeltas, index, totalReached);
  }

  private static long calculateDeltaLoopCountTriangular(
      List<Long> totals, List<Long> deltas, List<Long> deltaDeltas, int index, long totalReached) {
    long neededLoopCount = 26501365 / 262 - 1;
    long currentLoopCount = index / 262 - 1;
    long deltaLoopCount = neededLoopCount - currentLoopCount;
    long deltaLoopCountTriangular =
        (neededLoopCount * (neededLoopCount + 1)) / 2
            - (currentLoopCount * (currentLoopCount + 1)) / 2;
    long deltaDelta = deltaDeltas.get(deltaDeltas.size() - 1);
    long initialDelta = deltas.get(0);

    return (deltaDelta * deltaLoopCountTriangular + initialDelta * deltaLoopCount + totalReached);
  }

  private static char[][] readInputAsChars() {
    List<String> lines = readInputAsLines();
    char[][] charArray = new char[lines.size()][];

    for (int i = 0; i < lines.size(); i++) {
      charArray[i] = lines.get(i).toCharArray();
    }

    return charArray;
  }

  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day21/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }
}
