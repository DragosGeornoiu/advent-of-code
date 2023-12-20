package ro.dragos.geornoiu.year2023.day20;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Represents a state machine that simulates the behavior of interconnected nodes.
 */
public record Machine(Map<String, Node> setup, Counter counter) {

  /**
   * Simulates a button push and counts the number of low pulses received.
   * If a low pulse is received by the "rx" node, returns true; otherwise, returns false.
   *
   * @return True if a low pulse is received by the "rx" node, false otherwise.
   */
  boolean push() {
    // Initialize a message queue and increment the low counter for the button push
    Queue<Message> messageQueue = new LinkedList<>();
    counter.lowCounter().incrementAndGet();
    messageQueue.add(new Message("button", Node.BROADCASTER, Pulse.LOW));

    // Process messages in the queue until it is empty
    while (!messageQueue.isEmpty()) {
      Message curr = messageQueue.poll();

      // Check if the setup contains the target node
      if (setup.containsKey(curr.target())) {
        // Simulate receiving a message and obtain next messages
        var nexts = setup.get(curr.target()).recieve(curr.from(), curr.pulse());

        // Process and enqueue the next messages
        nexts.forEach(msg -> {
          // Update counters based on pulse type
          if (msg.pulse() == Pulse.LOW) {
            counter.lowCounter().incrementAndGet();
          } else {
            counter.highCounter().incrementAndGet();
          }
          messageQueue.add(msg);
        });
      }

      // Check if the target is "rx" and a low pulse is received
      if (curr.target().equals("rx") && curr.pulse() == Pulse.LOW) {
        return true;
      }
    }

    return false;
  }

  /**
   * Calculates the product of the low and high pulse counts.
   *
   * @return The product of low and high pulse counts.
   */
  long count() {
    return counter.lowCounter().get() * counter.highCounter().get();
  }

  /**
   * Resets the counters and all nodes in the setup.
   */
  void reset() {
    // Reset counters
    counter.lowCounter().set(0);
    counter.highCounter().set(0);

    // Reset all nodes in the setup
    setup.values().forEach(Node::reset);
  }

  /**
   * Builds a Machine instance from a list of nodes.
   * Connects nodes based on their outputs and inputs.
   *
   * @param nodes List of nodes to build the machine from.
   * @return A new Machine instance.
   */
  static Machine build(List<Node> nodes) {
    // Map nodes by their names
    Map<String, Node> mapped = nodes.stream().collect(Collectors.toMap(Node::getName, node -> node));

    // Connect nodes based on outputs and inputs
    mapped.values().forEach(node -> node.getOutputs().stream().filter(mapped::containsKey)
        .forEach(o -> mapped.get(o).markInput(node.getName())));

    return new Machine(mapped, new Counter(new AtomicLong(0L), new AtomicLong(0L)));
  }
}