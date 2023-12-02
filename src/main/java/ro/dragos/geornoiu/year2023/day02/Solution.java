package ro.dragos.geornoiu.year2023.day02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

  private static final String RELATIVE_PATH_TO_CUR_DIR = "src/main/java/ro/dragos/geornoiu/year2023/day02/";
  private static final String FILENAME_OUTPUT_PART_1 = "outputPart1.txt";
  private static final String FILENAME_OUTPUT_PART_2 = "outputPart2.txt";
  private static final String FILENAME_INPUT = "input.txt";
  private static final String COLOUR_RED = "red";
  private static final String COLOUR_BLUE = "blue";
  private static final String COLOUR_GREEN = "green";

  record GameEntry(int red, int blue, int green) {
      public boolean isValid(GameEntry limitGame) {
          return this.red <= limitGame.red && this.blue <= limitGame.blue && this.green <= limitGame.green;
      }
  }

  record Game(int id, List<GameEntry> gameEntries) {}

  public static void main(String[] args) {
    List<Game> games = parseInput();

    GameEntry referenceLimits = new GameEntry(12, 14, 13);
    int sumPart1 = calculateSumOfValidGames(games, referenceLimits);
    System.out.println(sumPart1);
    writeOutputToFile(String.valueOf(sumPart1), FILENAME_OUTPUT_PART_1);

    int sumPart2 = calSumOfCubePowers(games);
    System.out.println(sumPart2);
    writeOutputToFile(String.valueOf(sumPart2), FILENAME_OUTPUT_PART_2);
  }

  private static int calculateSumOfValidGames(List<Game> games, GameEntry limitGame) {
        return games.stream()
                .filter(game -> game.gameEntries.stream()
                        .allMatch(gameEntry -> gameEntry.isValid(limitGame)))
                .map(Game::id)
                .mapToInt(Integer::intValue)
                .sum();
  }

  private static List<Game> parseInput() {
      String input = readInputFromFile(FILENAME_INPUT);
      return input
          .lines()
          .map(
              line -> {
                String[] gameIdAndGamesSplit = line.split(":");
                if (gameIdAndGamesSplit.length != 2) {
                  throw new RuntimeException(
                      String.format("Invalid input, cannot correctly split line %s", line));
                }

                int gameId = Integer.parseInt(gameIdAndGamesSplit[0].replaceAll("Game", "").trim());
                String[] gamesSplit = gameIdAndGamesSplit[1].split(";");
                List<GameEntry> gameEntries =
                    Arrays.stream(gamesSplit)
                        .map(Solution::parseGameEntryInput)
                        .collect(Collectors.toList());

                return new Game(gameId, gameEntries);
              })
          .collect(Collectors.toList());
}

  private static String readInputFromFile(String fileName) {
      try {
          String currentDirectory = System.getProperty("user.dir");
          Path filePath = Paths.get(currentDirectory, RELATIVE_PATH_TO_CUR_DIR + fileName);
          return Files.readString(filePath);
      } catch (IOException e) {
          throw new RuntimeException("Error reading file: " + RELATIVE_PATH_TO_CUR_DIR + fileName, e);
      }
  }

  private static GameEntry parseGameEntryInput(String gameEntry) {
      String[] globeColEntries = gameEntry.split(",");
      int noRedGlobes = 0, noBlueGlobes = 0, noGreenGlobes = 0;
      for (String globeColEntry : globeColEntries) {
        if (globeColEntry.contains(COLOUR_RED)) {
          noRedGlobes =
              Integer.parseInt(globeColEntry.replaceAll(COLOUR_RED, "").trim());
        }
        if (globeColEntry.contains(COLOUR_BLUE)) {
          noBlueGlobes =
              Integer.parseInt(globeColEntry.replaceAll(COLOUR_BLUE, "").trim());
        }
        if (globeColEntry.contains(COLOUR_GREEN)) {
          noGreenGlobes =
              Integer.parseInt(globeColEntry.replaceAll(COLOUR_GREEN, "").trim());
        }
      }

      return new GameEntry(noRedGlobes, noBlueGlobes, noGreenGlobes);
  }

  private static int calSumOfCubePowers(List<Game> games) {
      return games.stream()
          .map(Solution::calculateSumOfCubePowers)
          .mapToInt(Integer::intValue)
          .sum();
  }

  private static int calculateSumOfCubePowers(Game game) {
      int minRed = 0, minBlue = 0, minGreen = 0;
      for(GameEntry gameEntry : game.gameEntries) {
          if(gameEntry.red > minRed) {
              minRed = gameEntry.red;
          }
          if(gameEntry.blue > minBlue) {
              minBlue = gameEntry.blue;
          }
          if(gameEntry.green > minGreen) {
              minGreen = gameEntry.green;
          }
      }

      return minRed * minBlue * minGreen;
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
