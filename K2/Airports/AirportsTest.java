package Napredno.K2.Airports;

import java.util.*;
import java.util.stream.Collectors;

public class AirportsTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Airports airports = new Airports();
        int n = scanner.nextInt();
        scanner.nextLine();
        String[] codes = new String[n];
        for (int i = 0; i < n; ++i) {
            String al = scanner.nextLine();
            String[] parts = al.split(";");
            airports.addAirport(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]));
            codes[i] = parts[2];
        }
        int nn = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < nn; ++i) {
            String fl = scanner.nextLine();
            String[] parts = fl.split(";");
            airports.addFlights(parts[0], parts[1], Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
        }
        int f = scanner.nextInt();
        int t = scanner.nextInt();
        String from = codes[f];
        String to = codes[t];
        System.out.printf("===== FLIGHTS FROM %S =====\n", from);
        airports.showFlightsFromAirport(from);
        System.out.printf("===== DIRECT FLIGHTS FROM %S TO %S =====\n", from, to);
        airports.showDirectFlightsFromTo(from, to);
        t += 5;
        t = t % n;
        to = codes[t];
        System.out.printf("===== DIRECT FLIGHTS TO %S =====\n", to);
        airports.showDirectFlightsTo(to);
    }
}

// vashiot kod ovde
class Flight implements Comparable<Flight>{
   private final int time;
    private final int duration;
   private final String from;
    private final String to;

    public Flight(String from,String to,int time, int duration) {
        this.from=from;
        this.to=to;
        this.time = time;
        this.duration = duration;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getTime() {
        return time;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public int compareTo(Flight o) {
        return Comparator.comparing(Flight::getTo).thenComparing(Flight::getTime).thenComparing(Flight::getFrom).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return time == flight.time && duration == flight.duration && Objects.equals(from, flight.from) && Objects.equals(to, flight.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, duration, from, to);
    }
    public String toString() {
        int end = time + duration;
        return String.format("%s-%s %02d:%02d-%02d:%02d %s%dh%02dm", from, to, time / 60, time % 60, (end / 60) % 24, end % 60, (end / 60) / 24 > 0 ? "+1d " : "", duration / 60, duration % 60);
    }
}
class Airport{
    private final String name;
    private final String country;
    private final String code;
    private final int passengers;
    public Airport(String name, String country,String code, int passengers){
        this.name=name;
        this.country=country;
        this.passengers=passengers;
        this.code=code;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }
    public int getPassengers() {
        return passengers;
    }
    @Override
    public String toString() {
        return String.format("%s (%s)%n%s%n%d", name, code, country, passengers);
    }
}
class Airports{
    Map<String,Airport> airports;
    Map<String,Set<Flight>> fromFlights;
    Map<String,Set<Flight>> toFlights;

    public Airports(){
        airports=new HashMap<>();
        fromFlights=new HashMap<>();
        toFlights=new HashMap<>();
    }

    public void addAirport(String name, String country, String code, int passengers) {
        airports.put(code, new Airport(name, country, code, passengers));
    }


    public void addFlights(String from, String to, int time, int duration) {
        Flight flight = new Flight(from, to, time, duration);

        fromFlights.putIfAbsent(from, new TreeSet<>());
        toFlights.putIfAbsent(to, new TreeSet<>());

        fromFlights.get(from).add(flight);
        toFlights.get(to).add(flight);
    }


    public void showFlightsFromAirport(String from) {
        Set<Flight> set = fromFlights.get(from);
        if (set == null || set.isEmpty()) {
            System.out.println("No flights from " + from);
            return;
        }

        System.out.println(airports.get(from));
        int i = 1;
        for (Flight f : set) {
            System.out.println(i++ + ". " + f);
        }
    }


    public void showDirectFlightsFromTo(String from, String to) {
        Set<Flight> set = fromFlights.get(from).stream()
                .filter(flight -> flight.getTo().equals(to) || flight.getFrom().equals(to))
                .collect(Collectors.toSet());

        if (set.isEmpty()) {
            System.out.println("No flights from " + from + " to " + to);
            return;
        }

        set.forEach(System.out::println);
    }

    public void showDirectFlightsTo(String to) {
        Set<Flight> set = toFlights.get(to);
        if (set.isEmpty()) {
            System.out.println("No direct flights to " + to);
            return;
        }

        set.forEach(System.out::println);
    }

}
