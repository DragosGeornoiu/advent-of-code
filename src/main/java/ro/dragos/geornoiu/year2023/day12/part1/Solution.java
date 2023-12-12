package ro.dragos.geornoiu.year2023.day12.part1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Solution {

  public static void main(String[] args) {
    List<String> lines = readInputAsLines();
    int sumOfArrangements =
        lines.stream()
            .map(
                line -> {
                  String[] parts = line.split(" ");
                  String springConfig = parts[0];
                  int[] springsMapping = parseMapping(parts[1]);
                  return countArrangements(springConfig, springsMapping);
                })
            .mapToInt(Integer::intValue)
            .sum();
    System.out.println("Result first part: " + sumOfArrangements);
  }

  public static int countArrangements(String springConfig, int[] springsMapping) {
    if (springConfig.isEmpty()) {
      return (springsMapping.length == 0) ? 1 : 0;
    }

    if (springsMapping.length == 0) {
      return (springConfig.contains("#")) ? 0 : 1;
    }

    int result = 0;

    if (springConfig.charAt(0) == '.' || springConfig.charAt(0) == '?') {
      result += countArrangements(springConfig.substring(1), springsMapping);
    }

    if (springConfig.charAt(0) == '#' || springConfig.charAt(0) == '?') {
      if (springsMapping[0] <= springConfig.length()
          && !springConfig.substring(0, springsMapping[0]).contains(".")
          && (springsMapping[0] == springConfig.length()
              || springConfig.charAt(springsMapping[0]) != '#')) {
        result +=
            countArrangements(safeSubstring(springConfig, springsMapping[0] + 1), subArray(springsMapping, 1));
      }
    }

    return result;
  }

  private static String safeSubstring(String str, int startIndex) {
    if (startIndex > str.length()) {
      return "";
    }

    return str.substring(startIndex);
  }

  private static int[] subArray(int[] array, int startIndex) {
    int length = array.length - startIndex;
    int[] subArray = new int[length];
    System.arraycopy(array, startIndex, subArray, 0, length);
    return subArray;
  }

  private static int[] parseMapping(String val) {
    String[] arr = val.split(",");
    int[] result = new int[arr.length];
    for (int i = 0; i < arr.length; i++) {
      result[i] = Integer.parseInt(arr[i]);
    }
    return result;
  }

  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day12/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Collections.emptyList();
  }
}
