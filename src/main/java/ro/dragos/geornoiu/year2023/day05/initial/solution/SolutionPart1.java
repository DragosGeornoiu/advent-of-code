package ro.dragos.geornoiu.year2023.day05.initial.solution;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;

public class SolutionPart1 {

  private static final String LINE_SEEDS = "seeds: ";
  private static final String LINE_SEEDS_TO_SOIL = "seed-to-soil map:";
  private static final String LINE_SOIL_TO_FERTILIZER = "soil-to-fertilizer map:";
  private static final String LINE_FERTILIZER_TO_WATER = "fertilizer-to-water map:";
  private static final String LINE_WATER_TO_LIGHT = "water-to-light map:";
  private static final String LINE_LIGHT_TO_TEMP = "light-to-temperature map:";
  private static final String LINE_TEMP_TO_HUM = "temperature-to-humidity map:";
  private static final String LINE_HUM_TO_LOCATION = "humidity-to-location map:";

  private static final String RELATIVE_PATH_TO_CUR_DIR =
      "src/main/java/ro/dragos/geornoiu/year2023/day05/";
  private static final String FILENAME_OUTPUT_PART_1 = "outputPart1.txt";
  private static final String FILENAME_INPUT = "input.txt";

  public static void main(String[] args) {
    List<Seed> seeds = parseInput();
    OptionalLong lowestLocation = seeds.stream().mapToLong(Seed::getLocation).min();
    if (lowestLocation.isPresent()) {
      System.out.println("Lowest location first part: " + lowestLocation.getAsLong());
      writeOutputToFile(String.valueOf(lowestLocation.getAsLong()), FILENAME_OUTPUT_PART_1);
    } else {
      System.out.println("Could not determine lowest location for first part.");
    }
  }

  private static List<Seed> parseInput() {
    String input = readInputFromFile(FILENAME_INPUT);
    String[] textBlocks = input.split(System.lineSeparator() + System.lineSeparator());

    Map<Long, Seed> seedIdToSeedMap = new HashMap<>();
    for (String textBlock : textBlocks) {
      if (textBlock.contains(LINE_SEEDS)) {
        seedIdToSeedMap = parseSeeds(textBlock);
      }

      Set<Long> seedIds = seedIdToSeedMap.keySet();
      parseSeedDetailsInput(seedIdToSeedMap, textBlock, seedIds);
    }

    return new ArrayList<>(seedIdToSeedMap.values());
  }

  private static Map<Long, Seed> parseSeeds(String textBlock) {
    // seeds: 79 14 55 13
    return Arrays.stream(textBlock.replaceAll(LINE_SEEDS, "").split(" "))
        .map(Long::parseLong)
        .collect(Collectors.toMap(seedNumber -> seedNumber, Seed::new));
  }

  private static void parseSeedDetailsInput(
      Map<Long, Seed> seedIdToSeedMap, String textBlock, Set<Long> seedIds) {
    if (textBlock.contains(LINE_SEEDS_TO_SOIL)) {
      List<Range> ranges = parseTextBlock(textBlock.replaceAll(LINE_SEEDS_TO_SOIL, ""));
      Map<Long, Long> seedIdToSoilIdMap = parseRanges(seedIds, ranges);
      for (Seed seed : seedIdToSeedMap.values()) {
        seed.setSoil(seedIdToSoilIdMap.get(seed.getId()));
      }
    }

    if (textBlock.contains(LINE_SOIL_TO_FERTILIZER)) {
      List<Range> ranges = parseTextBlock(textBlock.replaceAll(LINE_SOIL_TO_FERTILIZER, ""));
      Set<Long> soilIds =
          seedIdToSeedMap.values().stream().map(Seed::getSoil).collect(Collectors.toSet());
      Map<Long, Long> soilToFertilizer = parseRanges(soilIds, ranges);
      for (Seed seed : seedIdToSeedMap.values()) {
        seed.setFertilizer(soilToFertilizer.get(seed.getSoil()));
      }
    }

    if (textBlock.contains(LINE_FERTILIZER_TO_WATER)) {
      List<Range> ranges = parseTextBlock(textBlock.replaceAll(LINE_FERTILIZER_TO_WATER, ""));
      Set<Long> fertilizerIds =
          seedIdToSeedMap.values().stream().map(Seed::getFertilizer).collect(Collectors.toSet());
      Map<Long, Long> fertilizerIdsToWaterIds = parseRanges(fertilizerIds, ranges);
      for (Seed seed : seedIdToSeedMap.values()) {
        seed.setWater(fertilizerIdsToWaterIds.get(seed.getFertilizer()));
      }
    }

    if (textBlock.contains(LINE_WATER_TO_LIGHT)) {
      List<Range> ranges = parseTextBlock(textBlock.replaceAll(LINE_WATER_TO_LIGHT, ""));
      Set<Long> waterIds =
          seedIdToSeedMap.values().stream().map(Seed::getWater).collect(Collectors.toSet());
      Map<Long, Long> waterIdsToLightIds = parseRanges(waterIds, ranges);
      for (Seed seed : seedIdToSeedMap.values()) {
        seed.setLight(waterIdsToLightIds.get(seed.getWater()));
      }
    }

    if (textBlock.contains(LINE_LIGHT_TO_TEMP)) {
      List<Range> ranges = parseTextBlock(textBlock.replaceAll(LINE_LIGHT_TO_TEMP, ""));
      Set<Long> lightIds =
          seedIdToSeedMap.values().stream().map(Seed::getLight).collect(Collectors.toSet());
      Map<Long, Long> lightIdsToTempIds = parseRanges(lightIds, ranges);
      for (Seed seed : seedIdToSeedMap.values()) {
        seed.setTemperature(lightIdsToTempIds.get(seed.getLight()));
      }
    }

    if (textBlock.contains(LINE_TEMP_TO_HUM)) {
      List<Range> ranges = parseTextBlock(textBlock.replaceAll(LINE_TEMP_TO_HUM, ""));
      Set<Long> tempIds =
          seedIdToSeedMap.values().stream().map(Seed::getTemperature).collect(Collectors.toSet());
      Map<Long, Long> tempIdsToHumIds = parseRanges(tempIds, ranges);
      for (Seed seed : seedIdToSeedMap.values()) {
        seed.setHumidity(tempIdsToHumIds.get(seed.getTemperature()));
      }
    }

    if (textBlock.contains(LINE_HUM_TO_LOCATION)) {
      List<Range> ranges = parseTextBlock(textBlock.replaceAll(LINE_HUM_TO_LOCATION, ""));
      Set<Long> humIds =
          seedIdToSeedMap.values().stream().map(Seed::getHumidity).collect(Collectors.toSet());
      Map<Long, Long> humIdsToLocation = parseRanges(humIds, ranges);
      for (Seed seed : seedIdToSeedMap.values()) {
        seed.setLocation(humIdsToLocation.get(seed.getHumidity()));
      }
    }
  }

