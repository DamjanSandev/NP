package Napredno.K1.shapes2;

import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.*;

public class Shapes2Test {

    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);


    }
}

class IrregularCanvasException extends Exception{
    public IrregularCanvasException(String message) {
        super(message);
    }
}
enum ShapeType{
    CIRCLE,
    SQUARE
}

abstract class Shape implements Comparable<Shape> {
    protected ShapeType type;
    protected int side;

    protected Shape(ShapeType type, int side) {
        this.type = type;
        this.side = side;
    }

    public ShapeType getType() {
        return type;
    }

    public abstract double getArea();

    @Override
    public int compareTo(Shape o) {
        return Double.compare(getArea(), o.getArea());
    }
}
class Square extends Shape{
    public Square(int side) {
        super(ShapeType.SQUARE,side);
    }

    @Override
    public double getArea() {
        return side*4;
    }
}
class Circle extends Shape{
    public Circle(int side) {
        super(ShapeType.CIRCLE, side);
    }

    @Override
    public double getArea() {
        return Math.PI * side *side;
    }
}


class Canvas implements Comparable<Canvas> {
    private final String ID;
    private final List<Shape> list;

    public Canvas(String str) {
        this.list = new ArrayList<Shape>();

        String[] split = str.split("\\s+");
        this.ID = split[0];

        for (int i = 1; i < split.length; i += 2) {
            if (split[i].equals("C")) {
                list.add(new Circle(Integer.parseInt(split[i + 1])));
            } else if (split[i].equals("S")) {
                list.add(new Square(Integer.parseInt(split[i + 1])));
            }
        }
    }

    public double getMax() {
        return Collections.max(list).getArea();
    }

    public double getMin() {
        return Collections.min(list).getArea();
    }

    public double getArea() {
        return list.stream().mapToDouble(Shape::getArea).sum();
    }

    public  double averageArea() {
        return list.stream().mapToDouble(Shape::getArea).average().getAsDouble();
    }

    public long totalCircles() {
        return list.stream().filter(i -> i.getType().equals(ShapeType.CIRCLE)).count();
    }

    public long totalSquares() {
        return list.stream().filter(i -> i.getType().equals(ShapeType.SQUARE)).count();
    }

    public String getID() {
        return ID;
    }

    @Override
    public int compareTo(Canvas o) {
        return Double.compare(getArea(), o.getArea());
    }

    @Override
    public String toString() {
        return String.format("%s %d %d %d %.2f %.2f %.2f", ID, list.size(), totalCircles(), totalSquares(), getMin(), getMax(), averageArea());
    }
}

class ShapesApplication {
    private final List<Canvas> list;
    private final double maxArea;

    public ShapesApplication(double maxArea) {
        this.list = new ArrayList<Canvas>();
        this.maxArea = maxArea;
    }

    private void addCanvas(Canvas c) throws IrregularCanvasException {
        if (c.getMax() > maxArea) {
            throw new IrregularCanvasException(String.format("Canvas %s has a shape with area larger than %.2f", c.getID(), maxArea));
        }
        list.add(c);
    }

    public void readCanvases(InputStream in) {
        Scanner scanner = new Scanner(in);

        while (scanner.hasNextLine()) {
            try {
                addCanvas(new Canvas(scanner.nextLine()));
            } catch (IrregularCanvasException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void printCanvases(PrintStream out) {
        PrintWriter pw=new PrintWriter(out);
        list.sort(Collections.reverseOrder());
        list.forEach(pw::println);
        pw.flush();
        pw.close();
    }
}