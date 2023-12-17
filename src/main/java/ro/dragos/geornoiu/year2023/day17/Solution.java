package ro.dragos.geornoiu.year2023.day17;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Solution {
  private enum Direction {
    NORTH, EAST, SOUTH, WEST;
  }

  private record Position(int x, int y) {
    List<Position> getNextPositions(Direction dir) {
      return switch (dir) {
        case EAST -> List.of(new Position(x + 1, y), new Position(x, y - 1), new Position(x, y + 1));
        case WEST -> List.of(new Position(x - 1, y), new Position(x, y - 1), new Position(x, y + 1));
        case NORTH -> List.of(new Position(x, y - 1), new Position(x + 1, y), new Position(x - 1, y));
        case SOUTH -> List.of(new Position(x, y + 1), new Position(x + 1, y), new Position(x - 1, y));
      };
    }

    Direction directionOf(Position position) {
      if (y == position.y) {
        if (x < position.x) {
          return Direction.EAST;
        } else if (x > position.x) {
          return Direction.WEST;
        }
      } else if (x == position.x) {
        if (y < position.y) {
          return Direction.SOUTH;
        } else if (y > position.y) {
          return Direction.NORTH;
        }
      }

      throw new IllegalArgumentException();
    }
  }

  private record State(Position pos, int straightLength, int heatLoss, Direction dir) implements Comparable<State> {

    private static final Comparator<State> COMPARATOR = Comparator.comparingLong(State::heatLoss).thenComparingInt(State::straightLength);

    @Override
    public int compareTo(State o) {
      return COMPARATOR.compare(this, o);
    }

    int nextStraight(Position p) {
      if (pos.directionOf(p) == dir) {
        return straightLength + 1;
      }
      return 1;
    }

    long toKey() {
      return pos.x()*1_000_000L+pos.y()*1_000L+straightLength*10L+dir.ordinal();
    }
  }

  public static void main(String[] args) {
    List<String> matrix = readInputAsLines();

    int result = minHeatLoss(matrix, 3, (a, b) -> true, i -> true);
    System.out.println("Result first part: " + result);

    long resultPart2 = minHeatLoss(matrix, 10, (c, s) -> 4 <= s.straightLength() || s.pos().directionOf(c) == s.dir(), i -> 4<= i);
    System.out.println("Result second part: " + resultPart2);
  }

  private static int minHeatLoss(List<String> matrix, int maxLength, BiPredicate<Position, State> nextFilter, Predicate<Integer> stopFilter) {
    int height = matrix.size();
    int width = matrix.get(0).length();

    Predicate<Position> isValidCoordinate = c -> 0 <= c.x() && 0 <= c.y() && c.x < width && c.y < height;
    Predicate<State> isValidState = s -> isValidCoordinate.test(s.pos()) && s.straightLength() <= maxLength;
    Position target = new Position(width - 1, height - 1);
    Map<Long, Integer> bestCost = new HashMap<>();
    Queue<State> queue = new PriorityQueue<>();
    queue.add(new State(new Position(0, 0), 0, 0, Direction.EAST));

    while (!queue.isEmpty()) {
      State currentState = queue.poll();
      List<Position> nextPositions = currentState.pos().getNextPositions(currentState.dir());
      List<State> nextStates = nextPositions
          .stream()
          .filter(isValidCoordinate)
          .filter(position -> nextFilter.test(position, currentState))
          .map(position -> new State(position,
              currentState.nextStraight(position),
              currentState.heatLoss() + getHeatCost(position, matrix),
              currentState.pos().directionOf(position)))
          .filter(isValidState)
          .toList();

      for (State state : nextStates) {
        if (state.pos().equals(target) && stopFilter.test(state.straightLength())) {
          return state.heatLoss;
        }

        long costKey = state.toKey();
        if (!bestCost.containsKey(costKey) || state.heatLoss < bestCost.get(costKey)) {
          bestCost.put(costKey, state.heatLoss);
          queue.add(state);
        }
      }
    }

    throw new IllegalStateException("Can not solve matrix");
  }

  private static int getHeatCost(Position position, List<String> matrix) {
    return Integer.parseInt("" + matrix.get(position.y()).charAt(position.x()));
  }


  private static List<String> readInputAsLines() {
    try (BufferedReader br =
        new BufferedReader(
            new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day17/input.txt"))) {
      return br.lines().toList();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return new ArrayList<>();
  }
}
