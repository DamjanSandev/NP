package Napredno.K2.StudentRecords;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * January 2016 Exam problem 1
 */
public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

// your code here


class Student implements Comparable<Student> {
    private final String code;
    private final String smer;
    private final List<Integer> grades;

    public Student(String line) {
        String[] parts = line.split("\\s+");
        code = parts[0];
        smer = parts[1];
        grades = new ArrayList<>();
        for (int i = 2; i < parts.length; i++) {
            grades.add(Integer.parseInt(parts[i]));
        }
    }

    public String getCode() {
        return code;
    }


    public List<Integer> getGrades() {
        return grades;
    }

    public double getAverage() {
        return grades.stream().mapToDouble(i -> i).sum() / grades.size();
    }

    @Override
    public int compareTo(Student o) {
        return Comparator.comparing(Student::getAverage).reversed().thenComparing(Student::getCode).compare(this, o);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", code, getAverage());
    }
}

class StudentRecords {
    Map<String, Set<Student>> studentsBySmer;
    Map<String, Smer> smerovi;

    public StudentRecords() {
        studentsBySmer = new TreeMap<>();
        smerovi = new TreeMap<>();
    }

    public int readRecords(InputStream in) {
        int records = 0;
        Scanner sc = new Scanner(in);
        while (sc.hasNext()) {
            String line = sc.nextLine();
            String smer = line.split("\\s+")[1];
            Student student = new Student(line);
            studentsBySmer.putIfAbsent(smer, new TreeSet<>());
            studentsBySmer.get(smer).add(student);
            smerovi.putIfAbsent(smer, new Smer(smer));
            smerovi.get(smer).addGrades(student.getGrades());
            records++;
        }

        return records;
    }

    public void writeTable(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        studentsBySmer.forEach((key, value) -> {
            pw.println(key);
            value.forEach(pw::println);
        });
        pw.flush();

    }

    public void writeDistribution(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        smerovi.values().stream().sorted().forEach(pw::println);
        pw.flush();
    }
}

class Smer implements Comparable<Smer> {
    private final String name;
    private final List<Integer> grades;

    public Smer(String name) {
        this.name = name;
        grades = new ArrayList<>();
    }

    public void addGrades(List<Integer> grades) {
        this.grades.addAll(grades);
    }

    public String getName() {
        return name;
    }

    public int numOfTens() {
        return (int) grades.stream().filter(integer -> integer == 10).count();
    }

    public int gradesNumber(int grade) {
        return (int) grades.stream().filter(integer -> integer == grade).count();
    }

    @Override
    public int compareTo(Smer o) {
        return Comparator.comparing(Smer::numOfTens).reversed().compare(this, o);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("\n");
        Set<Integer> gradesFiltered = new TreeSet<>(this.grades);
        for (Integer integer : gradesFiltered) {
            int count = gradesNumber(integer);
            int charactersToPrint = count / 10;
            if (count % 10 != 0) {
                charactersToPrint++;
            }
            String characters = "*".repeat(charactersToPrint);
            sb.append(String.format("%2d | %s(%d)\n", integer, characters, count));
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}


