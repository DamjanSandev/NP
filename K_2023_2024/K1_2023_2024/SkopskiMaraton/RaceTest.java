package Napredno.K_2023_2024.K1_2023_2024.SkopskiMaraton;

import java.io.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class RaceTest {
    public static void main(String[] args) {
        try {
            TeamRace.findBestTeam(System.in, System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

class Participant implements Comparable<Participant> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final String id;
    private final Duration raceDuration;

    public Participant(String line) {
        String[] parts = line.split("\\s+");
        this.id = parts[0];
        String startTime = parts[1];
        String endTime = parts[2];
        raceDuration = Duration.between(LocalTime.parse(startTime, formatter), LocalTime.parse(endTime, formatter));
    }

    public Duration getRaceDuration() {
        return raceDuration;
    }

    @Override
    public int compareTo(Participant o) {
        return Long.compare(this.raceDuration.getSeconds(), o.raceDuration.getSeconds());
    }

    @Override
    public String toString() {
        return String.format("%s %02d:%02d:%02d", id, raceDuration.toHours(), raceDuration.toMinutesPart(), raceDuration.toSecondsPart());
    }
}

class TeamRace {
    private static List<Participant> participants = new ArrayList<>();

    public static void findBestTeam(InputStream in, OutputStream out) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        participants = br.lines().map(Participant::new).collect(Collectors.toList());

        PrintWriter pw = new PrintWriter(out);

        participants.stream().sorted().limit(4).forEach(pw::println);

        pw.println(getTimeRaced());

        pw.flush();

    }

    private static String getTimeRaced() {
        final Duration[] raceDuration = {Duration.ofNanos(0)};
        participants.stream().sorted().limit(4).forEach(participant -> raceDuration[0] = raceDuration[0].plus(participant.getRaceDuration()));

        return String.format("%02d:%02d:%02d", raceDuration[0].toHours(), raceDuration[0].toMinutesPart(), raceDuration[0].toSecondsPart());

    }
}
