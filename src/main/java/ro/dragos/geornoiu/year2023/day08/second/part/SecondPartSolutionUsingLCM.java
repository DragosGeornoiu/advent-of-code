package ro.dragos.geornoiu.year2023.day08.second.part;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

// Using LCM
public class SecondPartSolutionUsingLCM {
  public record Node(String name, String left, String right) {}

  public record Maze(String instructions, List<Node> nodes) {}

  public static void main(String[] args) throws IOException {
    Maze maze = readInputSecondPart();

    Map<String, Node> nodesByName =
        maze.nodes.stream().collect(Collectors.toMap(Node::name, Function.identity()));

    List<Node> startNodes = getAllStartingNodes(nodesByName);

    Set<Node> startSet = new HashSet<>(startNodes);
    List<Long> countedSteps = new ArrayList<>();

    for (Node n : startSet) {
      countedSteps.add(countSteps(n, maze.instructions, nodesByName));
    }

    long stepsSecondPart = computeLCM(countedSteps);
    System.out.println("No of steps second part: " + stepsSecondPart);
  }

  public static String navigate(String current, String instruction, Map<String, Node> nodesByName) {
    Node currentNode = nodesByName.get(current);
    return instruction.equals("R") ? currentNode.right : currentNode.left;
  }

  public static long countSteps(Node node, String instructions, Map<String, Node> nodesByName) {
    String current = node.name;
    long steps = 0;
    int instructionIndex = 0;

    while (!current.endsWith("Z")) {
      String instruction =
          instructions.charAt(instructionIndex % instructions.length()) == 'R' ? "R" : "L";
      current = navigate(current, instruction, nodesByName);
      steps++;
      instructionIndex++;
    }

    return steps;
  }

  public static long lcm(long a, long b) {
    if (b == 0) {
      return a;
    }
    return lcm(b, a % b);
  }

  public static long computeLCM(List<Long> arr) {
    long lcm = arr.get(0);
    for (int i = 1; i < arr.size(); i++) {
      long num1 = lcm;
      long num2 = arr.get(i);
      long gcd_val = lcm(num1, num2);
      lcm = (lcm * arr.get(i)) / gcd_val;
    }
    return lcm;
  }

  private static Maze readInputSecondPart() {
    String instructions = null;
    List<Node> nodes = new ArrayList<>();
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day08/input.txt"))) {

      List<String> lines = br.lines().toList();

      instructions = lines.get(0);

      nodes =
          lines.stream()
              .skip(1)
              .filter(str -> !str.isEmpty())
              .map(
                  str -> {
                    String[] nameAndDirections = str.split("=");
                    String name = nameAndDirections[0].trim();
                    String[] directions = nameAndDirections[1].split(",");
                    String left = directions[0].replaceAll("\\(", "").trim();
                    String right = directions[1].replaceAll("\\)", "").trim();

                    return new Node(name, left, right);
                  })
              .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new Maze(instructions, nodes);
  }

  private static List<Node> getAllStartingNodes(Map<String, Node> nodesByName) {
    return nodesByName.keySet().stream()
        .filter(key -> key.endsWith("A"))
        .map(nodesByName::get)
        .collect(Collectors.toList());
  }
}
