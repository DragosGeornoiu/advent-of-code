package ro.dragos.geornoiu.year2023.day10;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Node {

  public char c;
  private int x;
  private int y;

  public Integer distance = Integer.MAX_VALUE;
  public List<Node> shortestPath = new LinkedList<>();
  public Map<Node, Integer> adjacentNodes = new HashMap<>();

  public Node(char c, int x, int y) {
    this.c = c;
    this.x = x;
    this.y = y;
  }

  public void process(Node[][] graph) {
    switch (c) {
      case '|':
        addAdjacentNode(getUp(graph));
        addAdjacentNode(getDown(graph));
        break;
      case '-':
        addAdjacentNode(getLeft(graph));
        addAdjacentNode(getRight(graph));
        break;
      case 'J':
        addAdjacentNode(getLeft(graph));
        addAdjacentNode(getUp(graph));
        break;
      case 'L':
        addAdjacentNode(getRight(graph));
        addAdjacentNode(getUp(graph));
        break;
      case 'F':
        addAdjacentNode(getDown(graph));
        addAdjacentNode(getRight(graph));
        break;
      case '7':
        addAdjacentNode(getLeft(graph));
        addAdjacentNode(getDown(graph));
        break;
      case '.':
      case 'S':
        break;
      default:
        throw new IllegalStateException();
    }
  }

  private void addAdjacentNode(Node node) {
    if (node != null) adjacentNodes.put(node, 1);
  }

  public Node getUp(Node[][] graph) {
    return y > 0 ? graph[x][y - 1] : null;
  }

  public Node getDown(Node[][] graph) {
    return y < graph[0].length - 1 ? graph[x][y + 1] : null;
  }

  public Node getLeft(Node[][] graph) {
    return x > 0 ? graph[x - 1][y] : null;
  }

  public Node getRight(Node[][] graph) {
    return x < graph.length - 1 ? graph[x + 1][y] : null;
  }
}
