package Napredno.K2.FootballTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Partial exam II 2016/2017
 */
public class FootballTableTest {
    public static void main(String[] args) throws IOException {
        FootballTable table = new FootballTable();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines()
                .map(line -> line.split(";"))
                .forEach(parts -> table.addGame(parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[3])));
        reader.close();
        System.out.println("=== TABLE ===");
        System.out.printf("%-19s%5s%5s%5s%5s%5s\n", "Team", "P", "W", "D", "L", "PTS");
        table.printTable();
    }
}

// Your code here

class Team {

    private final String name;
    private int points = 0, wins = 0, losses = 0, draws = 0, played = 0, goals = 0;

    public Team(String name) {
        this.name = name;
    }

    public void setStats(int goalsScored, int goalsConceded) {
        if (goalsScored > goalsConceded) {
            points += 3;
            wins++;
        } else if (goalsScored == goalsConceded) {
            points++;
            draws++;
        } else {
            losses++;
        }
        this.goals += (goalsScored - goalsConceded);
        this.played++;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int goalDifference() {
        return goals;
    }

    @Override
    public String toString() {
        return String.format("%-15s%5d%5d%5d%5d%5d", name, played, wins, draws, losses, points);
    }
}

class FootballTable {
    private final Map<String, Team> teams;
    private final Comparator<Team> teamComparator = Comparator.comparing(Team::getPoints, Comparator.reverseOrder()).
            thenComparing(Team::goalDifference, Comparator.reverseOrder());

    public FootballTable() {
        teams = new TreeMap<>();
    }


    public void addGame(String homeTeam, String awayTeam, int homeGoals, int awayGoals) {
        teams.putIfAbsent(homeTeam, new Team(homeTeam));
        teams.putIfAbsent(awayTeam, new Team(awayTeam));

        teams.get(homeTeam).setStats(homeGoals, awayGoals);
        teams.get(awayTeam).setStats(awayGoals, homeGoals);
    }

    public void printTable() {
        List<Team> sortedTeams = teams.values().stream().sorted(teamComparator).collect(Collectors.toList());
        for (int i = 0; i < sortedTeams.size(); i++) {
            System.out.printf("%2d. %s%n", (i + 1), sortedTeams.get(i));
        }
    }
}
