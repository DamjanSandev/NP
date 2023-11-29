package Napredno.Lab1;
import java.util.Scanner;
import java.util.stream.IntStream;
public class RomanConverterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        IntStream.range(0, n)
                .forEach(x -> System.out.println(RomanConverter.toRoman(scanner.nextInt())));
        scanner.close();
    }
}
class RomanConverter{

    public static String toRoman(int n) {
        // your solution here
        String[] stotki= {"","C","CC","CCC","CD","D","DC","DCC","DCCC","CM"};
        String[] edinici= {"","I","II","III","IV","V","VI","VII","VIII","IX"};
        String[] desetki= {"","X","XX","XXX","XL","L","LX","LXX","LXXX","XC"};
        String res="";
        res+=(("M").repeat(n/1000));
        res+=stotki[n/100%10];
        res+=desetki[n/10%10];
        res+=edinici[n%10];
        return res;
    }
}