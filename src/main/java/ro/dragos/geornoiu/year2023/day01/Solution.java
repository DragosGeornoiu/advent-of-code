package ro.dragos.geornoiu.year2023.day01;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

public class Solution {

  record PositionAndValueDigitPair(int index, int value) {}

  private static final List<String> ALPHABETICAL_DIGITS =
      List.of("one", "two", "three", "four", "five", "six", "seven", "eight", "nine");

  public static void main(String[] args) {
    String input = readInputFromFile("src/main/java/ro/dragos/geornoiu/year2023/day01/input.txt");
    int sumPart1 = calculateSumPart1(input);
    System.out.println("Sum Part 1: " + sumPart1);
    writeOutputToFile(
        String.valueOf(sumPart1),
        "src/main/java/ro/dragos/geornoiu/year2023/day01/outputPart1.txt");
    int sumPart2 = calculateSumPart2(input);
    System.out.println("Sum Part 2: " + sumPart2);
    writeOutputToFile(
        String.valueOf(sumPart2),
        "src/main/java/ro/dragos/geornoiu/year2023/day01/outputPart2.txt");
  }

  private static String readInputFromFile(String fileName) {
    try {
      String currentDirectory = System.getProperty("user.dir");
      Path filePath = Paths.get(currentDirectory, fileName);
      return Files.readString(filePath);
    } catch (IOException e) {
      throw new RuntimeException("Error reading file: " + fileName, e);
    }
  }

  private static void writeOutputToFile(String output, String fileName) {
    try {
      String currentDirectory = System.getProperty("user.dir");
      Path filePath = Paths.get(currentDirectory, fileName);
      //      Files.createFile(filePath);
      Files.write(
          filePath,
          Collections.singletonList(output),
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static int calculateSumPart1(String input) {
    return input
        .lines()
        .map(Solution::extractCalibrationValuesPart1)
        .mapToInt(Integer::intValue)
        .sum();
  }

  private static Integer extractCalibrationValuesPart1(String line) {
    int firstPos = -1, lastPos = -1;
    char[] charArr = line.toCharArray();
    for (int i = 0; i < charArr.length; i++) {
      if (Character.isDigit(charArr[i])) {
        if (firstPos < 0) {
          firstPos = i;
        }

        lastPos = i;
      }
    }

    if (firstPos < 0) {
      throw new IllegalArgumentException("No digit found on line: " + line);
    }

    return (Character.getNumericValue(charArr[firstPos]) * 10
        + Character.getNumericValue(charArr[lastPos]));
  }

  private static int calculateSumPart2(String input) {
    return input
        .lines()
        .map(Solution::extractCalibrationValuesPart2)
        .mapToInt(Integer::intValue)
        .sum();
  }

  // this for sure can be done with some regex, but it was nice to use record for the solution
  private static Integer extractCalibrationValuesPart2(String line) {
    PositionAndValueDigitPair first = new PositionAndValueDigitPair(-1, -1);
    PositionAndValueDigitPair last = new PositionAndValueDigitPair(-1, -1);

    char[] charArr = line.toCharArray();
    for (int i = 0; i < charArr.length; i++) {
      if (Character.isDigit(charArr[i])) {
        PositionAndValueDigitPair positionAndValueDigitPair =
            new PositionAndValueDigitPair(i, Character.getNumericValue(charArr[i]));
        if (first.index < 0) {
          first = positionAndValueDigitPair;
        }

        last = positionAndValueDigitPair;
      }
    }

    for (int i = 0; i < ALPHABETICAL_DIGITS.size(); i++) {
      String alphabeticalDigit = ALPHABETICAL_DIGITS.get(i);
      int value = i + 1;
      int alphabeticalDigitIndex = line.indexOf(alphabeticalDigit);
      int alphabeticalDigitLastIndex = line.lastIndexOf(alphabeticalDigit);

      if (alphabeticalDigitIndex != -1
          && (alphabeticalDigitIndex < first.index || first.index == -1)) {
        first = new PositionAndValueDigitPair(alphabeticalDigitIndex, value);
      }

      if (alphabeticalDigitIndex != -1 && alphabeticalDigitIndex > last.index) {
        last = new PositionAndValueDigitPair(alphabeticalDigitIndex, value);
      }
      if (alphabeticalDigitLastIndex != -1 && alphabeticalDigitLastIndex > last.index) {
        last = new PositionAndValueDigitPair(alphabeticalDigitLastIndex, value);
      }
    }

    if (first.index < 0 || last.index < 0) {
      throw new IllegalArgumentException("No digit found on line: " + line);
    }

    return first.value * 10 + last.value;
  }
}
