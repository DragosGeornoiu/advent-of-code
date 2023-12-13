package ro.dragos.geornoiu.year2023.day13;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Solution {
  private record Reflection(long horizontal, long vertical) {}

  public static void main(String[] args) throws IOException {
    List<String> lines = readInputAsLines();
    List<List<String>> matrixes = computeMatrixes(lines);

    List<Reflection> reflections =
        matrixes.stream().map(Solution::computeMatrixReflection).collect(Collectors.toList());

    long result = calculateResult(reflections);
    System.out.println("Result first part: " + result);

    List<Reflection> newReflections = computeSmudgedReflections(matrixes, reflections);
    long resultSecondPart = calculateResult(newReflections);
    System.out.println("Result second part: " + resultSecondPart);
  }

  private static long calculateResult(List<Reflection> reflections) {
    return reflections.stream()
        .map(ref -> 100 * ref.horizontal + ref.vertical)
        .mapToLong(Long::longValue)
        .sum();
  }

  private static List<List<String>> computeMatrixes(List<String> lines) {
    List<List<String>> matrixes = new ArrayList<>();

    List<String> matrix = new ArrayList<>();
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).trim().isEmpty()) {
        matrixes.add(List.copyOf(matrix));
        matrix = new ArrayList<>();
      } else {
        matrix.add(lines.get(i));
      }
    }

    matrixes.add(List.copyOf(matrix));
    return matrixes;
  }

  public static List<String> transposeMatrix(List<String> originalMatrix) {
    int numRows = originalMatrix.size();
    int numCols = originalMatrix.get(0).length();

    return IntStream.range(0, numCols)
        .mapToObj(
            col ->
                IntStream.range(0, numRows)
                    .mapToObj(row -> originalMatrix.get(row).charAt(col))
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                    .toString())
        .collect(Collectors.toList());
  }

  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day13/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Collections.emptyList();
  }

  // part 1
  private static Reflection computeMatrixReflection(List<String> matrix) {
    int horizontalPatternIndex = computeReflection(matrix);

    if (horizontalPatternIndex != 0) {
      return new Reflection(horizontalPatternIndex, 0);
    }

    List<String> transposedMatrix = transposeMatrix(matrix);
    int verticalPatternIndex = computeReflection(transposedMatrix);
    return new Reflection(horizontalPatternIndex, verticalPatternIndex);
  }

  private static int computeReflection(List<String> matrix) {
    for (int i = 1; i < matrix.size(); i++) {
      if (matrix.get(i - 1).equals(matrix.get(i))) {
        if (validateIsReflection(matrix, i - 1)) {
          return i;
        }
      }
    }

    return 0;
  }

  private static boolean validateIsReflection(List<String> matrix, int backwardsIndex) {
    int forwardIndex = backwardsIndex + 1;
    while (backwardsIndex >= 0 && forwardIndex < matrix.size()) {
      if (!matrix.get(backwardsIndex).equals(matrix.get(forwardIndex))) {
        return false;
      }

      backwardsIndex--;
      forwardIndex++;
    }

    return true;
  }

  // part 2
  private static List<Reflection> computeSmudgedReflections(
      List<List<String>> matrixes, List<Reflection> originalReflections) {
    List<Reflection> newReflections = new ArrayList<>();

    for (int index = 0; index < originalReflections.size(); index++) {
      List<String> currMatrix = matrixes.get(index);
      Reflection currReflection = originalReflections.get(index);

      Reflection newReflection = computeSmudgedMatrixReflection(currMatrix, currReflection);
      if (newReflection != null) {
        newReflections.add(newReflection);
      }
    }

    return newReflections;
  }

  private static Reflection computeSmudgedMatrixReflection(
      List<String> currMatrix, Reflection currReflection) {
    for (int i = 0; i < currMatrix.size(); i++) {
      String line = currMatrix.get(i);
      for (int j = 0; j < line.length(); j++) {
        if (line.charAt(j) == '.' || line.charAt(j) == '#') {
          Character replacementChar = line.charAt(j) == '.' ? '#' : '.';
          List<String> newMatrix = getSmudgedMatrix(currMatrix, i, j, replacementChar);
          Reflection newReflection = computeSmudgedReflection(newMatrix, currReflection);
          if (newReflection.vertical != 0 || newReflection.horizontal != 0) {
            return newReflection;
          }
        }
      }
    }

    return null;
  }

  private static Reflection computeSmudgedReflection(
      List<String> matrix, Reflection prevReflection) {
    int horizontalPatternIndex = computeSmudgedReflection(matrix, prevReflection.horizontal);

    if (horizontalPatternIndex != 0) {
      return new Reflection(horizontalPatternIndex, 0);
    }

    List<String> transposedMatrix = transposeMatrix(matrix);
    int verticalPatternIndex = computeSmudgedReflection(transposedMatrix, prevReflection.vertical);
    return new Reflection(horizontalPatternIndex, verticalPatternIndex);
  }

  private static List<String> getSmudgedMatrix(
      List<String> currMatrix, int i, int j, Character replacement) {
    List<String> newMatrix = new ArrayList<>(currMatrix);
    StringBuilder modifiedRow = new StringBuilder(currMatrix.get(i));
    modifiedRow.setCharAt(j, replacement);
    newMatrix.set(i, modifiedRow.toString());

    return newMatrix;
  }

  private static int computeSmudgedReflection(List<String> matrix, long prevIndex) {
    for (int i = 1; i < matrix.size(); i++) {
      if (matrix.get(i - 1).equals(matrix.get(i))) {
        if (i != prevIndex && validateIsReflection(matrix, i - 1)) {
          return i;
        }
      }
    }

    return 0;
  }
}
