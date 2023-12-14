package ro.dragos.geornoiu.year2023.day14;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solution {

  public static void main(String[] args) throws IOException {
    char[][] matrix = readInputAsChars();
    tiltMatrixNorth(matrix);
    long result = calculateLoad(matrix);
    System.out.println("Result first part: " + result);

    matrix = readInputAsChars();
    tiltMatrixInCycles(matrix,1000000000 );
    long resultSecondPart = calculateLoad(matrix);
    System.out.println("Result second part: " + resultSecondPart);
  }

  private static boolean canRockTiltNorth(char[][] matrix, int i, int j) {
    if (i == 0) {
      return false;
    }

    return matrix[i - 1][j] == '.';
  }

  private static boolean canRockTiltSouth(char[][] matrix, int i, int j) {
    if (i >= matrix.length - 1) {
      return false;
    }

    return matrix[i + 1][j] == '.';
  }

  private static boolean canRockTiltWest(char[][] matrix, int i, int j) {
    if (j == 0) {
      return false;
    }

    return matrix[i][j - 1] == '.';
  }

  private static boolean canRockTiltEast(char[][] matrix, int i, int j) {
    if (j >= matrix[i].length - 1) {
      return false;
    }

    return matrix[i][j + 1] == '.';
  }

  private static long calculateLoad(char[][] matrix) {
    long result = 0;
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] == 'O') {
          result += matrix.length - i;
        }
      }
    }

    return result;
  }

  private static void tiltMatrixInCycles(char[][] matrix, int noOfCycles) {
    Map<String, Integer> previousMatrixes = new HashMap<>();
    for (int i = 0; i < noOfCycles; i++) {
      tiltMatrixNorth(matrix);
      tiltMatrixWest(matrix);
      tiltMatrixSouth(matrix);
      tiltMatrixEast(matrix);

      String matrixRepresentation = getMatrixRepresentation(matrix);
      if(previousMatrixes.containsKey(matrixRepresentation)) {
        int lastEncounterOfSameMatrix = previousMatrixes.get(matrixRepresentation);
        int cycleLength = i - lastEncounterOfSameMatrix;

        i = noOfCycles - (noOfCycles - i) % cycleLength;
        previousMatrixes = new HashMap<>();
      }

      previousMatrixes.put(matrixRepresentation, i);
    }
  }

  private static String getMatrixRepresentation(char[][] matrix) {
    StringBuilder sb = new StringBuilder();

    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] == 'O') {
          sb.append(i).append(j);
        }
      }
    }

    return sb.toString();
  }


  private static void tiltMatrixNorth(char[][] matrix) {
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] == 'O') {
          int currRockRow = i;
          int currRockCol = j;
          if (canRockTiltNorth(matrix, currRockRow, currRockCol)) {
            while (canRockTiltNorth(matrix, currRockRow, currRockCol)) {
              currRockRow--;
            }
            matrix[currRockRow][currRockCol] = 'O';
            matrix[i][j] = '.';
          }
        }
      }
    }
  }


  private static void tiltMatrixWest(char[][] matrix) {
    for (int j = 0; j < matrix.length; j++) {
      for (int i = 0; i < matrix.length; i++) {
        if (matrix[i][j] == 'O') {
          int currRockRow = i;
          int currRockCol = j;
          if (canRockTiltWest(matrix, currRockRow, currRockCol)) {
            while (canRockTiltWest(matrix, currRockRow, currRockCol)) {
              currRockCol--;
            }
            matrix[currRockRow][currRockCol] = 'O';
            matrix[i][j] = '.';
          }
        }
      }
    }
  }

  private static void tiltMatrixSouth(char[][] matrix) {
    for (int i = matrix.length - 1; i >= 0; i--) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] == 'O') {
          int currRockRow = i;
          int currRockCol = j;
          if (canRockTiltSouth(matrix, currRockRow, currRockCol)) {
            while (canRockTiltSouth(matrix, currRockRow, currRockCol)) {
              currRockRow++;
            }
            matrix[currRockRow][currRockCol] = 'O';
            matrix[i][j] = '.';
          }
        }
      }
    }
  }

  private static void tiltMatrixEast(char[][] matrix) {
    for (int j = matrix.length - 1; j >= 0; j--) {
      for (int i = 0; i < matrix.length; i++) {
        if (matrix[i][j] == 'O') {
          int currRockRow = i;
          int currRockCol = j;
          if (canRockTiltEast(matrix, currRockRow, currRockCol)) {
            while (canRockTiltEast(matrix, currRockRow, currRockCol)) {
              currRockCol++;
            }
            matrix[currRockRow][currRockCol] = 'O';
            matrix[i][j] = '.';
          }
        }
      }
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
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day14/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>(); // Return an empty list if there's an error
  }
}
