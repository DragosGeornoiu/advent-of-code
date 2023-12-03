package ro.dragos.geornoiu.year2023.day03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SolutionPart1 {
  private static final String RELATIVE_PATH_TO_CUR_DIR =
      "src/main/java/ro/dragos/geornoiu/year2023/day03/";

  private static final String FILENAME_OUTPUT_PART_1 = "outputPart1.txt";
  private static final String FILENAME_INPUT = "input.txt";

  public static void main(String[] args) {
    char[][] matrix = readInputFromFile(FILENAME_INPUT);
    List<Integer> validNumbers = getValidNumbersFromMatrix(matrix);
    Integer sum = validNumbers.stream().mapToInt(Integer::intValue).sum();
    System.out.println("Sum: " + sum);
    writeOutputToFile(String.valueOf(sum), FILENAME_OUTPUT_PART_1);
  }

  private static List<Integer> getValidNumbersFromMatrix(char[][] matrix) {
    List<Integer> validNumbers = new ArrayList<>();
    for (int i = 0; i < matrix.length; i++) {
      int startIndex = -1;
      for (int j = 0; j < matrix[i].length; j++) {
        if (Character.isDigit(matrix[i][j]) && startIndex == -1) {
          startIndex = j;
        } else if (!Character.isDigit(matrix[i][j]) && startIndex != -1) {
          boolean isNumberAdjacentToSymbol = isNumberAdjacentToSymbol(matrix, i, startIndex, j);
          if (isNumberAdjacentToSymbol) {
            int number = computeNumber(matrix[i], startIndex, j);
            validNumbers.add(number);
          }
          startIndex = -1;
        } else if (Character.isDigit(matrix[i][j]) && j == matrix[i].length - 1) {
          boolean isNumberAdjacentToSymbol = isNumberAdjacentToSymbol(matrix, i, startIndex, j);
          if (isNumberAdjacentToSymbol) {
            int number = computeNumber(matrix[i], startIndex, j + 1);
            System.out.println("Number was determined to be adjacent: " + number);
            validNumbers.add(number);
          }
          startIndex = -1;
        } else if (!Character.isDigit(matrix[i][j]) || startIndex == -1) {
          startIndex = -1;
        }
      }
    }

    return validNumbers;
  }

  private static int computeNumber(char[] line, int startIndex, int endIndex) {
    int numberOfDigits = endIndex - startIndex;
    int result = 0;
    for (int i = 0; i < numberOfDigits; i++) {
      int powerResult = (int) Math.pow(10, i);
      result += Character.getNumericValue(line[endIndex - 1 - i]) * powerResult;
    }

    return result;
  }

  private static boolean isNumberAdjacentToSymbol(
      char[][] matrix, int lineNumber, int startIndex, int endIndex) {
    if (startIndex == 0) {
      startIndex = 1;
    }

    if (lineNumber > 0) {
      for (int i = startIndex - 1; i <= endIndex; i++) {
        if (isSymbol(matrix[lineNumber - 1][i])) {
          return true;
        }
      }
    }

    if (startIndex > 0 && isSymbol(matrix[lineNumber][startIndex - 1])) {
      return true;
    }

    if (endIndex < matrix[lineNumber].length - 1 && isSymbol(matrix[lineNumber][endIndex])) {
      return true;
    }

    if (lineNumber < matrix.length - 1) {
      for (int i = startIndex - 1; i <= endIndex; i++) {
        if (isSymbol(matrix[lineNumber + 1][i])) {
          return true;
        }
      }
    }

    return false;
  }

  private static boolean isSymbol(char c) {
    return !Character.isLetterOrDigit(c) && c != '.';
  }

  private static char[][] readInputFromFile(String fileName) {
    String input = "";
    try {
      String currentDirectory = System.getProperty("user.dir");
      Path filePath = Paths.get(currentDirectory, RELATIVE_PATH_TO_CUR_DIR + fileName);
      input = Files.readString(filePath);
    } catch (IOException e) {
      throw new RuntimeException("Error reading file: " + RELATIVE_PATH_TO_CUR_DIR + fileName, e);
    }

    return input.lines().map(String::toCharArray).toArray(char[][]::new);
  }

  private static void writeOutputToFile(String output, String fileName) {
    try {
      String currentDirectory = System.getProperty("user.dir");
      Path filePath = Paths.get(currentDirectory, RELATIVE_PATH_TO_CUR_DIR + fileName);
      Files.write(
          filePath,
          Collections.singletonList(output),
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Failed to write to file %s", fileName));
    }
  }
}
