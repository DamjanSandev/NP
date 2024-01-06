package Napredno.Lab7.TermFrequency;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[]{"во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја"};
        TermFrequency tf = new TermFrequency(System.in,
                stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}
// vasiot kod ovde

class TermFrequency {
    Map<String, Integer> frequencyOfWord;

    public TermFrequency(InputStream inputStream, String[] stopWords) {
        frequencyOfWord = new HashMap<>();
        Scanner sc = new Scanner(inputStream);
        while (sc.hasNext()) {
            String line = sc.next();
            line = line.toLowerCase().replaceAll("[,.]", "").trim();
            if (!Arrays.asList(stopWords).contains(line) && !line.isEmpty()) {
                frequencyOfWord.computeIfPresent(line, (k, v) -> ++v);
                frequencyOfWord.putIfAbsent(line, 1);
            }
        }
    }

    public int countTotal() {
        return frequencyOfWord.values().stream().mapToInt(value -> value).sum();
    }

    public int countDistinct() {
        return frequencyOfWord.size();
    }

    public List<String> mostOften(int k) {
        return frequencyOfWord.keySet().stream().sorted(Comparator.comparing(frequencyOfWord::get).reversed().thenComparing(Object::toString)).limit(k).collect(Collectors.toList());
    }
}

