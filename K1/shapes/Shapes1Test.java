package Napredno.K1.shapes;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;

public class Shapes1Test {

    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);
    }
}
class ShapesApplication{
     private  String canvas_id;
     private int max;

     private int max_lenght;
    public ShapesApplication() {
        max=0;
        max_lenght=0;
    }
    public int readCanvases(InputStream inputStream){
        Scanner sc=new Scanner(inputStream);
        int count=0;
        boolean first=true;
        while (sc.hasNextLine()){
            String [] read= sc.nextLine().split(" \\s+");
            if(first) {
                canvas_id = read[0];
                first=false;
            }
            count+=read.length-1;
            int perimeter=0;
            for (int i = 1; i < read.length; i++) {
                   perimeter+=(Integer.parseInt(read[i])*4);
            }
            if(perimeter>max){
                max=perimeter;
                canvas_id=read[0];
                max_lenght=read.length-1;
            }

        }
        return count;
    }
    public void printLargestCanvasTo(OutputStream outputStream){
        System.out.println(canvas_id +" "+ max_lenght+ " " + max );
    }
}