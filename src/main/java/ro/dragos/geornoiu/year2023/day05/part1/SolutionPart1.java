package ro.dragos.geornoiu.year2023.day05.part1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolutionPart1 {

  public static void main(String[] args) {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day05/input.txt"))) {

      Map<String, Converter> converters = new HashMap<>();
      List<Long> seeds = new ArrayList<>();
      Converter currentConverter = null;
      for (String line : br.lines().toList()) {
        if (line.isBlank()) {
          continue;
        }
        if (line.startsWith("seeds:")) {
          for (String seed : line.replaceAll("seeds:", "").trim().split(" ")) {
            seeds.add(Long.parseLong(seed));
          }
        } else {
          if (line.matches("^\\d.*")) {
            currentConverter.addRange(line);
          } else {
            String[] parts = line.split("-");
            currentConverter = new Converter(parts[0], parts[2].replaceAll("map:", "").trim());
            converters.put(parts[0], currentConverter);
          }
        }
      }

      long smallestValue = Long.MAX_VALUE;

      for (long seed : seeds) {
        String destination = "seed";
        long value = seed;
        while (!destination.equals("location")) {
          currentConverter = converters.get(destination);
          destination = currentConverter.getDestination();
          value = currentConverter.getDestinationValue(value);
        }

        if (value < smallestValue) {
          smallestValue = value;
        }
      }

      System.out.println("smallest value: " + smallestValue);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
