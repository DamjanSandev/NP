package Napredno.K2.Stadium;

import java.util.*;

public class StaduimTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] sectorNames = new String[n];
        int[] sectorSizes = new int[n];
        String name = scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            sectorNames[i] = parts[0];
            sectorSizes[i] = Integer.parseInt(parts[1]);
        }
        Stadium stadium = new Stadium(name);
        stadium.createSectors(sectorNames, sectorSizes);
        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            try {
                stadium.buyTicket(parts[0], Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]));
            } catch (SeatNotAllowedException e) {
                System.out.println("SeatNotAllowedException");
            } catch (SeatTakenException e) {
                System.out.println("SeatTakenException");
            }
        }
        stadium.showSectors();
    }
}

class SeatNotAllowedException extends Exception {
    public SeatNotAllowedException() {
    }
}

class SeatTakenException extends Exception {
    public SeatTakenException() {
    }
}

class Sector implements Comparable<Sector> {
    private final String name;
    private final int size;
    private final Map<Integer, Integer> taken;
    private final Set<Integer> types;

    public Sector(String name, int size) {
        this.name = name;
        this.size = size;
        taken = new HashMap<>();
        types = new HashSet<>();
    }

    public int freeSeats() {
        return size - taken.size();
    }

    public boolean isTaken(int seat) {
        return taken.containsKey(seat);
    }

    public void takeSeat(int seat, int type) throws SeatNotAllowedException {
        if (type == 1) {
            if (types.contains(2)) {
                throw new SeatNotAllowedException();
            }
        } else if (type == 2) {
            if (types.contains(1)) {
                throw new SeatNotAllowedException();
            }
        }
        types.add(type);
        taken.put(seat, type);
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Sector o) {
        return Comparator.comparing(Sector::freeSeats, Comparator.reverseOrder()).thenComparing(Sector::getName).compare(this, o);
    }

    @Override
    public String toString() {
        return String.format("%s\t%d/%d\t%.1f%%", name, size - taken.size(), size, ((double) taken.size() / size) * 100);
    }
}

class Stadium {
    private final String name;
    Map<String, Sector> sectorsByName;

    public Stadium(String name) {
        this.name = name;
        sectorsByName = new HashMap<>();
    }

    public void createSectors(String[] sectorNames, int[] sectorSizes) {
        for (int i = 0; i < sectorSizes.length; i++) {
            sectorsByName.put(sectorNames[i], new Sector(sectorNames[i], sectorSizes[i]));
        }
    }


    public void buyTicket(String sectorName, int seat, int type) throws SeatNotAllowedException, SeatTakenException {
        if (sectorsByName.get(sectorName).isTaken(seat)) {
            throw new SeatTakenException();
        }
        sectorsByName.get(sectorName).takeSeat(seat, type);
    }

    public void showSectors() {
        sectorsByName.values().stream().sorted().forEach(System.out::println);
    }
}
