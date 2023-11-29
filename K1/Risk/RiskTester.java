package Napredno.K1.Risk;

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
            System.out.println(risk.processAttacksData(System.in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
class Round{
    private final List<Integer> attacker;
    private final List<Integer> defender;

    public Round(String line){
        String []parts=line.split(";");
        attacker=parseDice(parts[0]);
        defender=parseDice(parts[1]);
    }
    public List<Integer> parseDice(String line){
       return Arrays.stream(line.split("\\s+")).map(dice->Integer.parseInt(dice)).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }
    public boolean isWinner(){
        return IntStream.range(0,attacker.size()).allMatch(i->attacker.get(i)>defender.get(i));
    }
}
class Risk{

    public int processAttacksData(InputStream is) throws IOException {
        BufferedReader br= new BufferedReader(new InputStreamReader(is));
        List<Round> rounds= br.lines().map(Round::new).toList();
        br.close();
        return (int) rounds.stream().filter(Round::isWinner).count();
    }
}
