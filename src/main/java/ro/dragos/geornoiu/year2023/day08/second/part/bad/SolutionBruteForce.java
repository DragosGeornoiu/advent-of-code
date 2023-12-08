package ro.dragos.geornoiu.year2023.day08.second.part.bad;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// does not work to brute force to a solution
public class SolutionBruteForce {
  public record Node(String name, String left, String right) {}

  public record Maze(String instructions, List<Node> nodes) {}

  public static void main(String[] args) {
    Maze maze = readInputFirstPart();
    long stepsFirstPart = calculateNoOfSteps(maze);
    System.out.println("No of steps first part: " + stepsFirstPart);
  }

  private static long calculateNoOfSteps(Maze maze) {
    Map<String, Node> nodesByName =
        maze.nodes.stream().collect(Collectors.toMap(Node::name, Function.identity()));

    long noOfSteps = 0;
    char[] instructions = maze.instructions.toCharArray();
    int charIndex = 0;

    List<Node> currentNodes = getAllStartingNodes(nodesByName);
    while (!areAllNodesAtEnd(currentNodes)) {
      char ch = instructions[charIndex];

      currentNodes =
          ch == 'L'
              ? navigateLeft(nodesByName, currentNodes)
              : navigateRight(nodesByName, currentNodes);

      noOfSteps++;
      charIndex++;
      if (charIndex == instructions.length) {
        charIndex = 0;
      }
    }

    return noOfSteps;
  }

  private static List<Node> navigateLeft(Map<String, Node> nodesByName, List<Node> currentNodes) {
    return currentNodes.stream()
        .map(currentNode -> nodesByName.get(currentNode.left))
        .collect(Collectors.toList());
  }

  private static List<Node> navigateRight(Map<String, Node> nodesByName, List<Node> currentNodes) {
    return currentNodes.stream()
        .map(currentNode -> nodesByName.get(currentNode.right))
        .collect(Collectors.toList());
  }

  private static List<Node> getAllStartingNodes(Map<String, Node> nodesByName) {
    return nodesByName.keySet().stream()
        .filter(key -> key.endsWith("A"))
        .map(nodesByName::get)
        .collect(Collectors.toList());
  }

  private static boolean areAllNodesAtEnd(List<Node> currentNodes) {
    return currentNodes.stream().allMatch(node -> node.name.endsWith("Z"));
  }

  private static Maze readInputFirstPart() {
    String instructions = null;
    List<Node> nodes = new ArrayList<>();
    try {
      BufferedReader br =
          new BufferedReader(
              new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day08/input.txt"));

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
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    return new Maze(instructions, nodes);
  }
}
