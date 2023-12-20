package ro.dragos.geornoiu.year2023.day20;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node in a state machine with specific behavior based on its type.
 */
public class Node {

  /**
   * Represents the broadcaster type for special cases.
   */
  public static final String BROADCASTER = "broadcaster";

  private final Map<String, Pulse> inputs = new HashMap<>();

  private char type;
  private String name;
  private boolean onOff = false;
  private List<String> outputs = new ArrayList<>();

  /**
   * Parses a string representation of a node and creates a Node instance.
   *
   * @param line The string representation of the node.
   * @return A new Node instance.
   */
  static Node parse(String line) {
    Node result = new Node();
    var parts = line.split(" -> ");
    if (parts[0].equals(BROADCASTER)) {
      result.type = '?';
      result.name = parts[0];
    } else {
      result.type = parts[0].charAt(0);
      result.name = parts[0].substring(1);
    }

    // Add outputs based on the string representation
    Collections.addAll(result.outputs, parts[1].split(", "));
    return result;
  }

  /**
   * Marks an input for the node if its type is '&'.
   *
   * @param input The input to mark.
   */
  void markInput(String input) {
    if (type == '&') {
      inputs.put(input, Pulse.LOW);
    }
  }

  /**
   * Simulates receiving a message and generates messages to be sent to other nodes.
   *
   * @param from  The sender of the message.
   * @param pulse The pulse received.
   * @return A list of messages to be sent to other nodes.
   */
  List<Message> recieve(String from, Pulse pulse) {
    List<Message> notified = new ArrayList<>();
    switch (type) {
      case '%' -> {
        if (pulse == Pulse.LOW) {
          onOff = !onOff;
          // Generate messages based on the onOff state
          outputs.stream().map(s -> new Message(name, s, onOff ? Pulse.HIGH : Pulse.LOW))
              .forEach(notified::add);
        }
      }
      case '&' -> {
        inputs.put(from, pulse);
        // Check if all inputs are high and generate messages accordingly
        boolean allHigh = inputs.values().stream().allMatch(p -> p == Pulse.HIGH);
        outputs.stream().map(s -> new Message(name, s, allHigh ? Pulse.LOW : Pulse.HIGH))
            .forEach(notified::add);
      }
      case '?' -> outputs.stream().map(s -> new Message(name, s, pulse)).forEach(notified::add);
      default -> throw new IllegalArgumentException(String.format("No case match for %s", type));
    }
    return notified;
  }

  /**
   * Resets the state of the node, setting onOff to false and clearing input pulses.
   */
  void reset() {
    onOff = false;
    inputs.keySet().forEach(k -> inputs.put(k, Pulse.LOW));
  }

  public String getName() {
    return name;
  }

  public List<String> getOutputs() {
    return outputs;
  }

  public Map<String, Pulse> getInputs() {
    return inputs;
  }

  public void setOutputs(List<String> outputs) {
    this.outputs = outputs;
  }
}