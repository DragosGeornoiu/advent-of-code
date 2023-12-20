package ro.dragos.geornoiu.year2023.day20;

import java.util.concurrent.atomic.AtomicLong;

public record Counter(AtomicLong lowCounter, AtomicLong highCounter) {
}
