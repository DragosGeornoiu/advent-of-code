package ro.dragos.geornoiu.year2023.day06;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

  public record Race(long time, long distance) {}

  public static void main(String[] args) {
    List<Race> races = readInputFirstPart();
    long resultFirstPart =
        races.stream().map(Solution::getNoOfSolutions).reduce(1L, (a, b) -> a * b);
    System.out.println("Result first part: " + resultFirstPart);

    Race race = readInputSecondPart();
    long resultSecondPart = getNoOfSolutions(race);
    System.out.println("Result second part: " + resultSecondPart);
  }

  //  Time:      7  15   30
  // Distance:  9  40  200
  private static long getNoOfSolutions(Race race) {
    long noOfSol = 0;

    for (long i = 0; i < race.time; i++) {
      if ((race.time - i) * i > race.distance) {
        noOfSol++;
      }
    }

    return noOfSol;
  }

  private static List<Race> readInputFirstPart() {
    List<Race> races = new ArrayList<>();
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day06/input.txt"))) {

      List<Long> times = new ArrayList<>();
      List<Long> distances = new ArrayList<>();
      for (String line : br.lines().toList()) {
        if (line.startsWith("Time: ")) {
          times =
              Arrays.stream(line.replace("Time: ", "").split(" "))
                  .filter(str -> !str.isEmpty())
                  .map(Long::parseLong)
                  .collect(Collectors.toList());
        }

        if (line.startsWith("Distance: ")) {
          distances =
              Arrays.stream(line.replace("Distance: ", "").split(" "))
                  .filter(str -> !str.isEmpty())
                  .map(Long::parseLong)
                  .collect(Collectors.toList());
        }
      }

      for (int i = 0; i < times.size(); i++) {
        races.add(new Race(times.get(i), distances.get(i)));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return races;
  }

  private static Race readInputSecondPart() {
    long raceTime = 0;
    long raceDistance = 0;

    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day06/input.txt"))) {

      for (String line : br.lines().toList()) {
        if (line.startsWith("Time: ")) {
          raceTime = Long.parseLong(line.replace("Time: ", "").replaceAll(" ", ""));
        }

        if (line.startsWith("Distance: ")) {
          raceDistance = Long.parseLong(line.replace("Distance: ", "").replaceAll(" ", ""));
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    }

    return new Race(raceTime, raceDistance);
  }
}
