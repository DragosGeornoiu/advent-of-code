package ro.dragos.geornoiu.year2023.day16;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Solution {

  private enum Direction {
    RIGHT,
    LEFT,
    UP,
    DOWN
  }

  private record Position(int i, int j, Direction direction) {}

  public static void main(String[] args) {
    char[][] matrix = readInputAsChars();

    Position startingPosition = new Position(0, 0, Direction.RIGHT);
    long result = getEnergizedTiles(matrix, startingPosition);
    System.out.println("Result first part: " + result);

    long maxEnergizedTiles = getMaxEnergizedTiles(matrix);
    System.out.println("Result second part: " + maxEnergizedTiles);
  }

  private static long getMaxEnergizedTiles(char[][] matrix) {
    long maxEnergizedTiles = 0;
    Position maxEnergizedTilesPosition = null;

    for (int j = 0; j < matrix[0].length; j++) {
      Position startingPosition = new Position(0, j, Direction.DOWN);
      long energizedTiles = getEnergizedTiles(matrix, startingPosition);
      if (energizedTiles > maxEnergizedTiles) {
        maxEnergizedTiles = energizedTiles;
        maxEnergizedTilesPosition = startingPosition;
      }
    }

    for (int j = 0; j < matrix[0].length; j++) {
      Position startingPosition = new Position(matrix.length - 1, j, Direction.UP);
      long energizedTiles = getEnergizedTiles(matrix, startingPosition);
      if (energizedTiles > maxEnergizedTiles) {
        maxEnergizedTiles = energizedTiles;
        maxEnergizedTilesPosition = startingPosition;
      }
    }

    for (int i = 0; i < matrix.length; i++) {
      Position startingPosition = new Position(i, 0, Direction.RIGHT);
      long energizedTiles = getEnergizedTiles(matrix, startingPosition);
      if (energizedTiles > maxEnergizedTiles) {
        maxEnergizedTiles = energizedTiles;
        maxEnergizedTilesPosition = startingPosition;
      }
    }

    for (int i = 0; i < matrix.length; i++) {
      Position startingPosition = new Position(i, matrix[i].length - 1, Direction.LEFT);
      long energizedTiles = getEnergizedTiles(matrix, startingPosition);
      if (energizedTiles > maxEnergizedTiles) {
        maxEnergizedTiles = energizedTiles;
        maxEnergizedTilesPosition = startingPosition;
      }
    }

    System.out.printf(
        "Max Energized tiles are obtained from position %s%n", maxEnergizedTilesPosition);
    return maxEnergizedTiles;
  }

  private static long getEnergizedTiles(char[][] matrix, Position startingPosition) {
    char[][] emptyMatrix = createEmptyMatrix(matrix.length, matrix[0].length);
    char[][] energizedMatrix =
        computeEnergizedTiles(matrix, emptyMatrix, startingPosition, new HashSet<>());
    return countEnergizedTiles(energizedMatrix);
  }

  private static long countEnergizedTiles(char[][] energizedMatrix) {
    long count = 0;
    for (int i = 0; i < energizedMatrix.length; i++) {
      for (int j = 0; j < energizedMatrix[i].length; j++) {
        if (energizedMatrix[i][j] == '#') {
          count++;
        }
      }
    }

    return count;
  }

  private static char[][] computeEnergizedTiles(
      char[][] layoutMatrix,
      char[][] energizedMatrix,
      Position currentPosition,
      Set<Position> prevEncounters) {
    if (isOutsideOfBounds(layoutMatrix, currentPosition)) {
      return energizedMatrix;
    }

    energizedMatrix[currentPosition.i][currentPosition.j] = '#';

    if (prevEncounters.contains(currentPosition)) {
      return energizedMatrix;
    }
    prevEncounters.add(currentPosition);

    char currentSymbol = layoutMatrix[currentPosition.i][currentPosition.j];
    if (currentSymbol == '.') {
      Position nextPosition = getNextPositionForEmptySpace(currentPosition);
      return computeEnergizedTiles(layoutMatrix, energizedMatrix, nextPosition, prevEncounters);
    }

    if (currentSymbol == '\\' || currentSymbol == '/') {
      Position nextPosition = getNextPositionForMirror(currentSymbol, currentPosition);
      return computeEnergizedTiles(layoutMatrix, energizedMatrix, nextPosition, prevEncounters);
    }

    if (currentSymbol == '|' || currentSymbol == '-') {
      List<Position> nextPositions = getNextPositionForSplitter(currentSymbol, currentPosition);
      for (Position nextPosition : nextPositions) {
        char[][] localEnergizedMatrix =
            computeEnergizedTiles(layoutMatrix, energizedMatrix, nextPosition, prevEncounters);
        mergeMatrixes(energizedMatrix, localEnergizedMatrix);
      }

      return energizedMatrix;
    }

    return energizedMatrix;
  }

  private static boolean isOutsideOfBounds(char[][] layoutMatrix, Position currentPosition) {
    return currentPosition.i < 0
        || currentPosition.i >= layoutMatrix.length
        || currentPosition.j < 0
        || currentPosition.j >= layoutMatrix[0].length;
  }

  private static char[][] mergeMatrixes(char[][] energizedMatrix, char[][] localEnergizedMatrix) {
    for (int i = 0; i < localEnergizedMatrix.length; i++) {
      for (int j = 0; j < localEnergizedMatrix[i].length; j++) {
        if (localEnergizedMatrix[i][j] == '#') {
          energizedMatrix[i][j] = '#';
        }
      }
    }

    return energizedMatrix;
  }

  private static List<Position> getNextPositionForSplitter(
      char currentSymbol, Position currentPosition) {
    if (currentSymbol == '|') {
      if (currentPosition.direction == Direction.RIGHT
          || currentPosition.direction == Direction.LEFT) {
        Position firstBeam = new Position(currentPosition.i - 1, currentPosition.j, Direction.UP);
        Position secondBeam =
            new Position(currentPosition.i + 1, currentPosition.j, Direction.DOWN);
        return List.of(firstBeam, secondBeam);
      } else {
        Position nextPosition = getNextPositionForEmptySpace(currentPosition);
        return List.of(nextPosition);
      }
    } else {
      // currentSymbol is -
      if (currentPosition.direction == Direction.UP
          || currentPosition.direction == Direction.DOWN) {
        Position firstBeam = new Position(currentPosition.i, currentPosition.j - 1, Direction.LEFT);
        Position secondBeam =
            new Position(currentPosition.i, currentPosition.j + 1, Direction.RIGHT);
        return List.of(firstBeam, secondBeam);
      } else {
        Position nextPosition = getNextPositionForEmptySpace(currentPosition);
        return List.of(nextPosition);
      }
    }
  }

  private static Position getNextPositionForMirror(char currentSymbol, Position currentPosition) {
    if (currentSymbol == '\\') {
      if (currentPosition.direction == Direction.RIGHT) {
        return new Position(currentPosition.i + 1, currentPosition.j, Direction.DOWN);
      } else if (currentPosition.direction == Direction.LEFT) {
        return new Position(currentPosition.i - 1, currentPosition.j, Direction.UP);
      } else if (currentPosition.direction == Direction.DOWN) {
        return new Position(currentPosition.i, currentPosition.j + 1, Direction.RIGHT);
      } else {
        return new Position(currentPosition.i, currentPosition.j - 1, Direction.LEFT);
      }
    } else {
      // currentSymbol is /
      if (currentPosition.direction == Direction.RIGHT) {
        return new Position(currentPosition.i - 1, currentPosition.j, Direction.UP);
      } else if (currentPosition.direction == Direction.LEFT) {
        return new Position(currentPosition.i + 1, currentPosition.j, Direction.DOWN);
      } else if (currentPosition.direction == Direction.DOWN) {
        return new Position(currentPosition.i, currentPosition.j - 1, Direction.LEFT);
      } else {
        return new Position(currentPosition.i, currentPosition.j + 1, Direction.RIGHT);
      }
    }
  }

  private static Position getNextPositionForEmptySpace(Position currentPosition) {
    if (currentPosition.direction == Direction.RIGHT) {
      return new Position(currentPosition.i, currentPosition.j + 1, Direction.RIGHT);
    } else if (currentPosition.direction == Direction.LEFT) {
      return new Position(currentPosition.i, currentPosition.j - 1, Direction.LEFT);
    } else if (currentPosition.direction == Direction.DOWN) {
      return new Position(currentPosition.i + 1, currentPosition.j, Direction.DOWN);
    } else {
      return new Position(currentPosition.i - 1, currentPosition.j, Direction.UP);
    }
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
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day16/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }

  private static char[][] createEmptyMatrix(int rows, int columns) {
    char[][] dotMatrix = new char[rows][columns];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        dotMatrix[i][j] = '.';
      }
    }
    return dotMatrix;
  }
}
