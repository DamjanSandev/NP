package Napredno.K1.WeatherStation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WeatherStationTest {
    public static void main(String[] args) throws ParseException {
        Scanner scanner = new Scanner(System.in);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        int n = scanner.nextInt();
        scanner.nextLine();
        WeatherStation ws = new WeatherStation(n);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("=====")) {
                break;
            }
            String[] parts = line.split(" ");
            float temp = Float.parseFloat(parts[0]);
            float wind = Float.parseFloat(parts[1]);
            float hum = Float.parseFloat(parts[2]);
            float vis = Float.parseFloat(parts[3]);
            line = scanner.nextLine();
            Date date = df.parse(line);
            ws.addMeasurment(temp, wind, hum, vis, date);
        }
        String line = scanner.nextLine();
        Date from = df.parse(line);
        line = scanner.nextLine();
        Date to = df.parse(line);
        scanner.close();
        System.out.println(ws.total());
        try {
            ws.status(from, to);
        } catch (RuntimeException e) {
            System.out.println(e);
        }
    }
}

// vashiot kod ovde
class Measurement implements Comparable<Measurement>{
    protected double temperature;
    protected double wind;
    protected double humidity;
    protected double visibility;
    protected Date date;

    public Measurement(double temperature, double wind, double humidity, double visibility, Date date) {
        this.temperature=temperature;
        this.wind = wind;
        this.humidity = humidity;
        this.visibility = visibility;
        this.date = date;
    }

    @Override
    public String toString() {
        String str=date.toString();
        str=str.replace("UTC","GMT");
        return String.format("%.1f %.1f km/h %.1f%% %.1f km %s",temperature,wind,humidity,visibility,str);
    }

    @Override
    public int compareTo(Measurement o) {
        return date.compareTo(o.date);
    }
}
class WeatherStation{
    private final int days;
    List<Measurement> measurements;

    public WeatherStation(int days) {
        this.days = days;
        measurements=new ArrayList<>();
    }
    public void addMeasurment(float temperature, float wind, float humidity, float visibility, Date date){
        long ms= (long) days *24*3600*1000;
        measurements.removeIf(m -> date.getTime() - m.date.getTime() > ms);
        if(measurements.isEmpty()){
            measurements.add(new Measurement(temperature,wind,humidity,visibility,date));
            return;
        }
        if(Math.abs(date.getTime()-measurements.get(measurements.size()-1).date.getTime())<2.5*60*1000){
            return;
        }
        measurements.add(new Measurement(temperature,wind,humidity,visibility,date));
    }
    public int total(){
        return measurements.size();
    }
    public void status(Date from, Date to){
        List<Measurement> tmp=new ArrayList<>();
        for (Measurement m : measurements) {
            if(m.date.getTime()>= from.getTime() && m.date.getTime()<=to.getTime()){
                tmp.add(m);
            }
        }
        if(tmp.isEmpty())throw new RuntimeException();
        tmp.stream().sorted().forEach(System.out::println);
        double avg=tmp.stream().mapToDouble(t-> t.temperature).sum()/tmp.size();
        System.out.printf("Average temperature: %.2f",avg);
    }
}