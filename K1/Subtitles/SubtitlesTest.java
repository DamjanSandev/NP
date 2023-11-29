package Napredno.K1.Subtitles;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SubtitlesTest {
    public static void main(String[] args) {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles(System.in);
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.println(String.format("SHIFT FOR %d ms", shift));
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }
}

class Time{
    int hour,minutes,seconds,milliseconds;

    public Time(String line) {
        if(line.charAt(0) == ' ') {
            line = line.substring(1);
        }
        String []parts2=line.split(",");
        String []parts=parts2[0].split(":");
        hour=Integer.parseInt(parts[0]);
        minutes=Integer.parseInt(parts[1]);
       seconds=Integer.parseInt(parts[2]);
       milliseconds=Integer.parseInt(parts2[1]);
    }
    public void shift(int ms){
        milliseconds+=ms;
        if(milliseconds>=1000){
            milliseconds-=1000;
            seconds++;
        }
        else if(milliseconds<0){
            milliseconds+=1000;
            seconds--;
        }
        if(seconds>=60){
            seconds-=60;
            minutes++;
        }
        else if(seconds<0){
            seconds+=60;
            minutes--;
        }
        if(minutes>=60){
            minutes-=60;
            hour++;
        }
        else if(minutes<0){
            minutes+=60;
            hour--;
        }
    }
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d,%03d", hour, minutes, seconds, milliseconds);
    }
}

class Subtitle{
   int index;
   Time from,after;
   String text;

    public Subtitle(int index, Time from, Time after, String text) {
        this.index = index;
        this.from = from;
        this.after = after;
        this.text = text;
    }

    public int getIndex() {
        return index;
    }

    public Time getFrom() {
        return from;
    }
    public void shift(int ms){
        from.shift(ms);
        after.shift(ms);
    }
    public Time getAfter() {
        return after;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return String.format("%d\n%s --> %s\n%s", index, from, after, text);
    }
}
class Subtitles{
    List<Subtitle> subtitleList;
    public Subtitles() {
        subtitleList=new ArrayList<>();
    }
    public int loadSubtitles(InputStream inputStream){
        Scanner sc=new Scanner(inputStream);
        int counter=0;
        while (sc.hasNext()){
         int index=sc.nextInt();
            String from = sc.next();
            sc.next();
            String to = sc.nextLine();
            String text = "";

            while(sc.hasNextLine()) {
                String add = sc.nextLine();
                if(add.isEmpty()) break;
                text += add + "\n";
            }
            subtitleList.add(new Subtitle(index,new Time(from),new Time(to),text));
            ++counter;
        }

        return counter;
    }
    public void print(){
        subtitleList.forEach(System.out::println);
    }
    public void shift(int ms){
        for (Subtitle s : subtitleList) {
            s.shift(ms);
        }
    }
}