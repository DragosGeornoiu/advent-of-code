package ro.dragos.geornoiu.year2023.day11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Solution {
  public static void main(String[] args) throws IOException {
    List<String> lines = readInputAsLines();

    // BUild map
    int width = lines.get(0).length();
    int height = lines.size();

    int y = 0;
    char[][] map = computeMap(lines, width, height, y);

    // Part 1
    long result = computeSumOfDistances(width, height, map, 1);
    System.out.println("Result first part: " + result);

    // Part 2
    result = computeSumOfDistances(width, height, map, 999999);
    System.out.println("Result second part: " + result);
  }

  private static char[][] computeMap(List<String> lines, int width, int height, int y) {
    char[][] map = new char[width][height];
    for (String line : lines) {
      for (int x=0;x<line.length();x++) {
        map[x][y] = line.charAt(x);
      }
      y++;
    }
    return map;
  }

  private static long computeSumOfDistances(int width, int height, char[][] map, int increment) {
    long dx = 0;
    long dy = 0;

    long[] colDx = new long[width];
    long[] rowDy = new long[height];

    for (int x = 0;x<width;x++) {
      boolean empty = true;
      for (int y=0;y<height;y++) {
        if (map[x][y] != '.') {
          empty = false;
          break;
        }
      }

      if (empty) {
        dx+= increment;
      }

      colDx[x] = dx;
    }

    for (int y = 0;y<height;y++) {
      boolean empty = true;
      for (int x=0;x<width;x++) {
        if (map[x][y] != '.') {
          empty = false;
          break;
        }
      }

      if (empty) {
        dy+= increment;
      }

      rowDy[y] = dy;
    }

    List<Galaxy> galaxies = getGalaxies(width, height, map, colDx, rowDy);
    return getResult(galaxies);
  }

  private static long getResult(List<Galaxy> galaxies) {
    long result = 0;
    for (int i=0;i< galaxies.size();i++) {
      for (int j=i+1;j< galaxies.size();j++) {
        result += galaxies.get(i).getDistance(galaxies.get(j));
      }
    }
    return result;
  }

  private static List<Galaxy> getGalaxies(int width, int height, char[][] map, long[] colDx,
      long[] rowDy) {
    List<Galaxy> galaxies = new ArrayList<>();
    for (int y=0;y< height;y++) {
      for (int x=0;x< width;x++) {
        if (map[x][y] != '.') {
          galaxies.add(new Galaxy(x+ colDx[x], y+ rowDy[y]));
        }
      }
    }
    return galaxies;
  }

  public record Galaxy(long x, long y) {
    public long getDistance(Galaxy other) {
      return Math.abs(other.x - x) + Math.abs(other.y - y);
    }
  }

  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day11/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Collections.emptyList();
  }
}
