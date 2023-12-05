package ro.dragos.geornoiu.year2023.day05.part2;

import java.util.Map;
import java.util.concurrent.Callable;
import ro.dragos.geornoiu.year2023.day05.part1.Converter;

public class TestThread implements Callable<Long> {

  private Map<String, Converter> converters;
  private long start;
  private long len;

  public TestThread(Map<String, Converter> converters, long start, long len) {
    this.converters = converters;
    this.start = start;
    this.len = len;
  }

  @Override
  public Long call() {
    long smallestValue = Long.MAX_VALUE;

    for (long seed = start; seed < start + len; seed++) {
      String destination = "seed";
      long value = seed;
      while (!destination.equals("location")) {
        Converter currentConvert = converters.get(destination);
        destination = currentConvert.getDestination();
        value = currentConvert.getDestinationValue(value);
      }

      if (value < smallestValue) {
        smallestValue = value;
      }
    }

    System.out.println("Smallest value in thread: " + smallestValue);
    return smallestValue;
  }
}
