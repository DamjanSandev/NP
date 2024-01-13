package Napredno.K2.StopCorona;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

interface ILocation {
    double getLongitude();

    double getLatitude();

    LocalDateTime getTimestamp();
}

public class StopCoronaTest {

    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG": //register
                    String name = parts[1];
                    String id = parts[2];
                    try {
                        stopCoronaApp.addUser(name, id);
                    } catch (UserAlreadyExistException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC": //add locations
                    id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);

                    break;
                case "DET": //detect new cases
                    id = parts[1];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
                    stopCoronaApp.detectNewCase(id, timestamp);

                    break;
                case "REP": //print report
                    stopCoronaApp.createReport();
                    break;
                default:
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            @Override
            public double getLongitude() {
                return Double.parseDouble(lon);
            }

            @Override
            public double getLatitude() {
                return Double.parseDouble(lat);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.parse(timestamp);
            }
        };
    }
}

class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException(String message) {
        super(message);
    }
}

class User {
    private final String name, id;
    private List<ILocation> locations;

    private boolean isCovidPositive;

    private LocalDateTime timestamp;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        isCovidPositive = false;
        locations = new ArrayList<>();
    }

    public void setLocations(List<ILocation> locations) {
        this.locations = locations;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void newCovidCase(LocalDateTime timestamp) {
        isCovidPositive = true;
        this.timestamp = timestamp;
    }


    public List<ILocation> getLocations() {
        return locations;
    }

    public boolean isCovidPositive() {
        return isCovidPositive;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", name, id, timestamp);
    }

    public String userHidden() {
        return String.format("%s %s***", name, id.substring(0, 4));
    }
}

class StopCoronaApp {
    Map<String, User> usersById;

    public StopCoronaApp() {
        usersById = new HashMap<>();
    }

    public void addUser(String name, String id) throws UserAlreadyExistException {
        if (usersById.containsKey(id)) {
            throw new UserAlreadyExistException("User with ID: " + id + " already exists");
        }
        usersById.put(id, new User(name, id));
    }

    public void addLocations(String id, List<ILocation> locations) {
        usersById.get(id).setLocations(locations);
    }

    public void detectNewCase(String id, LocalDateTime timestamp) {
        usersById.get(id).newCovidCase(timestamp);
    }


    private Map<User, Integer> getDirectContacts(User u) {
        Map<User, Integer> directContacts = new HashMap<>();
        for (String userId : usersById.keySet()) {
            if (!userId.equals(u.getId())) {
                User user = usersById.get(userId);
                int count = CloseEncountersCalculator.dangerContactsCounter(user, u);
                if (count != 0) {
                    directContacts.put(user, count);
                }
            }
        }
        return directContacts;
    }

    private Collection<User> getIndirectContacts(User u) {
        Set<User> indirectContacts = new HashSet<>();
        Map<User, Integer> directContact = getDirectContacts(u);
        directContact.keySet().stream()
                .flatMap(user -> getDirectContacts(user).keySet().stream())
                .filter(user -> !indirectContacts.contains(user) && !directContact.containsKey(user) && !user.equals(u))
                .forEach(indirectContacts::add);

        return indirectContacts;
    }

    public void createReport() {
        List<User> infectedUsers = usersById.values().stream().filter(User::isCovidPositive).sorted(Comparator.comparing(User::getTimestamp).thenComparing(User::getId)).collect(Collectors.toList());
        infectedUsers.forEach(user -> {
            System.out.println(user);

            System.out.println("Direct contacts:");
            getDirectContacts(user).entrySet().stream().sorted(Map.Entry.<User, Integer>comparingByValue().thenComparing(entry -> entry.getKey().getName()).thenComparing(entry -> entry.getKey().getId()).reversed()).forEach((k -> System.out.println(k.getKey().userHidden() + " " + k.getValue())));
            System.out.printf("Count of direct contacts: %d%n", getDirectContacts(user).values().stream().mapToInt(i -> i).sum());

            System.out.println("Indirect contacts:");
            getIndirectContacts(user).stream().sorted(Comparator.comparing(User::getName).thenComparing(User::getId)).forEach(user1 -> System.out.println(user1.userHidden()));
            System.out.printf("Count of indirect contacts: %d%n", getIndirectContacts(user).size());


        });
        int totalNumberOfIndirectContacts = infectedUsers.stream().mapToInt(user -> getIndirectContacts(user).size()).sum();
        int totalNumberOfDirectContacts = infectedUsers.stream().mapToInt(user -> getDirectContacts(user).values().stream().mapToInt(i -> i).sum()).sum();
        System.out.printf("Average direct contacts: %.4f%n", totalNumberOfDirectContacts * 1.0 / infectedUsers.size());
        System.out.printf("Average indirect contacts: %.4f%n", totalNumberOfIndirectContacts * 1.0 / infectedUsers.size());
    }
}


class CloseEncountersCalculator {

    public static double distanceBetween(ILocation location1, ILocation location2) {
        return Math.sqrt(Math.pow(location1.getLatitude() - location2.getLatitude(), 2)
                + Math.pow(location1.getLongitude() - location2.getLongitude(), 2));
    }

    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static boolean isDangerContact(ILocation location1, ILocation location2) {
        return distanceBetween(location1, location2) <= 2 && timeBetweenInSeconds(location1, location2) <= 300;
    }

    public static int dangerContactsCounter(User u1, User u2) {
        int counter = 0;

        for (ILocation iLocation : u1.getLocations()) {
            for (ILocation iLocation1 : u2.getLocations()) {
                if (isDangerContact(iLocation1, iLocation))
                    counter++;
            }
        }
        return counter;
    }
}


