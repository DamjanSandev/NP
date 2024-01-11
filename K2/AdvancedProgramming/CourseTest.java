package Napredno.K2.AdvancedProgramming;

//package mk.ukim.finki.midterm;

import java.util.*;
import java.util.stream.Collectors;


public class CourseTest {

    public static void printStudents(List<Student> students) {
        students.forEach(System.out::println);
    }

    public static void printMap(Map<Integer, Integer> map) {
        map.forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
    }

    public static void main(String[] args) {
        AdvancedProgrammingCourse advancedProgrammingCourse = new AdvancedProgrammingCourse();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            String command = parts[0];

            if (command.equals("addStudent")) {
                String id = parts[1];
                String name = parts[2];
                advancedProgrammingCourse.addStudent(new Student(id, name));
            } else if (command.equals("updateStudent")) {
                String idNumber = parts[1];
                String activity = parts[2];
                int points = Integer.parseInt(parts[3]);
                advancedProgrammingCourse.updateStudent(idNumber, activity, points);
            } else if (command.equals("getFirstNStudents")) {
                int n = Integer.parseInt(parts[1]);
                printStudents(advancedProgrammingCourse.getFirstNStudents(n));
            } else if (command.equals("getGradeDistribution")) {
                printMap(advancedProgrammingCourse.getGradeDistribution());
            } else {
                advancedProgrammingCourse.printStatistics();
            }
        }
    }
}

class Student {
    private final String index;
    private final String name;
    private int firstMidTerm;
    private int secondMidTerm;
    private int labExercises;

    public Student(String index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public void setFirstMidTerm(int firstMidTerm) throws Exception {
        if (firstMidTerm < 0 || firstMidTerm > 100) {
            throw new Exception();
        }
        this.firstMidTerm = firstMidTerm;
    }

    public void setSecondMidTerm(int secondMidTerm) throws Exception {
        if (secondMidTerm < 0 || secondMidTerm > 100) {
            throw new Exception();
        }
        this.secondMidTerm = secondMidTerm;
    }

    public void setLabExercises(int labExercises) throws Exception {
        if (labExercises < 0 || labExercises > 10) {
            throw new Exception();
        }
        this.labExercises = labExercises;
    }

    public double summaryPoints() {
        return firstMidTerm * 0.45 + secondMidTerm * 0.45 + labExercises;
    }

    public int getGrade() {
        int grade = (int) Math.ceil(summaryPoints() / 10);
        if (grade < 6) return 5;
        return grade;
    }

    @Override
    public String toString() {
        return String.format("ID: %s Name: %s First midterm: %d Second midterm %d Labs: %d Summary points: %.2f Grade: %d", index, name, firstMidTerm, secondMidTerm, labExercises, summaryPoints(), getGrade());
    }
}

class AdvancedProgrammingCourse {

    Map<String, Student> studentsById;


    public AdvancedProgrammingCourse() {
        studentsById = new HashMap<>();
    }

    public void addStudent(Student student) {
        studentsById.put(student.getIndex(), student);
    }

    public void updateStudent(String idNumber, String activity, int points) {
        try {
            if (activity.equals("midterm1")) {
                studentsById.get(idNumber).setFirstMidTerm(points);
            } else if (activity.equals("midterm2")) {
                studentsById.get(idNumber).setSecondMidTerm(points);
            } else {
                studentsById.get(idNumber).setLabExercises(points);
            }
        } catch (Exception ignored) {
        }
    }

    public List<Student> getFirstNStudents(int n) {
        return studentsById.values().stream().sorted(Comparator.comparing(Student::summaryPoints).reversed()).limit(n).collect(Collectors.toList());
    }

    public Map<Integer, Integer> getGradeDistribution() {
        Map<Integer, Integer> map = studentsById.values().stream().collect(Collectors.groupingBy(
                Student::getGrade,
                TreeMap::new,
                Collectors.summingInt(i -> 1)

        ));
        for (int i = 5; i <= 10; i++) {
            map.putIfAbsent(i, 0);
        }
        return map;
    }

    public void printStatistics() {
        DoubleSummaryStatistics doubleSummaryStatistics = studentsById.values().stream().filter(student -> student.getGrade() > 5).mapToDouble(Student::summaryPoints).summaryStatistics();
        System.out.printf("Count: %d Min: %.2f Average: %.2f Max: %.2f", doubleSummaryStatistics.getCount(), doubleSummaryStatistics.getMin(), doubleSummaryStatistics.getAverage(), doubleSummaryStatistics.getMax());
    }
}