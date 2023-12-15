package ro.dragos.geornoiu.year2023.day15;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {

  public static void main(String[] args) {

    // write first part also using
    List<String> input = readInputAsSingleString();

    int result = computeSumOfHashes(input);
    System.out.println("Result first part: " + result);

    result =
        input.stream()
            .map(s -> s.chars().reduce(0, (acc, c) -> ((acc + c) * 17) % 256))
            .mapToInt(Integer::intValue)
            .sum();
    System.out.println("Result first part done cleaner: " + result);

    int resultSecondPart = computeTotalFocusingPower(input);
    System.out.println("Result second part: " + resultSecondPart);
  }

  private static int computeTotalFocusingPower(List<String> inputs) {
    List<LinkedHashMap<String, Integer>> boxes = getBoxes(inputs);

    int result = 0;
    for (int i = 0; i < boxes.size(); i++) {
      LinkedHashMap<String, Integer> box = boxes.get(i);
      int boxEntryIndex = 1;
      for (Map.Entry<String, Integer> entry : box.entrySet()) {
        result += (i + 1) * boxEntryIndex * entry.getValue();
        boxEntryIndex++;
      }
    }

    return result;
  }

  private static List<LinkedHashMap<String, Integer>> getBoxes(List<String> inputs) {
    List<LinkedHashMap<String, Integer>> boxes = initializeBoxes(256);

    for (String input : inputs) {
      if (input.contains("-")) {
        String label = input.substring(0, input.indexOf('-'));
        int hash = computeHash(label);
        LinkedHashMap<String, Integer> currentBox = boxes.get(hash);
        currentBox.remove(label);
      } else {
        String[] parts = input.split("=");
        int hash = computeHash(parts[0]);
        LinkedHashMap<String, Integer> currentBox = boxes.get(hash);
        currentBox.put(parts[0], Integer.valueOf(parts[1]));
      }
    }
    return boxes;
  }

  private static List<LinkedHashMap<String, Integer>> initializeBoxes(int numberOfBoxes) {
    List<LinkedHashMap<String, Integer>> boxes = new ArrayList<>();

    for (int i = 0; i < numberOfBoxes; i++) {
      boxes.add(new LinkedHashMap<>());
    }

    return boxes;
  }

  private static int computeSumOfHashes(List<String> inputs) {
    return inputs.stream().map(Solution::computeHash).mapToInt(Integer::intValue).sum();
  }

  private static int computeHash(String input) {
    int result = 0;
    for (Character ch : input.toCharArray()) {
      result = ((result + ch) * 17) % 256;
    }

    return result;
  }

  private static List<String> readInputAsSingleString() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day15/input.txt"))) {
      String input = br.lines().collect(Collectors.joining());
      return Arrays.asList(input.split(","));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }
}
