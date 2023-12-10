package ro.dragos.geornoiu.year2023.day09;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

  public static void main(String[] args) {
    List<String> lines = readInputAsLines();

    long sumOfNextValues =
        lines.stream().map(Solution::getNextValue).mapToLong(Long::longValue).sum();
    System.out.println("Result first part: " + sumOfNextValues);

    long sumOfPreviousValues =
        lines.stream().map(Solution::getPrevValue).mapToLong(Long::longValue).sum();
    System.out.println("Result second part: " + sumOfPreviousValues);
  }

  private static Long getNextValue(String line) {
    List<List<Long>> diffList = computeDiffList(line);

    return computeNextValue(diffList);
  }

  private static List<List<Long>> computeDiffList(String line) {
    List<List<Long>> diffList = new ArrayList<>();
    diffList.add(parseLine(line));

    boolean allEntriesZero = false;
    int rowIndex = 0;
    while (!allEntriesZero) {
      List<Long> previousValues = diffList.get(rowIndex);
      List<Long> computedValues = new ArrayList<>();
      for (int i = 0; i < diffList.get(rowIndex).size() - 1; i++) {
        long computeVal = previousValues.get(i + 1) - previousValues.get(i);
        computedValues.add(i, computeVal);
      }

      allEntriesZero = areAllEntriesZero(computedValues);

      rowIndex++;
      diffList.add(rowIndex, computedValues);
    }
    return diffList;
  }

  private static Long getPrevValue(String line) {
    List<List<Long>> diffList = computeDiffList(line);
    return computePrevValue(diffList);
  }

  private static Long computeNextValue(List<List<Long>> diffList) {
    long increment = 0;
    for (int i = diffList.size() - 1; i >= 0; i--) {
      List<Long> currRow = diffList.get(i);
      increment += currRow.get(currRow.size() - 1);
    }

    return increment;
  }

  private static Long computePrevValue(List<List<Long>> diffList) {
    long decrement = 0;
    for (int i = diffList.size() - 1; i >= 0; i--) {
      List<Long> currRow = diffList.get(i);
      decrement = currRow.get(0) - decrement;
    }

    return decrement;
  }

  private static boolean areAllEntriesZero(List<Long> computedValues) {
    return computedValues.stream().allMatch(val -> val == 0L);
  }

  private static List<Long> parseLine(String line) {
    return Arrays.stream(line.split(" ")).map(Long::parseLong).collect(Collectors.toList());
  }

  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day09/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Collections.emptyList();
  }
}
