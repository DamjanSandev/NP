package Napredno.K_2023_2024.K1_2023_2024.StreamingPlatform;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class StreamingPlatformTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StreamingPlatform sp = new StreamingPlatform();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");
            String method = parts[0];
            String data = Arrays.stream(parts).skip(1).collect(Collectors.joining(" "));
            if (method.equals("addItem")) {
                sp.addItem(data);
            } else if (method.equals("listAllItems")) {
                sp.listAllItems(System.out);
            } else if (method.equals("listFromGenre")) {
                System.out.println(data);
                sp.listFromGenre(data, System.out);
            }
        }

    }
}

interface Content {
    double getRating();

    boolean hasGenre(String genre);
}

class Episode {
    List<Integer> episodeRatings;

    public Episode(String line) {
        episodeRatings = Arrays.stream(line.split("\\s+")).skip(1).map(Integer::parseInt).collect(Collectors.toList());
    }

    public double getEpisodeRating() {
        double size = episodeRatings.size();
        return (episodeRatings.stream().mapToDouble(value -> value).sum() / size) * Math.min(size / 20.0, 1.0);
    }
}

class Series implements Content {
    private final String title;
    private final List<String> genres;

    private final List<Episode> episodes;

    public Series(String line) {
        String[] parts = line.split(";");
        this.title = parts[0];
        this.genres = Arrays.stream(parts[1].split(",")).collect(Collectors.toList());
        this.episodes = Arrays.stream(parts).skip(2).map(Episode::new).collect(Collectors.toList());
    }


    @Override
    public double getRating() {
        List<Episode> bestEpisodes = episodes.stream().sorted(Comparator.comparing(Episode::getEpisodeRating).reversed()).limit(3).collect(Collectors.toList());
        return bestEpisodes.stream().mapToDouble(Episode::getEpisodeRating).sum() / 3.0;
    }

    @Override
    public boolean hasGenre(String genre) {
        return genres.stream().anyMatch(s -> s.equals(genre));
    }

    @Override
    public String toString() {
        return String.format("TV Show %s %.4f (%d episodes)", title, getRating(), episodes.size());
    }

}

class Movie implements Content {
    private final String title;
    private final List<String> genres;

    private final List<Integer> ratings;

    public Movie(String line) {
        String[] parts = line.split(";");
        this.title = parts[0];
        this.genres = Arrays.stream(parts[1].split(",")).collect(Collectors.toList());
        this.ratings = Arrays.stream(parts[2].split("\\s+")).map(Integer::parseInt).collect(Collectors.toList());
    }

    public double getRatingsAvg() {
        return ratings.stream().mapToDouble(Integer::intValue).sum() / ratings.size();
    }

    @Override
    public double getRating() {
        return getRatingsAvg() * Math.min(ratings.size() / 20.0, 1.0);
    }

    @Override
    public boolean hasGenre(String genre) {
        return genres.stream().anyMatch(s -> s.equals(genre));
    }

    @Override
    public String toString() {
        return String.format("Movie %s %.4f", title, getRatingsAvg());
    }
}


class StreamingPlatform {
    List<Content> content;

    public StreamingPlatform() {
        content = new ArrayList<>();
    }

    public void addItem(String data) {
        if (data.split(";").length > 3) {
            content.add(new Series(data));
        } else {
            content.add(new Movie(data));
        }
    }


    public void listAllItems(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        content.stream().sorted(Comparator.comparing(Content::getRating).reversed()).forEach(pw::println);
        pw.flush();

    }

    public void listFromGenre(String data, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        content.stream().filter(content -> content.hasGenre(data)).sorted(Comparator.comparing(Content::getRating).reversed()).forEach(pw::println);
        pw.flush();
    }
}
