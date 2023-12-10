package ro.dragos.geornoiu.year2023.day10;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
  private Set<Node> nodes = new HashSet<>();

  public Graph(List<Node> allNodes) {
    nodes.addAll(allNodes);
  }

  public void addNode(Node nodeA) {
    nodes.add(nodeA);
  }
}
