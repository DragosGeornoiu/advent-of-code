package ro.dragos.geornoiu.year2023.day08.first.part;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Solution {

  private static final String START_NODE = "AAA";
  private static final String END_NODE = "ZZZ";

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
    Node currentNode = nodesByName.get(START_NODE);
    char[] instructions = maze.instructions.toCharArray();

    int charIndex = 0;
    while (!currentNode.name.equals(END_NODE)) {
      char ch = instructions[charIndex];

      currentNode =
          ch == 'L' ? nodesByName.get(currentNode.left) : nodesByName.get(currentNode.right);

      noOfSteps++;
      charIndex++;
      if (charIndex == instructions.length) {
        charIndex = 0;
      }
    }

    return noOfSteps;
  }

  private static Maze readInputFirstPart() {
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
}
