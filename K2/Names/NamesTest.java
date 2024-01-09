package Napredno.K2.Names;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}

// vashiot kod ovde

class Names {
    Map<String, Integer> namesByOccurences;
    Set<String> names;

    public Names() {
        namesByOccurences = new TreeMap<>();
        names = new TreeSet<>();
    }

    public void addName(String name) {
        namesByOccurences.putIfAbsent(name, 0);
        namesByOccurences.computeIfPresent(name, (k, v) -> ++v);
        names.add(name);
    }

    public void printN(int n) {
        namesByOccurences.entrySet().stream().sorted(Entry.comparingByKey())
                .filter(stringIntegerEntry -> stringIntegerEntry.getValue() >= n)
                .forEach(stringIntegerEntry -> System.out.println(stringIntegerEntry.getKey() + " (" + stringIntegerEntry.getValue() + ") " + findNumberOfUniqueLetters(stringIntegerEntry.getKey())));
    }

    private int findNumberOfUniqueLetters(String name) {
        return name.toLowerCase().chars().mapToObj(c -> (char) c).collect(Collectors.toSet()).size();
    }

    public String findName(int len, int index) {
        List<String> filteredByLength = names.stream().filter(s -> s.length() < len).collect(Collectors.toList());
        return filteredByLength.get((filteredByLength.size() + index) % filteredByLength.size());
    }
}
