package ro.dragos.geornoiu.year2023.day23;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class Solution {

  public record Pair(int i, int j) {}

  private record Point(Pair pair, int steps) {}

  private static char[][] map;
  private static boolean[][] visited = null;
  private static Pair start;
  private static Pair end;
  private static Deque<Point> queue;

  public static void main(String[] args) {
    List<String> lines = readInputAsLines();

    initializeInput(lines);
    long resultPart1 = findLongestRoute(false);
    System.out.println("Result first part: " + resultPart1);

    initializeInput(lines);
    long resultPart2 = findLongestRoute(true);
    System.out.println("Result second part: " + resultPart2);
  }

  private static void initializeInput(List<String> lines) {
    map = lines.stream().filter(s -> !s.isBlank()).map(String::toCharArray).toArray(char[][]::new);
    start = new Pair(0, 1);
    end = new Pair(map.length - 1, map[0].length - 2);
  }

  /**
   * Finds the longest route in the map.
   *
   * @param climbing Indicates whether climbing is allowed.
   * @return The length of the longest route.
   */
  public static int findLongestRoute(boolean climbing) {
    int n = map.length - 1;
    int m = map[0].length - 1;
    visited = new boolean[n + 1][m + 1];
    queue = new LinkedList<>();
    queue.add(new Point(start, 0));

    int steps = 0;
    while (!queue.isEmpty()) {
      var point = queue.poll();
      if (end.equals(point.pair)) {
        steps = Math.max(steps, point.steps);
      } else {
        int i = point.pair.i();
        int j = point.pair.j();
        if (point.steps == -1) {
          visited[i][j] = false;
        } else {
          visited[i][j] = true;
          queue.push(new Point(point.pair, -1));
          move(i, j, point.steps, climbing);
        }
      }
    }
    return steps;
  }

  /**
   * Moves to neighboring points based on the current position.
   *
   * @param i Current row index.
   * @param j Current column index.
   * @param steps Current number of steps taken.
   * @param climbing Indicates whether climbing is allowed.
   */
  private static void move(int i, int j, int steps, boolean climbing) {
    int nextStep = steps + 1;
    if (canAddPoint(i - 1, j, 'v', climbing)) {
      queue.push(new Point(new Pair(i - 1, j), nextStep));
    }
    if (canAddPoint(i, j - 1, '>', climbing)) {
      queue.push(new Point(new Pair(i, j - 1), nextStep));
    }
    if (canAddPoint(i + 1, j, '^', climbing)) {
      queue.push(new Point(new Pair(i + 1, j), nextStep));
    }
    if (canAddPoint(i, j + 1, '<', climbing)) {
      queue.push(new Point(new Pair(i, j + 1), nextStep));
    }
  }

  /**
   * Checks if a point can be added based on certain conditions.
   *
   * @param i Row index of the point.
   * @param j Column index of the point.
   * @param wall Wall character for climbing.
   * @param climbing Indicates whether climbing is allowed.
   * @return True if the point can be added, false otherwise.
   */
  private static boolean canAddPoint(int i, int j, char wall, boolean climbing) {
    return isIndexInMatrix(visited, i, j)
        && !visited[i][j]
        && map[i][j] != '#'
        && (climbing || map[i][j] != wall);
  }

  /**
   * Checks if the given indices are within the bounds of the matrix.
   *
   * @param matrix The matrix to check.
   * @param i Row index.
   * @param j Column index.
   * @return True if the indices are within bounds, false otherwise.
   */
  public static boolean isIndexInMatrix(boolean[][] matrix, int i, int j) {
    return i >= 0 && i < matrix.length && j >= 0 && j < matrix[0].length;
  }

  /**
   * Reads input from a file and returns it as a list of strings.
   *
   * @return List of input lines.
   */
  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day23/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }
}
