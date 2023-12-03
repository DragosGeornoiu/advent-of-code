package ro.dragos.geornoiu.year2023.day03;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SolutionPart2 {
  private static final String RELATIVE_PATH_TO_CUR_DIR =
      "src/main/java/ro/dragos/geornoiu/year2023/day03/";

  private static final String FILENAME_OUTPUT_PART_2 = "outputPart2.txt";
  private static final String FILENAME_INPUT = "input.txt";

  record NumberPosition(int line, int startIndex, int endIndex, int value) {}

  record GearPosition(int line, int col) {}

  public static void main(String[] args) {
    char[][] matrix = readInputFromFile(FILENAME_INPUT);
    List<NumberPosition> numbers = getNumbers(matrix);
    List<GearPosition> gearPositions = getGearPositions(matrix);
    List<Long> gearRatios = getGearRations(matrix, gearPositions, numbers);
    long sum = gearRatios.stream().mapToLong(Long::longValue).sum();
    System.out.println("Sum of Gear Ratios: " + sum);
    writeOutputToFile(String.valueOf(sum), FILENAME_OUTPUT_PART_2);
  }

  private static List<NumberPosition> getNumbers(char[][] matrix) {
    List<NumberPosition> validNumbers = new ArrayList<>();
    for (int i = 0; i < matrix.length; i++) {
      int startIndex = -1;
      for (int j = 0; j < matrix[i].length; j++) {
        if (Character.isDigit(matrix[i][j]) && startIndex == -1) {
          startIndex = j;
        } else if (!Character.isDigit(matrix[i][j]) && startIndex != -1) {
          int number = computeNumber(matrix[i], startIndex, j);
          validNumbers.add(new NumberPosition(i, startIndex, j - 1, number));
          startIndex = -1;
        } else if (Character.isDigit(matrix[i][j]) && j == matrix[i].length - 1) {
          int number = computeNumber(matrix[i], startIndex, j + 1);
          validNumbers.add(new NumberPosition(i, startIndex, j, number));
          startIndex = -1;
        } else if (!Character.isDigit(matrix[i][j]) || startIndex == -1) {
          startIndex = -1;
        }
      }
    }

    return validNumbers;
  }

  private static List<GearPosition> getGearPositions(char[][] matrix) {
    List<GearPosition> gearPositions = new ArrayList<>();
    for (int i = 0; i < matrix.length; i++) {
      for (int j = 0; j < matrix[i].length; j++) {
        if (matrix[i][j] == '*') {
          gearPositions.add(new GearPosition(i, j));
        }
      }
    }

    return gearPositions;
  }

  private static List<Long> getGearRations(
      char[][] matrix, List<GearPosition> gearPositions, List<NumberPosition> numbers) {
    Map<Integer, List<NumberPosition>> numbersPerLineMap =
        numbers.stream().collect(Collectors.groupingBy(NumberPosition::line));
    List<Long> gearRatios = new ArrayList<>();

    for (GearPosition gearPosition : gearPositions) {
      List<NumberPosition> adjacentParts = new ArrayList<>();
      int gearLine = gearPosition.line;

      if (gearLine > 0) {
        List<NumberPosition> prevLineNumbers =
            numbersPerLineMap.getOrDefault(gearLine - 1, Collections.emptyList());
        int limitStartIndex = gearPosition.col > 0 ? gearPosition.col - 1 : 0;
        int limitEndIndex =
            matrix[gearLine].length - 1 == gearPosition.col
                ? matrix[gearLine].length - 1
                : gearPosition.col + 1;

        for (NumberPosition numberPosition : prevLineNumbers) {
          if (isNumberPositionAdjacent(limitStartIndex, limitEndIndex, numberPosition)) {
            adjacentParts.add(numberPosition);
          }
        }
      }

      List<NumberPosition> currentLineNumbers =
          numbersPerLineMap.getOrDefault(gearLine, Collections.emptyList());
      int limitStartIndex = gearPosition.col > 0 ? gearPosition.col - 1 : 0;
      int limitEndIndex =
          matrix[gearLine].length - 1 == gearPosition.col
              ? matrix[gearLine].length - 1
              : gearPosition.col + 1;
      for (NumberPosition numberPosition : currentLineNumbers) {
        if (isNumberPositionAdjacent(limitStartIndex, limitEndIndex, numberPosition)) {
          adjacentParts.add(numberPosition);
        }
      }

      if (gearLine < matrix.length - 1) {
        List<NumberPosition> nextLineNumbers =
            numbersPerLineMap.getOrDefault(gearLine + 1, Collections.emptyList());
        limitStartIndex = gearPosition.col > 0 ? gearPosition.col - 1 : 0;
        limitEndIndex =
            matrix[gearLine].length - 1 == gearPosition.col
                ? matrix[gearLine].length - 1
                : gearPosition.col + 1;
        for (NumberPosition numberPosition : nextLineNumbers) {
          if (isNumberPositionAdjacent(limitStartIndex, limitEndIndex, numberPosition)) {
            adjacentParts.add(numberPosition);
          }
        }
      }

      if (adjacentParts.size() == 2) {
        gearRatios.add((long) adjacentParts.get(0).value * (long) adjacentParts.get(1).value);
      }
    }

    return gearRatios;
  }

  private static boolean isNumberPositionAdjacent(
      int limitStartIndex, int limitEndIndex, NumberPosition numberPosition) {
    if (numberPosition.endIndex >= limitStartIndex && numberPosition.endIndex <= limitEndIndex) {
      return true;
    }

    if (numberPosition.startIndex >= limitStartIndex
        && numberPosition.startIndex <= limitEndIndex) {
      return true;
    }

    return false;
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
