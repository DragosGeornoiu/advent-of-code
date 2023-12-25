package ro.dragos.geornoiu.year2023.day04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Solution {

  private static final String RELATIVE_PATH_TO_CUR_DIR = "src/main/java/ro/dragos/geornoiu/year2023/day04/";
  private static final String FILENAME_OUTPUT_PART_1 = "outputPart1.txt";
  private static final String FILENAME_OUTPUT_PART_2 = "outputPart2.txt";
  private static final String FILENAME_INPUT = "input.txt";

  record CardGame(int id, List<Integer> winningNumbers, List<Integer> actualNumbers) {
      public List<Integer> getValidNumbers() {
        return actualNumbers.stream().filter(winningNumbers::contains).collect(Collectors.toList());
      }
  }

  public static void main(String[] args) {
    List<CardGame> cardGames = parseInput();

    int totalPoints = calculateTotalPoints(cardGames);
    System.out.println("Total Points: " + totalPoints);
    writeOutputToFile(String.valueOf(totalPoints), FILENAME_OUTPUT_PART_1);

    int totalScratchcards = calculateTotalScratchcards(cardGames);
    System.out.println("Total scratchcards: " + totalScratchcards);
    writeOutputToFile(String.valueOf(totalScratchcards), FILENAME_OUTPUT_PART_2);

  }

  private static int calculateTotalPoints(List<CardGame> cardGames) {
    return cardGames.stream().map(CardGame::getValidNumbers)
        .map(validNumbers -> (int) Math.pow(2, validNumbers.size() - 1))
        .mapToInt(Integer::intValue)
        .sum();
  }

  private static int calculateTotalScratchcards(List<CardGame> cardGames) {
    List<Integer> totalScratchcards = new ArrayList<>(Collections.nCopies(cardGames.size(), 1));
    for(int index=0; index<cardGames.size(); index++) {
      int scratchcardsWon = cardGames.get(index).getValidNumbers().size();

      for(int j=index+1; j<=index+scratchcardsWon; j++) {
        Integer scratchcards = totalScratchcards.get(j);
        totalScratchcards.set(j, scratchcards + totalScratchcards.get(index));
      }
    }

    return totalScratchcards.stream().mapToInt(Integer::intValue).sum();
  }

  private static List<CardGame> parseInput() {
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

                int gameId = Integer.parseInt(gameIdAndGamesSplit[0].replaceAll("Card", "").trim());
                String[] gamesSplit = gameIdAndGamesSplit[1].split("\\|");

                if (gamesSplit.length != 2) {
                  throw new RuntimeException(
                      String.format("Invalid input, cannot correctly split line %s to determine winning and actual numbers", line));
                }

                List<Integer> winningNumbers = Arrays.stream(gamesSplit[0].split(" "))
                    .filter(str -> !str.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
                List<Integer> actualNumbers = Arrays.stream(gamesSplit[1].split(" "))
                    .filter(str -> !str.isEmpty())
                    .map(Integer::parseInt)
                    .collect(
                    Collectors.toList());

                return new CardGame(gameId, winningNumbers, actualNumbers);
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
