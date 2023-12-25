package ro.dragos.geornoiu.year2023.day25;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Solution {

  private record Edge(String from, String to) {}

  public static void main(String[] args) {
    List<String> lines = readInputAsLines();

    int resultPart1 = solvePart1(lines);
    System.out.println("Result first part: " + resultPart1);
  }

  private static int solvePart1(List<String> lines) {
    Set<String> vertices = new HashSet<>();
    List<Edge> edges = new ArrayList<>();

    for (String line : lines) {
      String[] parts = line.split(": ");

      String component = parts[0];
      vertices.add(component);

      String[] connections = parts[1].split(" ");
      for (String connection : connections) {
        vertices.add(connection);
        edges.add(new Edge(connection, component));
      }
    }

    List<List<String>> groups = getGroups(vertices, edges);

    int product = 1;
    for (List<String> group : groups) {
      product *= group.size();
    }
    return product;
  }

  private static List<List<String>> getGroups(Set<String> vertices, List<Edge> edges) {
    List<List<String>> groups;
    do {
      groups = new ArrayList<>();
      for (String vertex : vertices) {
        List<String> group = new ArrayList<>();
        group.add(vertex);
        groups.add(group);
      }

      while (groups.size() > 2) {
        Edge randomEdge = edges.get(new Random().nextInt(edges.size()));

        List<String> group1 =
            groups.stream().filter(group -> group.contains(randomEdge.from)).findFirst().get();
        List<String> group2 =
            groups.stream().filter(group -> group.contains(randomEdge.to)).findFirst().get();

        if (group1.equals(group2)) {
          continue;
        }

        groups.remove(group2);
        group1.addAll(group2);
      }

    } while (getDisconnectionCount(groups, edges) != 3);

    return groups;
  }

  private static int getDisconnectionCount(List<List<String>> groups, List<Edge> edges) {
    int disconnectionCount = 0;
    for (int i = 0; i < edges.size(); i++) {
      Edge edge = edges.get(i);
      List<String> group1 =
          groups.stream().filter(group -> group.contains(edge.from())).findFirst().get();
      List<String> group2 =
          groups.stream().filter(group -> group.contains(edge.to())).findFirst().get();
      if (!group1.equals(group2)) {
        disconnectionCount++;
      }
    }
    return disconnectionCount;
  }

  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day25/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }
}
