package Napredno.K1.Canvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum Color {
    RED, GREEN, BLUE
}
public class ShapesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println("ORIGNAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }
}

interface Scalable{
    void scale(float scaleFactor);
}
interface Stackable extends Comparable<Stackable>{
    float weight();//vrakja plostina
    @Override
    default int compareTo(Stackable o){
        return Float.compare(weight(),o.weight());
    }
}
abstract class Shape implements Stackable,Scalable{
    protected String id;
    protected Color color;

    public Shape(String id, Color color) {
        this.id = id;
        this.color = color;
    }
}
class Circle extends Shape{
     private float radius;

    public Circle(String id, Color color,float radius) {
        super(id, color);
        this.radius=radius;
    }

    @Override
    public void scale(float scaleFactor) {
         radius*=scaleFactor;
    }

    @Override
    public float weight() {
       return (float) (radius*radius*Math.PI);
    }

    @Override
    public String toString() {
        return String.format("C: %-5s%-10s%10.2f",id,color.toString(),weight());
    }
}
class Rectange extends Shape{
    private float width,height;

    public Rectange(String id, Color color,float width,float height) {
        super(id, color);
        this.height=height;
        this.width=width;
    }

    @Override
    public void scale(float scaleFactor) {
      width*=scaleFactor;
      height*=scaleFactor;
    }

    @Override
    public float weight() {
        return width*height;
    }
    @Override
    public String toString() {
        return String.format("R: %-5s%-10s%10.2f",id,color.toString(),weight());
    }

}
class Canvas {
    private final List<Shape> shapes;
    public Canvas(){
        shapes=new ArrayList<>();
    }
    public void add(String id, Color color, float radius){
        Shape toAdd=new Circle(id,color,radius);
        toAdd(toAdd);
    }
    public void add(String id, Color color, float width, float height){
        Shape toAdd=new Rectange(id,color,width,height);
        toAdd(toAdd);
    }

    private void toAdd(Shape toAdd) {
        if(shapes.isEmpty()){
            shapes.add(toAdd);
            return;
        }
        for (int i = 0; i < shapes.size(); i++) {
            if(toAdd.compareTo(shapes.get(i))>0){
                shapes.add(i,toAdd);
                return;
            }
        }
        shapes.add(toAdd);
    }

    public void scale(String id, float scaleFactor){
        Shape toFind = null;
        for (int i = 0; i < shapes.size(); i++) {
            if(id.equals(shapes.get(i).id)){
                toFind=shapes.get(i);
                shapes.remove(i);
                break;
            }
        }
        assert toFind != null;
        toFind.scale(scaleFactor);
        toAdd(toFind);

    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        for (Shape s : shapes) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}
