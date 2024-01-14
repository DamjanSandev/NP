package Napredno.K2.DailyTemperatures;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

/**
 * I partial exam 2016
 */
public class DailyTemperatureTest {
    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, 'C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, 'F');
    }
}

// Vashiot kod ovde
class Temperature {
    private double temperature;

    public Temperature(String line) {
        String tempNumber = line.substring(0, line.length() - 1);
        temperature = Double.parseDouble(tempNumber);
        if (line.charAt(line.length() - 1) == 'F') {
            temperature = farenheitToCelsius(temperature);
        }
    }

    public void celsiusToFarenheit() {
        temperature = temperature * (double) 9 / 5 + 32;
    }

    public double farenheitToCelsius(double temp) {
        return (temp - 32) * (double) 5 / 9;
    }

    public double getTemperature() {
        return temperature;
    }
}

class TemperatureDay {
    private final int day;
    private final List<Temperature> temperatures;

    public TemperatureDay(String line) {
        String[] parts = line.split("\\s+");
        day = Integer.parseInt(parts[0]);
        temperatures = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            temperatures.add(new Temperature(parts[i]));
        }
    }

    public int getDay() {
        return day;
    }

    public String Print(char scale) {
        if (scale == 'F') {
            temperatures.forEach(Temperature::celsiusToFarenheit);
        }
        DoubleSummaryStatistics summaryStatistics = temperatures.stream().mapToDouble(Temperature::getTemperature).summaryStatistics();
        double min = summaryStatistics.getMin();
        double max = summaryStatistics.getMax();
        double avg = summaryStatistics.getAverage();
        int count = (int) summaryStatistics.getCount();
        return String.format("%3d: Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c", day, count, min, scale, max, scale, avg, scale);

    }

}

class DailyTemperatures {
    List<TemperatureDay> tempsByDay;

    public DailyTemperatures() {
        tempsByDay = new ArrayList<>();
    }

    public void readTemperatures(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        tempsByDay = br.lines().map(TemperatureDay::new).collect(Collectors.toList());
    }

    public void writeDailyStats(OutputStream out, char scale) {
        PrintWriter pw = new PrintWriter(out);

        tempsByDay.stream().sorted(Comparator.comparing(TemperatureDay::getDay)).forEach(temperatureDay -> pw.println(temperatureDay.Print(scale)));

        pw.flush();
    }
}