  private static List<Range> parseTextBlock(String textBlock) {
    return Arrays.stream(textBlock.trim().split(System.lineSeparator()))
        .map(
            line -> {
              String[] split = line.split(" ");
              return new Range(
                  Long.parseLong(split[1]), Long.parseLong(split[0]), Long.parseLong(split[2]));
            })
        .collect(Collectors.toList());
  }

  private static Map<Long, Long> parseRanges(Set<Long> sourceIds, List<Range> ranges) {
    Map<Long, Long> map = new HashMap<>();

    for (Long sourceId : sourceIds) {
      boolean isPartOfARange = false;
      for (Range range : ranges) {
        if (range.isPartOfSourceRange(sourceId)) {
          map.put(sourceId, range.getCorrespondingTarget(sourceId));
          isPartOfARange = true;
          break;
        }
      }

      if (!isPartOfARange) {
        map.put(sourceId, sourceId);
      }
    }

    return map;
  }

  private static String readInputFromFile(String fileName) {
    try {
      String currentDirectory = System.getProperty("user.dir");
      Path filePath = Paths.get(currentDirectory, RELATIVE_PATH_TO_CUR_DIR + fileName);
      return Files.readString(filePath);
    } catch (IOException e) {
      throw new RuntimeException("Error reading file: " + RELATIVE_PATH_TO_CUR_DIR + fileName, e);
    }
  }

  private static void writeOutputToFile(String output, String fileName) {
    try {
      String currentDirectory = System.getProperty("user.dir");
      Path filePath = Paths.get(currentDirectory, RELATIVE_PATH_TO_CUR_DIR + fileName);
      Files.write(
          filePath,
          Collections.singletonList(output),
          StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException(String.format("Failed to write to file %s", fileName));
    }
  }
}

record Range(long sourceStart, long targetStart, long rangeLength) {

  public boolean isPartOfSourceRange(long value) {
    return sourceStart <= value && value <= sourceStart + rangeLength;
  }

  public long getCorrespondingTarget(long value) {
    return value - sourceStart + targetStart;
  }
}

class Seed {
  private long id;
  private long soil;
  private long fertilizer;
  private long water;
  private long light;
  private long temperature;
  private long humidity;
  private long location;

  public Seed(long id) {
    this.id = id;
  }

  public Seed(
      long id,
      long soil,
      long fertilizer,
      long water,
      long light,
      long temperature,
      long humidity,
      long location) {
    this.id = id;
    this.soil = soil;
    this.fertilizer = fertilizer;
    this.water = water;
    this.light = light;
    this.temperature = temperature;
    this.humidity = humidity;
    this.location = location;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getSoil() {
    return soil;
  }

  public void setSoil(long soil) {
    this.soil = soil;
  }

  public long getFertilizer() {
    return fertilizer;
  }

  public void setFertilizer(long fertilizer) {
    this.fertilizer = fertilizer;
  }

  public long getWater() {
    return water;
  }

  public void setWater(long water) {
    this.water = water;
  }

  public long getLight() {
    return light;
  }

  public void setLight(long light) {
    this.light = light;
  }

  public long getTemperature() {
    return temperature;
  }

  public void setTemperature(long temperature) {
    this.temperature = temperature;
  }

  public long getHumidity() {
    return humidity;
  }

  public void setHumidity(long humidity) {
    this.humidity = humidity;
  }

  public long getLocation() {
    return location;
  }

  public void setLocation(long location) {
    this.location = location;
  }

  @Override
  public String toString() {
    return "Seed{"
        + "id="
        + id
        + ", soil="
        + soil
        + ", fertilizer="
        + fertilizer
        + ", water="
        + water
        + ", light="
        + light
        + ", temperature="
        + temperature
        + ", humidity="
        + humidity
        + ", location="
        + location
        + '}';
  }
}