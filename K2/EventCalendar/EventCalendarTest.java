package Napredno.K2.EventCalendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class EventCalendarTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        EventCalendar eventCalendar = new EventCalendar(year);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            String name = parts[0];
            String location = parts[1];
            Date date = df.parse(parts[2]);
            try {
                eventCalendar.addEvent(name, location, date);
            } catch (WrongDateException e) {
                System.out.println(e.getMessage());
            }
        }
        Date date = df.parse(scanner.nextLine());
        eventCalendar.listEvents(date);
        eventCalendar.listByMonth();
    }
}

// vashiot kod ovde
class WrongDateException extends Exception {
    public WrongDateException(Date date) {
        super("Wrong date: " + date.toString());
    }
}

class Event implements Comparable<Event> {
    private final String name;
    private final String location;
    private final Date date;

    public Event(String name, String location, Date date) {
        this.name = name;
        this.location = location;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public int getMonth() {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;
    }

    @Override
    public int compareTo(Event o) {
        return Comparator.comparing(Event::getDate).thenComparing(Event::getName).compare(this, o);
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM, yyy HH:mm");
        String formattedDate = dateFormat.format(date);
        return String.format("%s at %s, %s", formattedDate, location, name);
    }
}

class EventCalendar {
    private final int year;
    Map<Date, Set<Event>> eventsByDate;

    public EventCalendar(int year) {
        this.year = year;
        eventsByDate = new HashMap<>();
    }

    public void addEvent(String name, String location, Date date) throws WrongDateException {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        if (c.get(Calendar.YEAR) != year) {
            throw new WrongDateException(date);
        }
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Date dateFormatted = c.getTime();
        eventsByDate.putIfAbsent(dateFormatted, new TreeSet<>());
        eventsByDate.get(dateFormatted).add(new Event(name, location, date));
    }

    public void listEvents(Date date) {
        if (eventsByDate.get(date) == null) {
            System.out.println("No events on this day!");
            return;
        }
        eventsByDate.get(date).forEach(System.out::println);
    }

    public void listByMonth() {
        Map<Integer, Long> eventsByMonth = eventsByDate.values().stream().flatMap(Set::stream).collect(Collectors.groupingBy(
                Event::getMonth,
                TreeMap::new,
                Collectors.counting()
        ));
        for (int i = 1; i <= 12; i++) {
            eventsByMonth.putIfAbsent(i, 0L);
        }
        eventsByMonth.forEach((key, value) -> System.out.println(key + " : " + value));

    }
}

