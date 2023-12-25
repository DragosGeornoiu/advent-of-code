package ro.dragos.geornoiu.year2023.day05.part2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import ro.dragos.geornoiu.year2023.day05.part1.Converter;

public class SolutionPart2 {

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

      long minResult = getLowestLocation(converters, seeds);

      System.out.println("Smallest location overall: " + minResult);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static long getLowestLocation(Map<String, Converter> converters, List<Long> seeds) {
    ExecutorService fixedPool = Executors.newFixedThreadPool(10);

    List<Future<Long>> futures = new ArrayList<>();
    for (int i = 0; i < seeds.size(); i += 2) {
      long start = seeds.get(i);
      long len = seeds.get(i + 1);

      futures.add(fixedPool.submit(new TestThread(converters, start, len)));
    }

    fixedPool.shutdown();

    // Wait for all tasks to complete
    for (Future<Long> future : futures) {
      try {
        future.get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    // Now, collect the results and find the minimum
    long minResult = Long.MAX_VALUE;
    for (Future<Long> future : futures) {
      try {
        long result = future.get();
        minResult = Math.min(minResult, result);
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }
    return minResult;
  }
}
