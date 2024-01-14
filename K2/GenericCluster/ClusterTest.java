package Napredno.K2.GenericCluster;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * January 2016 Exam problem 2
 */
public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }
}

interface Indentifier {
    long getId();

    double evqlidianDistance(Object o);
}

class Point2D implements Indentifier {
    private final long id;
    private final float x, y;

    public Point2D(long id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }


    @Override
    public long getId() {
        return id;
    }

    @Override
    public double evqlidianDistance(Object o) {
        Point2D other = (Point2D) o;
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
}

class Cluster<T extends Indentifier> {

    Map<Long, T> elements;

    public Cluster() {
        elements = new HashMap<>();
    }

    public void addItem(T point2D) {
        elements.put(point2D.getId(), point2D);
    }

    public void near(long id, int top) {
        AtomicInteger i = new AtomicInteger(1);
        elements.entrySet().stream().filter(longTEntry -> longTEntry.getKey() != id)
                .sorted(Comparator.comparing(longTEntry -> longTEntry.getValue().evqlidianDistance(elements.get(id))))
                .limit(top)
                .forEach(longTEntry -> System.out.println(i.getAndIncrement() + ". " + String.format("%d -> %.3f", longTEntry.getKey(), longTEntry.getValue().evqlidianDistance(elements.get(id)))));
    }
}