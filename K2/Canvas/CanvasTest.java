package Napredno.K2.Canvas;


import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;


public class CanvasTest {

    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        try {
            canvas.readShapes(System.in);
        } catch (InvalidDimensionException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}

class InvalidIDException extends Exception {
    public InvalidIDException(String message) {
        super(message);
    }
}

class InvalidDimensionException extends Exception {
    public InvalidDimensionException() {
        super("Dimension 0 is not allowed!");
    }
}

interface Shape extends Comparable<Shape> {
    double perimeter();

    double area();

    @Override
    default int compareTo(Shape o) {
        return Double.compare(perimeter(), o.perimeter());
    }

    void scale(double coef);
}

class Circle implements Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double perimeter() {
        return radius * 2 * Math.PI;
    }

    @Override
    public double area() {
        return radius * radius * Math.PI;
    }

    @Override
    public void scale(double coef) {
        radius *= coef;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f", radius, area(), perimeter());
    }
}

class Square implements Shape {

    private double side;

    public Square(double side) {
        this.side = side;
    }

    @Override
    public double perimeter() {
        return side * 4;
    }

    @Override
    public double area() {
        return side * side;
    }

    @Override
    public void scale(double coef) {
        side *= coef;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f", side, area(), perimeter());
    }
}

class Rectangle implements Shape {

    private double width, height;

    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public double perimeter() {
        return 2 * width + 2 * height;
    }

    @Override
    public double area() {
        return width * height;
    }

    @Override
    public void scale(double coef) {
        width *= coef;
        height *= coef;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f", width, height, area(), perimeter());
    }
}


class Canvas {
    private final Map<String, Set<Shape>> shapesbyId;
    private final Set<Shape> shapes;

    public Canvas() {
        shapesbyId = new HashMap<>();
        shapes = new TreeSet<>(Comparator.comparing(Shape::area));
    }

    public boolean checkID(String id) {
        if (id.length() != 6) return false;
        for (int i = 0; i < id.length(); i++) {
            if (!Character.isLetterOrDigit(id.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public void readShapes(InputStream in) throws InvalidDimensionException {
        Scanner sc = new Scanner(in);
        while (sc.hasNext()) {
            String[] parts = sc.nextLine().split("\\s+");
            String id = parts[1];
            try {
                if (!checkID(id)) {
                    throw new InvalidIDException("ID " + id + " is not valid");
                }
                String type = parts[0];
                if (type.equals("1")) {
                    double radius = Double.parseDouble(parts[2]);
                    if (radius == 0) {
                        throw new InvalidDimensionException();
                    }
                    shapesbyId.putIfAbsent(id, new TreeSet<>(Comparator.reverseOrder()));
                    Circle c = new Circle(radius);
                    shapesbyId.get(id).add(c);
                    shapes.add(c);
                } else if (type.equals("2")) {
                    double side = Double.parseDouble(parts[2]);
                    if (side == 0) {
                        throw new InvalidDimensionException();
                    }
                    shapesbyId.putIfAbsent(id, new TreeSet<>(Comparator.reverseOrder()));
                    Square s = new Square(side);
                    shapesbyId.get(id).add(s);
                    shapes.add(s);
                } else {
                    double width = Double.parseDouble(parts[2]);
                    double heigth = Double.parseDouble(parts[3]);
                    if (width == 0 || heigth == 0) {
                        throw new InvalidDimensionException();
                    }
                    shapesbyId.putIfAbsent(id, new TreeSet<>(Comparator.reverseOrder()));
                    Rectangle r = new Rectangle(width, heigth);
                    shapesbyId.get(id).add(r);
                    shapes.add(r);

                }

            } catch (InvalidIDException e) {
                System.out.println(e.getMessage());
            }

        }

    }

    public void printAllShapes(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        shapes.forEach(System.out::println);
        pw.flush();
    }

    public void scaleShapes(String userId, double coef) {
        if (!shapesbyId.containsKey(userId)) return;
        shapesbyId.get(userId).forEach(shape -> shape.scale(coef));
    }

    public void printByUserId(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        shapesbyId.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Set<Shape>>, Integer>comparing(entry -> entry.getValue().size()).
                        reversed()
                        .thenComparing(entry -> entry.getValue().stream().mapToDouble(Shape::perimeter).sum()))
                .forEach(entry -> {
                    pw.println("Shapes of user: " + entry.getKey());
                    entry.getValue().stream()
                            .sorted()
                            .forEach(shape -> pw.println(shape.toString()));
                });

        pw.flush();
    }

    public void statistics(OutputStream out) {
        PrintWriter pw = new PrintWriter(out);
        DoubleSummaryStatistics summaryStatistics = shapes.stream().mapToDouble(Shape::area).summaryStatistics();
        pw.printf("count: %d\n", summaryStatistics.getCount());
        pw.printf("sum: %.2f\n", summaryStatistics.getSum());
        pw.printf("min: %.2f\n", summaryStatistics.getMin());
        pw.printf("average: %.2f\n", summaryStatistics.getAverage());
        pw.printf("max: %.2f", summaryStatistics.getMax());
        pw.flush();
    }
}
