package Napredno.K_2023_2024.K2_2023_2024.Parking;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

//Netocni primeri za 1 do 2 decimali za spotOccupacy

class DateUtil {
    public static long durationBetween(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end).toMinutes();
    }
}

public class ParkingTesting {

    public static <K, V extends Comparable<V>> void printMapSortedByValue(Map<K, V> map) {
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> System.out.printf("%s -> %s%n", entry.getKey().toString(), entry.getValue().toString()));

    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int capacity = Integer.parseInt(sc.nextLine());

        Parking parking = new Parking(capacity);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            if (parts[0].equals("update")) {
                String registration = parts[1];
                String spot = parts[2];
                LocalDateTime timestamp = LocalDateTime.parse(parts[3]);
                boolean entrance = Boolean.parseBoolean(parts[4]);
                parking.update(registration, spot, timestamp, entrance);
            } else if (parts[0].equals("currentState")) {
                System.out.println("PARKING CURRENT STATE");
                parking.currentState();
            } else if (parts[0].equals("history")) {
                System.out.println("PARKING HISTORY");
                parking.history();
            } else if (parts[0].equals("carStatistics")) {
                System.out.println("CAR STATISTICS");
                printMapSortedByValue(parking.carStatistics());
            } else if (parts[0].equals("spotOccupancy")) {
                LocalDateTime start = LocalDateTime.parse(parts[1]);
                LocalDateTime end = LocalDateTime.parse(parts[2]);
                printMapSortedByValue(parking.spotOccupancy(start, end));
            }
        }
    }
}

class Entry {
    private final String registration;
    private final String spot;
    private final LocalDateTime start;
    private LocalDateTime end;
    private boolean ongoing;

    public Entry(String registration, String spot, LocalDateTime start, boolean ongoing) {
        this.registration = registration;
        this.spot = spot;
        this.start = start;
        this.ongoing = ongoing;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }


    public LocalDateTime getStart() {
        return start;
    }

    public long parkingDuration() {
        return DateUtil.durationBetween(start, end);
    }

    public void currentParked() {
        System.out.printf("Registration number: %s Spot: %s Start timestamp: %s%n", registration, spot, start);
    }

    public void parkingFinished() {
        System.out.printf("Registration number: %s Spot: %s Start timestamp: %s End timestamp: %s Duration in minutes: %d%n", registration, spot, start, end, parkingDuration());
    }
}

class Spot {
    private final String spot;
    private final Map<LocalDateTime, LocalDateTime> parkTimestamps;

    public Spot(String spot) {
        this.spot = spot;
        parkTimestamps = new HashMap<>();
    }

    public void addPark(LocalDateTime start, LocalDateTime end) {
        parkTimestamps.put(start, end);
    }

    public double getSpotOccupacy(LocalDateTime start, LocalDateTime end) {
        long duration = DateUtil.durationBetween(start, end);
        long durationPassed = 0L;
        for (Map.Entry<LocalDateTime, LocalDateTime> entry : parkTimestamps.entrySet()) {
            LocalDateTime sTime = entry.getKey(), endTime = entry.getValue();
            if (sTime.isBefore(start) || sTime.isAfter(end))
                continue;
            if (endTime.isAfter(end))
                endTime = end;
            if ((sTime.isAfter(start) || sTime == start) && (endTime.isBefore(end) || endTime == end)) {
                durationPassed += (DateUtil.durationBetween(sTime, endTime));
            }
        }
        return ((double) durationPassed / duration) * 100;
    }

    public String getSpot() {
        return spot;
    }
}

class Parking {
    private final int capacity;
    private final Map<String, List<Entry>> parkings;

    private final Map<String, Spot> spots;


    public Parking(int capacity) {
        this.capacity = capacity;
        parkings = new HashMap<>();
        spots = new HashMap<>();
    }

    public void update(String registration, String spot, LocalDateTime timestamp, boolean entrance) {
        parkings.putIfAbsent(registration, new ArrayList<>());
        if (entrance) {
            parkings.get(registration).add(new Entry(registration, spot, timestamp, true));
        } else {
            List<Entry> entries = parkings.get(registration);
            Entry e = entries.get(entries.size() - 1);
            e.setEnd(timestamp);
            e.setOngoing(false);
            spots.putIfAbsent(spot, new Spot(spot));
            spots.get(spot).addPark(e.getStart(), timestamp);
        }
    }

    public void currentState() {
        int parked = (int) parkings.values().stream().flatMap(List::stream).filter(Entry::isOngoing).count();
        if (parked == 0) {
            System.out.println("Capacity filled: 0.00%");
            return;
        }
        double capacityFilled = ((double) parked / capacity) * 100;

        System.out.printf("Capacity filled: %.2f%%\n", capacityFilled);
        parkings.values().stream().flatMap(List::stream).filter(Entry::isOngoing).sorted(Comparator.comparing(Entry::getStart).reversed()).forEach(Entry::currentParked);
    }

    public void history() {
        parkings.values().stream().flatMap(List::stream).filter(entry -> !entry.isOngoing()).sorted(Comparator.comparing(Entry::parkingDuration).reversed()).forEach(Entry::parkingFinished);
    }

    public Map<String, Integer> carStatistics() {
        Map<String, Integer> result = new TreeMap<>();
        parkings.forEach((key, value) -> result.put(key, value.size()));
        return result;
    }

    public Map<String, Double> spotOccupancy(LocalDateTime start, LocalDateTime end) {
        Map<String, Double> result = new TreeMap<>();

        spots.values().forEach(spot -> result.put(spot.getSpot(), spot.getSpotOccupacy(start, end)));

        return result;

    }
}

