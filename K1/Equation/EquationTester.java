package Napredno.K1.Equation;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


class Line {
    Double coeficient;
    Double x;
    Double intercept;

    public Line(Double coeficient, Double x, Double intercept) {
        this.coeficient = coeficient;
        this.x = x;
        this.intercept = intercept;
    }

    public static Line createLine(String line) {
        String[] parts = line.split("\\s+");
        return new Line(
                Double.parseDouble(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2])
        );
    }

    public double calculateLine() {
        return coeficient * x + intercept;
    }

    @Override
    public String toString() {
        return String.format("%.2f * %.2f + %.2f", coeficient, x, intercept);
    }
}
class Equation <T1,T2>{
    Supplier<T1> supplier;
    Function<T1,T2>function;
    public Equation(Supplier<T1> supplier, Function<T1, T2> function) {
        this.supplier = supplier;
        this.function = function;
    }
    public Optional<T2> calculate(){
        return Optional.of(function.apply(supplier.get()));

    }
}
class EquationProcessor{
    public static <T1,T2> void process(List<T1> list,List<Equation<T1,T2>> equations){
        for (T1 l:list){
            System.out.println("Input: " + l);
        }
        for (Equation<T1,T2> e:equations){
            if(e.calculate().isPresent()){
                System.out.println("Result: " + e.calculate().get());
            }
        }

    }
}
public class EquationTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int testCase = Integer.parseInt(sc.nextLine());

        if (testCase == 1) { // Testing with Integer, Integer
            List<Equation<Integer, Integer>> equations1 = new ArrayList<>();
            List<Integer> inputs = new ArrayList<>();
            while (sc.hasNext()) {
                inputs.add(Integer.parseInt(sc.nextLine()));
            }

            // TODO: Add an equation where you get the 3rd integer from the inputs list, and the result is the sum of that number and the number 1000.
            equations1.add(new Equation<>(
                    ()->inputs.get(2),s->inputs.get(2)+1000
            ));


            // TODO: Add an equation where you get the 4th integer from the inputs list, and the result is the maximum of that number and the number 100.
            equations1.add(new Equation<>(
                    ()->inputs.get(3),s->Math.max(inputs.get(3),100)
            ));

            EquationProcessor.process(inputs, equations1);

        } else { // Testing with Line, Integer
            List<Equation<Line, Double>> equations2 = new ArrayList<>();
            List<Line> inputs = new ArrayList<>();
            while (sc.hasNext()) {
                inputs.add(Line.createLine(sc.nextLine()));
            }

            //TODO Add an equation where you get the 2nd line, and the result is the value of y in the line equation.
            equations2.add(new Equation<>(
                    ()->inputs.get(1),
                    s->inputs.get(1).calculateLine()
            ));

            //TODO Add an equation where you get the 1st line, and the result is the sum of all y values for all lines that have a greater y value than that equation.
            equations2.add(new Equation<>(
                    ()->inputs.get(0),
                    s->inputs.stream().filter(line -> line.calculateLine() > inputs.get(0).calculateLine()).mapToDouble(Line::calculateLine).sum()
            ));

            EquationProcessor.process(inputs, equations2);
        }
    }
}
