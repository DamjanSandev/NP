package Napredno.K1.TimeTable;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TimesTest {

    public static void main(String[] args) {
        TimeTable timeTable = new TimeTable();
        try {
            timeTable.readTimes(System.in);
        } catch (UnsupportedFormatException e) {
            System.out.println("UnsupportedFormatException: " + e.getMessage());
        } catch (InvalidTimeException e) {
            System.out.println("InvalidTimeException: " + e.getMessage());
        }
        System.out.println("24 HOUR FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_24);
        System.out.println("AM/PM FORMAT");
        timeTable.writeTimes(System.out, TimeFormat.FORMAT_AMPM);
    }

}

enum TimeFormat {
    FORMAT_24, FORMAT_AMPM
}
class Time implements Comparable<Time>{
    int hours,minutes;
    public Time(String line) throws UnsupportedFormatException ,InvalidTimeException {
        line=line.replace('.',':');
        String[]parts=line.split(":");
        if(parts.length==1)throw new UnsupportedFormatException(line);
        hours=Integer.parseInt(parts[0]);
        minutes=Integer.parseInt(parts[1]);
        if(!valid())throw new InvalidTimeException(line);
    }
    public boolean valid(){
        if(hours<0 || hours>23 || minutes<0 || minutes>59)return false;
        return true;
    }

    @Override
    public int compareTo(Time o) {
        if(hours!=o.hours){
            return hours-o.hours;
        }
        return minutes-o.minutes;
    }
   public void changetoAM_PM(OutputStream outputStream){
        PrintWriter pw=new PrintWriter(outputStream);
        if(hours==0){
         hours+=12;
            pw.println(this +" AM");
        }
        else if(hours>0 && hours<12){
            pw.println(this +" AM");
        }
        else if(hours==12){
            pw.println(this+" PM");
        }
        else{
            hours-=12;
            pw.println(this+" PM");
        }
        pw.flush();
   }
    @Override
    public String toString() {
        if(hours>9)
          return String.format("%d:%02d",hours,minutes);
        else return String.format(" %d:%02d",hours,minutes);
    }
}
class UnsupportedFormatException extends Exception{
    public UnsupportedFormatException(String message) {
        super(message);
    }
}
class InvalidTimeException extends Exception{
    public InvalidTimeException(String message) {
        super(message);
    }
}
class TimeTable{
   List<Time> times;

    public TimeTable() {
        times=new ArrayList<>();
    }
    public void readTimes(InputStream inputStream) throws InvalidTimeException, UnsupportedFormatException {
        Scanner sc=new Scanner(inputStream);
        while (sc.hasNextLine()){
            String line= sc.nextLine();
            String []parts=line.split("\\s+");
            for (int i = 0; i < parts.length; i++) {
                  times.add(new Time(parts[i]));
            }
        }
    }
    public void writeTimes(OutputStream outputStream, TimeFormat format){
        PrintWriter pw=new PrintWriter(outputStream);
        if(format==TimeFormat.FORMAT_24){
            times.stream().sorted().forEach(pw::println);

        }
        else times.stream().sorted().forEach(time -> time.changetoAM_PM(outputStream));

       pw.flush();
    }

}