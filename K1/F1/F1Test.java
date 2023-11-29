package Napredno.K1.F1;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class F1Test {

    public static void main(String[] args) throws IOException {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }

}

class Lap implements Comparable<Lap>{
    protected int minutes,seconds,milliseconds;

    public Lap(String line){
        String []parts=line.split(":");
        minutes=Integer.parseInt(parts[0]);
        seconds=Integer.parseInt(parts[1]);
        milliseconds=Integer.parseInt(parts[2]);
    }

    @Override
    public int compareTo(Lap o) {
        if (minutes != o.minutes) {
            return minutes - o.minutes;
        }

        if (seconds != o.seconds) {
            return seconds - o.seconds;
        }

        if (milliseconds != o.milliseconds) {
            return milliseconds - o.milliseconds;
        }

        return 0;
    }
    @Override
    public String toString() {
        return String.format("%d:%02d:%03d", minutes, seconds, milliseconds);
    }
}
class F1Driver implements Comparable<F1Driver> {
    protected String name;
    List<Lap> laps;

    public F1Driver(String line){
        String []parts=line.split("\\s+");
        name=parts[0];
        laps=new ArrayList<>(3);
        for (int i = 1; i <= 3; i++) {
            laps.add(new Lap(parts[i]));
        }
    }
    public Lap getFastestLap(){
        return Collections.min(laps);
    }
    @Override
    public int compareTo(F1Driver o) {
        return  getFastestLap().compareTo(o.getFastestLap());
    }
    @Override
    public String toString() {
        return String.format("%-10s%10s", name, getFastestLap());
    }
}
class F1Race{
    List<F1Driver>drivers;
    public F1Race(){
        drivers=new ArrayList<>();
    }
    void readResults(InputStream is) throws IOException {
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        drivers=br.lines().map(F1Driver::new).collect(Collectors.toList());
        br.close();
    }
    void printSorted(OutputStream os){
        PrintWriter pw=new PrintWriter(os);
        Collections.sort(drivers);
         for (int i = 0; i < drivers.size(); i++) {
            pw.println(i+1 + ". " + drivers.get(i));
        }
        pw.flush();
        pw.close();

    }
}
