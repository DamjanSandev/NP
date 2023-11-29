package Napredno.Lab1.Lab1_3;

public interface StringToDouble {
    static double StringtoDouble(String s){
        s = s.replace(',', '.');
        return Double.parseDouble(s.substring(0, s.length() - 1));
    }
}
