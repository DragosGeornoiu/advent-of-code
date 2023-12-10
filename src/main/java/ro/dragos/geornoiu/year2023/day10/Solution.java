package ro.dragos.geornoiu.year2023.day10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Solution {

  public static void main(String[] args) throws IOException {
    List<String> lines = readInputAsLines();

    int height = lines.size();
    int width = lines.get(0).length();

    Node[][] graphArray = new Node[width][height];
    List<Node> allNodes = new ArrayList<>();

    Node startNode = null;
    for (int y = 0; y < lines.size(); y++) {
      String line = lines.get(y);
      for (int x = 0; x < line.length(); x++) {
        Node node = new Node(line.charAt(x), x, y);
        graphArray[x][y] = node;
        allNodes.add(node);

        if (line.charAt(x) == 'S') {
          startNode = graphArray[x][y];
        }
      }
    }

    allNodes.forEach(n -> n.process(graphArray));
    allNodes.forEach(
        n -> {
          for (Node other : new ArrayList<>(n.adjacentNodes.keySet())) {
            if (other.c == 'S') other.adjacentNodes.put(n, 1);
          }
        });

    Graph graph = computeGraphWithDijkstra(new Graph(allNodes), startNode);

    int farthestDistance =
        allNodes.stream()
            .mapToInt(n -> n.distance)
            .filter(d -> d != Integer.MAX_VALUE)
            .max()
            .orElseThrow();
    System.out.println("Result first part: " + farthestDistance);

    long noOfNodesEnclosedByLoop =
        getNoOfNodesEnclosedByLoopUsingScanlineAlgorithm(height, width, graphArray, startNode);
    System.out.println("Result second part: " + noOfNodesEnclosedByLoop);
  }

  private static long getNoOfNodesEnclosedByLoopUsingScanlineAlgorithm(
      int height, int width, Node[][] graphArray, Node startNode) {
    mutateStartNodeWithPipe(graphArray, startNode);

    HashSet<Node> enclosed = new HashSet<>();
    for (int y = 0; y < height; y++) {
      HashSet<Node> lineInside = new HashSet<>();
      boolean isInside = false;
      char enterChar = ' ';
      for (int x = 0; x < width; x++) {
        Node node = graphArray[x][y];
        if (node.distance != Integer.MAX_VALUE) {
          if (enterChar == ' ') enterChar = node.c;
          if (node.c == '|') {
            isInside = !isInside;
            enterChar = ' ';
          }
          if (node.c == 'J' && enterChar == 'F') {
            isInside = !isInside;
            enterChar = ' ';
          }
          if (node.c == 'J' && enterChar == 'L') {
            enterChar = ' ';
          }
          if (node.c == '7' && enterChar == 'L') {
            isInside = !isInside;
            enterChar = ' ';
          }
          if (node.c == '7' && enterChar == 'F') {
            enterChar = ' ';
          }

          if (!lineInside.isEmpty()) {
            enclosed.addAll(lineInside);
            lineInside.clear();
          }
        }
        if (node.distance == Integer.MAX_VALUE && isInside) {
          lineInside.add(node);
          enterChar = ' ';
        }
      }
    }
    return enclosed.size();
  }

  private static void mutateStartNodeWithPipe(Node[][] graphArray, Node startNode) {
    if (startNode.adjacentNodes.containsKey(startNode.getUp(graphArray))) {
      if (startNode.adjacentNodes.containsKey(startNode.getDown(graphArray))) {
        startNode.c = '|';
      }
      if (startNode.adjacentNodes.containsKey(startNode.getLeft(graphArray))) {
        startNode.c = 'J';
      }
      if (startNode.adjacentNodes.containsKey(startNode.getRight(graphArray))) {
        startNode.c = 'L';
      }
    } else if (startNode.adjacentNodes.containsKey(startNode.getDown(graphArray))) {
      if (startNode.adjacentNodes.containsKey(startNode.getLeft(graphArray))) {
        startNode.c = '7';
      }
      if (startNode.adjacentNodes.containsKey(startNode.getRight(graphArray))) {
        startNode.c = 'F';
      }
    } else {
      startNode.c = '-';
    }
  }

  public static Graph computeGraphWithDijkstra(Graph graph, Node source) {
    source.distance = 0;

    Set<Node> settledNodes = new HashSet<>();
    Set<Node> unsettledNodes = new HashSet<>();

    unsettledNodes.add(source);

    while (!unsettledNodes.isEmpty()) {
      Node currentNode = getLowestDistanceNode(unsettledNodes);
      unsettledNodes.remove(currentNode);
      for (Entry<Node, Integer> adjacencyPair : currentNode.adjacentNodes.entrySet()) {
        Node adjacentNode = adjacencyPair.getKey();
        Integer edgeWeight = adjacencyPair.getValue();
        if (!settledNodes.contains(adjacentNode)) {
          calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
          unsettledNodes.add(adjacentNode);
        }
      }
      settledNodes.add(currentNode);
    }
    return graph;
  }

  private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
    return unsettledNodes.stream().min(Comparator.comparingInt(n -> n.distance)).orElse(null);
  }

  public static void calculateMinimumDistance(
      Node evaluationNode, Integer edgeWeigh, Node sourceNode) {
    Integer sourceDistance = sourceNode.distance;
    if (sourceDistance + edgeWeigh < evaluationNode.distance) {
      evaluationNode.distance = sourceDistance + edgeWeigh;
      LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.shortestPath);
      shortestPath.add(sourceNode);
      evaluationNode.shortestPath = shortestPath;
    }
  }

  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day10/input.txt"))) {

      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Collections.emptyList();
  }
}
