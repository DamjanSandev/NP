package Napredno.Lab5.TimeStamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class SchedulerTest {

    static final LocalDateTime TIME = LocalDateTime.of(2016, 10, 25, 10, 15);

    public static void main(String[] args) {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Timestamp with String
            Timestamp<String> t = new Timestamp<>(TIME, jin.next());
            System.out.println(t);
            System.out.println(t.getTime());
            System.out.println(t.getElement());
        }
        if (k == 1) { //test Timestamp with ints
            Timestamp<Integer> t1 = new Timestamp<>(TIME, jin.nextInt());
            System.out.println(t1);
            System.out.println(t1.getTime());
            System.out.println(t1.getElement());
            Timestamp<Integer> t2 = new Timestamp<>(TIME.plusDays(10), jin.nextInt());
            System.out.println(t2);
            System.out.println(t2.getTime());
            System.out.println(t2.getElement());
            System.out.println(t1.compareTo(t2));
            System.out.println(t2.compareTo(t1));
            System.out.println(t1.equals(t2));
            System.out.println(t2.equals(t1));
        }
        if (k == 2) {//test Timestamp with String, complex
            Timestamp<String> t1 = new Timestamp<>(ofEpochMS(jin.nextLong()), jin.next());
            System.out.println(t1);
            System.out.println(t1.getTime());
            System.out.println(t1.getElement());
            Timestamp<String> t2 = new Timestamp<>(ofEpochMS(jin.nextLong()), jin.next());
            System.out.println(t2);
            System.out.println(t2.getTime());
            System.out.println(t2.getElement());
            System.out.println(t1.compareTo(t2));
            System.out.println(t2.compareTo(t1));
            System.out.println(t1.equals(t2));
            System.out.println(t2.equals(t1));
        }
        if (k == 3) { //test Scheduler with String
            Scheduler<String> scheduler = new Scheduler<>();
            LocalDateTime now = LocalDateTime.now();
            scheduler.add(new Timestamp<>(now.minusHours(2), jin.next()));
            scheduler.add(new Timestamp<>(now.minusHours(1), jin.next()));
            scheduler.add(new Timestamp<>(now.minusHours(4), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(2), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(4), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(1), jin.next()));
            scheduler.add(new Timestamp<>(now.plusHours(5), jin.next()));
            System.out.println(scheduler.next().getElement());
            System.out.println(scheduler.last().getElement());
            List<Timestamp<String>> result = scheduler.getAll(now.minusHours(3), now.plusHours(4).plusMinutes(15));
            String out = result.stream()
                    .sorted()
                    .map(Timestamp::getElement)
                    .collect(Collectors.joining(", "));
            System.out.println(out);
        }
        if (k == 4) {//test Scheduler with ints complex
            Scheduler<Integer> scheduler = new Scheduler<>();
            int counter = 0;
            ArrayList<Timestamp<Integer>> forRemoval = new ArrayList<>();
            while (jin.hasNextLong()) {
                Timestamp<Integer> ti = new Timestamp<>(ofEpochMS(jin.nextLong()), jin.nextInt());
                if ((counter & 7) == 0) {
                    forRemoval.add(ti);
                }
                scheduler.add(ti);
                ++counter;
            }
            jin.next();

            while (jin.hasNextLong()) {
                LocalDateTime left = ofEpochMS(jin.nextLong());
                LocalDateTime right = ofEpochMS(jin.nextLong());
                List<Timestamp<Integer>> res = scheduler.getAll(left, right);
                Collections.sort(res);
                System.out.println(left + " <: " + print(res) + " >: " + right);
            }
            System.out.println("test");
            List<Timestamp<Integer>> res = scheduler.getAll(ofEpochMS(0), ofEpochMS(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
            forRemoval.forEach(scheduler::remove);
            res = scheduler.getAll(ofEpochMS(0), ofEpochMS(Long.MAX_VALUE));
            Collections.sort(res);
            System.out.println(print(res));
        }
    }

    private static LocalDateTime ofEpochMS(long ms) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(ms), ZoneId.systemDefault());
    }

    private static <T> String print(List<Timestamp<T>> res) {
        if (res == null || res.size() == 0) return "NONE";
        return res.stream()
                .map(each -> each.getElement().toString())
                .collect(Collectors.joining(", "));
    }

}

class Timestamp <T> implements Comparable<Timestamp<?>>{
    private final LocalDateTime localDateTime;
    private final T element;

    public Timestamp(LocalDateTime localDateTime, T element) {
        this.localDateTime = localDateTime;
        this.element = element;
    }

    public LocalDateTime getTime() {
        return localDateTime;
    }

    public T getElement() {
        return element;
    }


    @Override
    public int compareTo(Timestamp<?> o) {
        return localDateTime.compareTo(o.getTime());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Timestamp<?> timestamp = (Timestamp<?>) o;
        return Objects.equals(localDateTime, timestamp.localDateTime);
    }

    @Override
    public String toString() {
        return localDateTime + " " + element;
    }
}
class Scheduler <T> {
    List<Timestamp<T>> timestamps;

    public Scheduler() {
        timestamps = new ArrayList<>();
    }

    public void add(Timestamp<T> t) {
        timestamps.add(t);
        Collections.sort(timestamps);
    }

    public boolean remove(Timestamp<T> t) {
        for (Timestamp<T> i : timestamps) {
            if (i.equals(t)) {
                timestamps.remove(i);
                return true;
            }
        }
        return false;
    }

    public Timestamp<T> next() {
       return timestamps.stream().filter(x -> x.getTime().isAfter(LocalDateTime.now())).min(Timestamp::compareTo).orElse(null);
    }

    public Timestamp<T> last() {
        return timestamps.stream().filter(x -> x.getTime().isBefore(LocalDateTime.now())).max(Timestamp::compareTo).orElse(null);
    }
    public List<Timestamp<T>> getAll(LocalDateTime begin, LocalDateTime end){
        List<Timestamp<T>> res=new ArrayList<>();
        for (Timestamp<T> i : timestamps) {
            if(i.getTime().isAfter(begin) && i.getTime().isBefore(end)){
                res.add(i);
            }
        }
        return res;
    }


}

