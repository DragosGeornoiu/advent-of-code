package ro.dragos.geornoiu.year2023.day12.part2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {

  private static Map<String, Long> cache = new HashMap<>();

  public static void main(String[] args) {
    List<String> lines = readInputAsLines();

    long sumOfUnfoldedArrangements =
        lines.stream()
            .map(
                line -> {
                  String[] parts = line.split(" ");
                  String springConfig = parts[0];
                  int[] springsMapping = parseMapping(parts[1]);

                  springConfig =
                      String.join(
                          "?",
                          springConfig,
                          springConfig,
                          springConfig,
                          springConfig,
                          springConfig);
                  springsMapping = replicateArray(springsMapping, 5);

                  return countUnfoldedArrangements(springConfig, springsMapping);
                })
            .mapToLong(Long::longValue)
            .sum();

    System.out.println("Result second part: " + sumOfUnfoldedArrangements);
  }

  public static long countUnfoldedArrangements(String springConfig, int[] springsMapping) {
    if (springConfig.isEmpty()) {
      return (springsMapping.length == 0) ? 1 : 0;
    }

    if (springsMapping.length == 0) {
      return (springConfig.contains("#")) ? 0 : 1;
    }

    String key = springConfig + "," + arrayToString(springsMapping);

    if (cache.containsKey(key)) {
      return cache.get(key);
    }

    long result = 0;

    if (springConfig.charAt(0) == '.' || springConfig.charAt(0) == '?') {
      result += countUnfoldedArrangements(springConfig.substring(1), springsMapping);
    }

    if (springConfig.charAt(0) == '#' || springConfig.charAt(0) == '?') {
      if (springsMapping[0] <= springConfig.length()
          && !springConfig.substring(0, springsMapping[0]).contains(".")
          && (springsMapping[0] == springConfig.length()
              || springConfig.charAt(springsMapping[0]) != '#')) {
        result +=
            countUnfoldedArrangements(
                safeSubstring(springConfig, springsMapping[0] + 1), subArray(springsMapping, 1));
      }
    }

    cache.put(key, result);
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

  private static String arrayToString(int[] array) {
    StringBuilder builder = new StringBuilder();
    for (int num : array) {
      builder.append(num).append(",");
    }
    return builder.toString();
  }

  private static int[] parseMapping(String val) {
    String[] arr = val.split(",");
    int[] result = new int[arr.length];
    for (int i = 0; i < arr.length; i++) {
      result[i] = Integer.parseInt(arr[i]);
    }
    return result;
  }

  private static int[] replicateArray(int[] array, int times) {
    int[] result = new int[array.length * times];
    for (int i = 0; i < times; i++) {
      System.arraycopy(array, 0, result, i * array.length, array.length);
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
