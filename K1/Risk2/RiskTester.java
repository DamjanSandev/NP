package Napredno.K1.Risk2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RiskTester {
    public static void main(String[] args) {

        Risk risk = new Risk();

        try {
            risk.processAttacksData(System.in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
class Round{
     final List<Integer> attacker;
     final List<Integer> defender;

    public Round(String line){
        String []parts=line.split(";");
        attacker=parseDice(parts[0]);
        defender=parseDice(parts[1]);
    }
    public List<Integer> parseDice(String line){
        return Arrays.stream(line.split("\\s+")).map(dice->Integer.parseInt(dice)).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }
}
class Risk{

    public void processAttacksData(InputStream is) throws IOException {
        Scanner sc=new Scanner(is);
        while (sc.hasNextLine()){
            String line=sc.nextLine();
            Round round=new Round(line);
            int attacker=0,defender=0;
            for (int i = 0; i < 3; i++) {
                if(round.attacker.get(i)>round.defender.get(i)){
                    attacker++;
                }
               else
                   defender++;
            }
            System.out.println(attacker + " " + defender);
        }
    }
}
