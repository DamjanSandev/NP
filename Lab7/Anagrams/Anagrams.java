package Napredno.Lab7.Anagrams;

import java.io.InputStream;
import java.util.*;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        Scanner sc = new Scanner(inputStream);
        Map<String, List<String>> map = new LinkedHashMap<>();
        while (sc.hasNext()) {
            String word = sc.nextLine();
            char[] arr = word.toCharArray();
            Arrays.sort(arr);
            String sortedWord = new String(arr);
            if (!map.containsKey(sortedWord)) {
                map.put(sortedWord, new ArrayList<>());
            }
            map.get(sortedWord).add(word);
        }
        map.values().stream().filter(strings -> strings.size() >= 5).forEach(strings -> System.out.println(String.join(" ", strings)));
    }

}

