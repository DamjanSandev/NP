package Napredno.K_2023_2024.K2_2023_2024.StreamingPlatform2;

import java.util.*;
import java.util.stream.Collectors;

class CosineSimilarityCalculator {

    public static double cosineSimilarity(Map<String, Integer> c1, Map<String, Integer> c2) {
        return cosineSimilarity(c1.values(), c2.values());
    }

    public static double cosineSimilarity(Collection<Integer> c1, Collection<Integer> c2) {
        int[] array1;
        int[] array2;
        array1 = c1.stream().mapToInt(i -> i).toArray();
        array2 = c2.stream().mapToInt(i -> i).toArray();
        double up = 0.0;
        double down1 = 0, down2 = 0;

        for (int i = 0; i < c1.size(); i++) {
            up += (array1[i] * array2[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down1 += (array1[i] * array1[i]);
        }

        for (int i = 0; i < c1.size(); i++) {
            down2 += (array2[i] * array2[i]);
        }

        return up / (Math.sqrt(down1) * Math.sqrt(down2));
    }
}


public class StreamingPlatform2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StreamingPlatform sp = new StreamingPlatform();

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            if (parts[0].equals("addMovie")) {
                String id = parts[1];
                String name = Arrays.stream(parts).skip(2).collect(Collectors.joining(" "));
                sp.addMovie(id, name);
            } else if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                sp.addUser(id, name);
            } else if (parts[0].equals("addRating")) {
                //String userId, String movieId, int rating
                String userId = parts[1];
                String movieId = parts[2];
                int rating = Integer.parseInt(parts[3]);
                sp.addRating(userId, movieId, rating);
            } else if (parts[0].equals("topNMovies")) {
                int n = Integer.parseInt(parts[1]);
                System.out.println("TOP " + n + " MOVIES:");
                sp.topNMovies(n);
            } else if (parts[0].equals("favouriteMoviesForUsers")) {
                List<String> users = Arrays.stream(parts).skip(1).collect(Collectors.toList());
                System.out.println("FAVOURITE MOVIES FOR USERS WITH IDS: " + users.stream().collect(Collectors.joining(", ")));
                sp.favouriteMoviesForUsers(users);
            } else if (parts[0].equals("similarUsers")) {
                String userId = parts[1];
                System.out.println("SIMILAR USERS TO USER WITH ID: " + userId);
                sp.similarUsers(userId);
            }
        }
    }
}

class Movie {
    private final String title, id;
    private final List<Integer> ratings;

    public Movie(String id, String title) {
        this.title = title;
        this.id = id;
        ratings = new ArrayList<>();
    }

    public void addRating(int rating) {
        ratings.add(rating);
    }

    public double getAvgRating() {
        return ratings.stream().mapToDouble(Integer::intValue).sum() / ratings.size();
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Movie ID: %s Title: %s Rating: %.2f", id, title, getAvgRating());
    }
}

class User {
    private final String id, username;
    private final Map<Movie, Integer> moviesRated;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
        moviesRated = new HashMap<>();
    }

    public void addRating(Movie m, int rating) {
        moviesRated.putIfAbsent(m, rating);
    }

    public List<Movie> getFavouriteMovies() {
        int favMovieRating = moviesRated.values().stream().max(Integer::compare).get();
        return moviesRated.entrySet().stream().filter(entry -> entry.getValue().equals(favMovieRating)).
                map(Map.Entry::getKey).
                sorted(Comparator.comparing(Movie::getAvgRating).reversed().thenComparing(Movie::getId)).
                collect(Collectors.toList());
    }

    public Map<Movie, Integer> getMoviesRated() {
        return moviesRated;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("User ID: %s Name: %s", id, username);
    }
}

class StreamingPlatform {

    private final Map<String, Movie> moviesById;
    private final Map<String, User> usersById;

    public StreamingPlatform() {
        moviesById = new HashMap<>();
        usersById = new HashMap<>();
    }

    public void addMovie(String id, String name) {
        moviesById.putIfAbsent(id, new Movie(id, name));
    }

    public void addUser(String id, String name) {
        usersById.putIfAbsent(id, new User(id, name));
    }

    public void addRating(String userId, String movieId, int rating) {
        Movie m = moviesById.get(movieId);
        m.addRating(rating);
        usersById.get(userId).addRating(m, rating);
    }

    public void topNMovies(int n) {
        moviesById.values().stream().sorted(Comparator.comparing(Movie::getAvgRating).reversed()).limit(n).forEach(System.out::println);
    }

    public void favouriteMoviesForUsers(List<String> userIds) {
        userIds.forEach(id -> {
            User u = usersById.get(id);
            System.out.println(u);
            u.getFavouriteMovies().forEach(System.out::println);
            System.out.println();
        });
    }

    public void similarUsers(String userId) {
        User currentUser = usersById.get(userId);

        Map<String, Integer> currentUserRatings = new HashMap<>();

        currentUser.getMoviesRated().forEach((key, value) -> currentUserRatings.put(key.getId(), value));
        moviesById.forEach((key, value) -> currentUserRatings.putIfAbsent(key, 0));

        Map<User, Double> userSimilarities = new HashMap<>();
        usersById.values().stream()
                .filter(user -> !user.getId().equals(userId))
                .forEach(user -> {
                    Map<String, Integer> otherUserRatings = new HashMap<>();
                    user.getMoviesRated().forEach((key, value) -> otherUserRatings.put(key.getId(), value));
                    moviesById.forEach((key, value) -> otherUserRatings.putIfAbsent(key, 0));
                    double similarity = CosineSimilarityCalculator.cosineSimilarity(currentUserRatings, otherUserRatings);
                    userSimilarities.put(user, similarity);
                });
        userSimilarities.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(entry -> System.out.println(entry.getKey() + " " + entry.getValue()));

    }
}

