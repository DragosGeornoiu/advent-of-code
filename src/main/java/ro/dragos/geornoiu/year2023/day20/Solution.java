package ro.dragos.geornoiu.year2023.day20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class Solution {

  public static void main(String[] args) {
    Machine machine = Machine.build(readInput().lines().map(Node::parse).toList());

    long resultPart1 = part1(machine);
    System.out.println("Result first part: " + resultPart1);

    machine.reset();

    long resultPart2 = part2(machine);
    System.out.println("Result second part: " + resultPart2);
  }

  private static long part1(Machine machine) {
    for (int i = 0; i < 1000; ++i) {
      machine.push();
    }
    return machine.count();
  }

  private static long part2(Machine machine) {
    var broadcaster = machine.setup().get(Node.BROADCASTER);
    var originalOutputs = broadcaster.getOutputs();
    var toRxSink =
        machine.setup().entrySet().stream()
            .filter(e -> e.getValue().getOutputs().contains("rx"))
            .findAny()
            .orElseThrow()
            .getValue();
    long result = 1L;
    for (String s : originalOutputs) {
      toRxSink.getInputs().keySet().forEach(k -> toRxSink.getInputs().put(k, Pulse.HIGH));
      broadcaster.setOutputs(List.of(s));
      long counter = 1L;
      while (!machine.push()) {
        ++counter;
      }
      machine.reset();
      result = lcm(result, counter);
    }
    broadcaster.setOutputs(originalOutputs);
    return result;
  }

  /** Calculates the least common multiple of two numbers. */
  private static long lcm(long a, long b) {
    return a / gcf(a, b) * b;
  }

  /** Calculates the greatest common factor of two numbers using the Euclidean algorithm. */
  private static long gcf(long a, long b) {
    if (b == 0) {
      return a;
    } else {
      return gcf(b, a % b);
    }
  }

  private static String readInput() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day20/input.txt"))) {
      return br.lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    return StringUtils.EMPTY;
  }
}
