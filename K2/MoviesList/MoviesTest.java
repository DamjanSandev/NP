package Napredno.K2.MoviesList;

import java.util.*;
import java.util.stream.Collectors;

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}

// vashiot kod ovde

class Movie {
    private final String name;
    private final List<Integer> ratings;

    public Movie(String name, int[] ratings) {
        this.name = name;
        this.ratings = Arrays.asList(Arrays.stream(ratings).boxed().toArray(Integer[]::new));
    }

    public double getAverageRating() {
        return ratings.stream().mapToDouble(value -> value).sum() / ratings.size();
    }

    public int getRatingsSize() {
        return ratings.size();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings", name, getAverageRating(), getRatingsSize());
    }
}

class MoviesList {
    private final List<Movie> movies;
    private final Comparator<Movie> comparatorAvg = Comparator.comparing(Movie::getAverageRating).reversed().thenComparing(Movie::getName);
    private final Comparator<Movie> comparatorCoef = Comparator.comparing(this::calculateCoef).reversed().thenComparing(Movie::getName);

    public MoviesList() {
        this.movies = new ArrayList<>();
    }

    public void addMovie(String title, int[] ratings) {
        movies.add(new Movie(title, ratings));
    }

    public List<Movie> top10ByAvgRating() {
        return movies.stream().sorted(comparatorAvg).limit(10).collect(Collectors.toList());
    }

    public int getTotalRatings() {
        return movies.stream().mapToInt(Movie::getRatingsSize).sum();
    }

    public List<Movie> top10ByRatingCoef() {
        return movies.stream().sorted(comparatorCoef).limit(10).collect(Collectors.toList());
    }

    private double calculateCoef(Movie movie) {
        return (movie.getAverageRating() * movie.getRatingsSize()) / getTotalRatings();
    }
}
