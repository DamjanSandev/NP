package Napredno.K2.OnlinePayments;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class OnlinePaymentsTest {
    public static void main(String[] args) {
        OnlinePayments onlinePayments = new OnlinePayments();

        onlinePayments.readItems(System.in);

        IntStream.range(151020, 151025).mapToObj(String::valueOf).forEach(id -> onlinePayments.printStudentReport(id, System.out));
    }
}

class Stavka {
    private final String description;
    private final int price;

    public Stavka(String description, int price) {
        this.description = description;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s %d", description, price);
    }
}

class Student {
    private final String id;
    private final List<Stavka> stavki;

    public Student(String id) {

        this.id = id;
        stavki = new ArrayList<>();
    }

    public void addStavka(String desc, int price) {
        stavki.add(new Stavka(desc, price));
    }

    public int getPrices() {
        return stavki.stream().mapToInt(Stavka::getPrice).sum();
    }

    public int getFee() {
        int fee = (int) Math.round((getPrices() * 1.14) / 100);
        return fee < 3 ? 3 : Math.min(fee, 300);
    }

    public int total() {
        return getPrices() + getFee();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Student: %s Net: %d Fee: %d Total: %d\n", id, getPrices(), getFee(), total()));
        sb.append("Items:\n");
        AtomicInteger i = new AtomicInteger(1);
        stavki.stream().sorted(Comparator.comparing(Stavka::getPrice).reversed()).forEach(stavka -> sb.append(String.format("%d. %s\n", i.getAndIncrement(), stavka)));
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}

class OnlinePayments {
    private final Map<String, Student> studentsById;

    public OnlinePayments() {
        studentsById = new HashMap<>();
    }

    public void readItems(InputStream in) {
        Scanner sc = new Scanner(in);
        while (sc.hasNext()) {
            String[] parts = sc.nextLine().split(";");
            studentsById.putIfAbsent(parts[0], new Student(parts[0]));
            studentsById.get(parts[0]).addStavka(parts[1], Integer.parseInt(parts[2]));
        }

    }

    public void printStudentReport(String id, OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        if (!studentsById.containsKey(id)) {
            pw.println("Student " + id + " not found!");
        } else {
            pw.println(studentsById.get(id));
        }

        pw.flush();
    }
}
