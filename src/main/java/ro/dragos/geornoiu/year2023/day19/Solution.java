package ro.dragos.geornoiu.year2023.day19;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class Solution {

  private enum Condition {
    GREATER_THAN,
    LESS_THAN,
    NONE
  }

  private static class Instruction {
    String leftName;
    Condition condition;
    Long rightValue;
    String result;

    public boolean isSatisfied(final Part part) {
      if (condition == Condition.NONE) {
        return true;
      }
      long compareValue = 0;
      if (leftName.equals("x")) {
        compareValue = part.x;
      }
      if (leftName.equals("m")) {
        compareValue = part.m;
      }
      if (leftName.equals("a")) {
        compareValue = part.a;
      }
      if (leftName.equals("s")) {
        compareValue = part.s;
      }
      if (condition == Condition.GREATER_THAN && compareValue > rightValue) {
        return true;
      }

      if (condition == Condition.LESS_THAN && compareValue < rightValue) {
        return true;
      }

      return false;
    }
  }

  private static class InstructionSet {
    String name;
    List<Instruction> instructions = new ArrayList<>();

    public String getResult(final Part part) {
      for (final Instruction instruction : instructions) {
        if (instruction.isSatisfied(part)) {
          return instruction.result;
        }
      }
      return null;
    }
  }

  private static class Part {
    long x;
    long m;
    long a;
    long s;
  }

  private static class Range {
    long minimum;
    long maximum;

    public Range copy() {
      final Range range = new Range();
      range.minimum = minimum;
      range.maximum = maximum;
      return range;
    }

    public long getAmount() {
      return maximum - minimum + 1;
    }
  }

  private static class PartRanges {
    List<Range> xRanges = new ArrayList<>();
    List<Range> mRanges = new ArrayList<>();
    List<Range> aRanges = new ArrayList<>();
    List<Range> sRanges = new ArrayList<>();

    public PartRanges copy() {
      final PartRanges partRanges = new PartRanges();
      for (final Range range : xRanges) {
        partRanges.xRanges.add(range.copy());
      }
      for (final Range range : mRanges) {
        partRanges.mRanges.add(range.copy());
      }
      for (final Range range : aRanges) {
        partRanges.aRanges.add(range.copy());
      }
      for (final Range range : sRanges) {
        partRanges.sRanges.add(range.copy());
      }
      return partRanges;
    }

    public long getCombinationValue() {
      return sum(transformData(xRanges, Range::getAmount))
          * sum(transformData(mRanges, Range::getAmount))
          * sum(transformData(aRanges, Range::getAmount))
          * sum(transformData(sRanges, Range::getAmount));
    }

    public static List<Range> splitRangeOnGreaterThan(
        final long greaterThanValue, final List<Range> ranges) {
      final List<Range> newRanges = new ArrayList<>();
      for (final Range range : ranges) {
        if (range.maximum <= greaterThanValue) {
          continue;
        }
        if (range.minimum > greaterThanValue) {
          newRanges.add(range.copy());
          continue;
        }

        final Range newRange = new Range();
        newRange.minimum = greaterThanValue + 1;
        newRange.maximum = range.maximum;
        newRanges.add(newRange);
      }
      return newRanges;
    }

    public static List<Range> splitRangeOnLessThan(
        final long lessThanValue, final List<Range> ranges) {
      final List<Range> newRanges = new ArrayList<>();
      for (final Range range : ranges) {
        if (range.minimum >= lessThanValue) {
          continue;
        }

        if (range.maximum < lessThanValue) {
          newRanges.add(range.copy());
          continue;
        }

        final Range newRange = new Range();
        newRange.minimum = range.minimum;
        newRange.maximum = lessThanValue - 1;
        newRanges.add(newRange);
      }

      return newRanges;
    }
  }

  public static void main(String[] args) {
    long resultPart1 = sumOfRatingNumbers();
    System.out.println("Result first part: " + resultPart1);

    long resultPart2 = getDistinctCombinations();
    System.out.println("Result second part: " + resultPart2);
  }

  private static long sumOfRatingNumbers() {
    final String input = readInput();
    final Map<String, InstructionSet> instructionSetMap = getInstructionSetMap(input);
    final List<Part> parts = getParts(input);
    final List<Part> acceptedParts = new ArrayList<>();
    for (final Part part : parts) {
      String currentInstruction = "in";
      while (!currentInstruction.equals("A") && !currentInstruction.equals("R")) {
        final InstructionSet instructionSet = instructionSetMap.get(currentInstruction);
        currentInstruction = instructionSet.getResult(part);
      }
      if (currentInstruction.equals("A")) {
        acceptedParts.add(part);
      }
    }

    return sum(transformData(acceptedParts, part -> part.x + part.m + part.a + part.s));
  }

  private static Map<String, InstructionSet> getInstructionSetMap(final String input) {
    final List<String> halves = splitStringIntoList(input, "\n\n");
    final List<String> instructionLines = splitStringIntoList(halves.get(0), "\n");
    final Map<String, InstructionSet> instructionSetMap = new HashMap<>();
    for (final String instructionLine : instructionLines) {
      final InstructionSet instructionSet = new InstructionSet();
      instructionSet.name = splitStringIntoList(instructionLine, "{").get(0);
      final String target = splitStringIntoList(instructionLine, "{").get(1);
      final List<String> instructions =
          splitStringIntoList(target.substring(0, target.length() - "}".length()), ",");
      for (final String instructionString : instructions) {
        final Instruction instruction = new Instruction();
        if (instructionString.contains(">")) {
          instruction.leftName = splitStringIntoList(instructionString, ">").get(0);
          instruction.rightValue =
              Long.parseLong(
                  splitStringIntoList(splitStringIntoList(instructionString, ">").get(1), ":")
                      .get(0));
          instruction.result =
              splitStringIntoList(splitStringIntoList(instructionString, ">").get(1), ":").get(1);
          instruction.condition = Condition.GREATER_THAN;
        } else if (instructionString.contains("<")) {
          instruction.leftName = splitStringIntoList(instructionString, "<").get(0);
          instruction.rightValue =
              Long.parseLong(
                  splitStringIntoList(splitStringIntoList(instructionString, "<").get(1), ":")
                      .get(0));
          instruction.result =
              splitStringIntoList(splitStringIntoList(instructionString, "<").get(1), ":").get(1);
          instruction.condition = Condition.LESS_THAN;
        } else {
          instruction.condition = Condition.NONE;
          instruction.result = instructionString;
        }
        instructionSet.instructions.add(instruction);
      }
      instructionSetMap.put(instructionSet.name, instructionSet);
    }
    return instructionSetMap;
  }

  private static List<Part> getParts(final String input) {
    final List<String> halves = splitStringIntoList(input, "\n\n");
    final List<String> partLines = splitStringIntoList(halves.get(1), "\n");
    final List<Part> parts = new ArrayList<>();
    for (final String partValue : partLines) {
      final String target = partValue.substring(0, partValue.length() - "}".length());
      final int index = "{".length();
      List<String> values =
          splitStringIntoList(target.substring(index, index + target.length() - "{".length()), ",");
      final Part newPart = new Part();
      newPart.x = Long.parseLong(splitStringIntoList(values.get(0), "=").get(1));
      newPart.m = Long.parseLong(splitStringIntoList(values.get(1), "=").get(1));
      newPart.a = Long.parseLong(splitStringIntoList(values.get(2), "=").get(1));
      newPart.s = Long.parseLong(splitStringIntoList(values.get(3), "=").get(1));
      parts.add(newPart);
    }
    return parts;
  }

  private static long calculateAcceptedCombinations(
      final String instructionName,
      final PartRanges partRanges,
      final Map<String, InstructionSet> instructionSetMap,
      final List<String> instructionPath) {
    if (instructionName.equals("R")) {
      return 0;
    }
    if (instructionName.equals("A")) {
      return partRanges.getCombinationValue();
    }
    final List<Long> values = new ArrayList<>();
    PartRanges currentPartRanges = partRanges.copy();
    for (final Instruction instruction : instructionSetMap.get(instructionName).instructions) {
      if (instruction.condition == Condition.GREATER_THAN) {
        final PartRanges sendOffPartRanges = currentPartRanges.copy();
        final long mustBeGreaterThanValue = instruction.rightValue;
        if (instruction.leftName.equals("x")) {
          sendOffPartRanges.xRanges =
              PartRanges.splitRangeOnGreaterThan(mustBeGreaterThanValue, sendOffPartRanges.xRanges);
          currentPartRanges.xRanges =
              PartRanges.splitRangeOnLessThan(
                  mustBeGreaterThanValue + 1, currentPartRanges.xRanges);
        }
        if (instruction.leftName.equals("m")) {
          sendOffPartRanges.mRanges =
              PartRanges.splitRangeOnGreaterThan(mustBeGreaterThanValue, sendOffPartRanges.mRanges);
          currentPartRanges.mRanges =
              PartRanges.splitRangeOnLessThan(
                  mustBeGreaterThanValue + 1, currentPartRanges.mRanges);
        }
        if (instruction.leftName.equals("a")) {
          sendOffPartRanges.aRanges =
              PartRanges.splitRangeOnGreaterThan(mustBeGreaterThanValue, sendOffPartRanges.aRanges);
          currentPartRanges.aRanges =
              PartRanges.splitRangeOnLessThan(
                  mustBeGreaterThanValue + 1, currentPartRanges.aRanges);
        }
        if (instruction.leftName.equals("s")) {
          sendOffPartRanges.sRanges =
              PartRanges.splitRangeOnGreaterThan(mustBeGreaterThanValue, sendOffPartRanges.sRanges);
          currentPartRanges.sRanges =
              PartRanges.splitRangeOnLessThan(
                  mustBeGreaterThanValue + 1, currentPartRanges.sRanges);
        }
        final List<String> newInstructionPath = new ArrayList<>(instructionPath);
        newInstructionPath.add(instruction.result);
        values.add(
            calculateAcceptedCombinations(
                instruction.result, sendOffPartRanges, instructionSetMap, newInstructionPath));
      } else if (instruction.condition == Condition.LESS_THAN) {
        final PartRanges sendOffPartRanges = currentPartRanges.copy();
        final long mustBeLessThanValue = instruction.rightValue;
        if (instruction.leftName.equals("x")) {
          sendOffPartRanges.xRanges =
              PartRanges.splitRangeOnLessThan(mustBeLessThanValue, sendOffPartRanges.xRanges);
          currentPartRanges.xRanges =
              PartRanges.splitRangeOnGreaterThan(
                  mustBeLessThanValue - 1, currentPartRanges.xRanges);
        }
        if (instruction.leftName.equals("m")) {
          sendOffPartRanges.mRanges =
              PartRanges.splitRangeOnLessThan(mustBeLessThanValue, sendOffPartRanges.mRanges);
          currentPartRanges.mRanges =
              PartRanges.splitRangeOnGreaterThan(
                  mustBeLessThanValue - 1, currentPartRanges.mRanges);
        }
        if (instruction.leftName.equals("a")) {
          sendOffPartRanges.aRanges =
              PartRanges.splitRangeOnLessThan(mustBeLessThanValue, sendOffPartRanges.aRanges);
          currentPartRanges.aRanges =
              PartRanges.splitRangeOnGreaterThan(
                  mustBeLessThanValue - 1, currentPartRanges.aRanges);
        }
        if (instruction.leftName.equals("s")) {
          sendOffPartRanges.sRanges =
              PartRanges.splitRangeOnLessThan(mustBeLessThanValue, sendOffPartRanges.sRanges);
          currentPartRanges.sRanges =
              PartRanges.splitRangeOnGreaterThan(
                  mustBeLessThanValue - 1, currentPartRanges.sRanges);
        }
        final List<String> newInstructionPath = new ArrayList<>(instructionPath);
        newInstructionPath.add(instruction.result);
        values.add(
            calculateAcceptedCombinations(
                instruction.result, sendOffPartRanges, instructionSetMap, newInstructionPath));
      } else if (instruction.condition == Condition.NONE) {
        final List<String> newInstructionPath = new ArrayList<>(instructionPath);
        newInstructionPath.add(instruction.result);
        values.add(
            calculateAcceptedCombinations(
                instruction.result, currentPartRanges, instructionSetMap, newInstructionPath));
      }
    }

    return sum(values);
  }

  private static long getDistinctCombinations() {
    final String input = readInput();
    final Map<String, InstructionSet> instructionSetMap = getInstructionSetMap(input);
    final PartRanges partRanges = new PartRanges();
    final Range xRange = new Range();
    xRange.minimum = 1;
    xRange.maximum = 4000;
    partRanges.xRanges.add(xRange);
    final Range mRange = new Range();
    mRange.minimum = 1;
    mRange.maximum = 4000;
    partRanges.mRanges.add(mRange);
    final Range aRange = new Range();
    aRange.minimum = 1;
    aRange.maximum = 4000;
    partRanges.aRanges.add(aRange);
    final Range sRange = new Range();
    sRange.minimum = 1;
    sRange.maximum = 4000;
    partRanges.sRanges.add(sRange);
    final List<String> instructionPath = new ArrayList<>();
    instructionPath.add("in");

    return calculateAcceptedCombinations("in", partRanges, instructionSetMap, instructionPath);
  }

  public static long sum(final Collection<Long> valueList) {
    long sum = 0;
    for (final Long value : valueList) {
      sum += value;
    }
    return sum;
  }

  public static <T1, T2> List<T2> transformData(
      final List<T1> items, Function<T1, T2> transformFunction) {
    final List<T2> transformedList = new ArrayList<>();
    for (final T1 item : items) {
      transformedList.add(transformFunction.apply(item));
    }
    return transformedList;
  }

  public static List<String> splitStringIntoList(final String target, final String splitBy) {
    return new ArrayList<>(Arrays.asList(target.split(Pattern.quote(splitBy)).clone()));
  }

  private static String readInput() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day19/input.txt"))) {
      return br.lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return StringUtils.EMPTY;
  }
}
