package Napredno.Lab2.Lab2_3;

import java.util.*;

enum TYPE {
    POINT,
    CIRCLE
}

enum DIRECTION {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

public class CirclesTest {

    public static void main(String[] args) throws ObjectCanNotBeMovedException {

        System.out.println("===COLLECTION CONSTRUCTOR AND ADD METHOD TEST===");
        MovablesCollection collection = new MovablesCollection(100, 100);
        Scanner sc = new Scanner(System.in);
        int samples = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < samples; i++) {
            String inputLine = sc.nextLine();
            String[] parts = inputLine.split(" ");

            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int xSpeed = Integer.parseInt(parts[3]);
            int ySpeed = Integer.parseInt(parts[4]);

            if (Integer.parseInt(parts[0]) == 0) { //point
                try {
                    collection.addMovableObject(new MovablePoint(x, y, xSpeed, ySpeed));
                } catch (MovableObjectNotFittableException e) {
                    System.out.println(e.getMessage());
                }
            } else { //circle
                int radius = Integer.parseInt(parts[5]);
                try {
                    collection.addMovableObject(new MovableCircle(radius, new MovablePoint(x, y, xSpeed, ySpeed)));
                } catch (MovableObjectNotFittableException e) {
                    System.out.println(e.getMessage());
                }
            }

        }
        System.out.println(collection.toString());

        System.out.println("MOVE POINTS TO THE LEFT");

        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.LEFT);

        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES DOWN");

        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.DOWN);



        System.out.println(collection.toString());

        System.out.println("CHANGE X_MAX AND Y_MAX");
        MovablesCollection.setxMax(90);
        MovablesCollection.setyMax(90);

        System.out.println("MOVE POINTS TO THE RIGHT");

        collection.moveObjectsFromTypeWithDirection(TYPE.POINT, DIRECTION.RIGHT);


        System.out.println(collection.toString());

        System.out.println("MOVE CIRCLES UP");

        collection.moveObjectsFromTypeWithDirection(TYPE.CIRCLE, DIRECTION.UP);


        System.out.println(collection.toString());


    }

}
class ObjectCanNotBeMovedException extends Exception{
    public ObjectCanNotBeMovedException(String message) {
         super(message);
    }
}
interface Movable  {
    void moveUp() throws ObjectCanNotBeMovedException;
    void moveLeft() throws ObjectCanNotBeMovedException;
    void moveRight() throws ObjectCanNotBeMovedException;
    void moveDown() throws ObjectCanNotBeMovedException;
    int getCurrentXPosition();
    int getCurrentYPosition();
    TYPE  getType();
}
class MovablePoint implements Movable {
    private int x,y;
    private final int xSpeed;
    private final int ySpeed;

    public MovablePoint(int x, int y, int xSpeed, int ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }

    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
        if(y+ySpeed > MovablesCollection.y_max)
            throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", x, y + ySpeed));
        this.y+=ySpeed;

    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException{
        if(x-xSpeed < 0)
            throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", x-xSpeed, y));
        this.x-=xSpeed;
    }

    @Override
    public void moveRight()throws ObjectCanNotBeMovedException {
         if(x+xSpeed > MovablesCollection.x_max){
             throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", x+xSpeed, y));
         }
         this.x+=xSpeed;
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException{
        if(y-ySpeed < 0 ){
            throw new ObjectCanNotBeMovedException(String.format("Point (%d,%d) is out of bounds", x, y - ySpeed));
        }
        this.y-=ySpeed;
    }

    @Override
    public int getCurrentXPosition() {
        return x;
    }

    @Override
    public int getCurrentYPosition() {
        return y;
    }

    @Override
    public TYPE getType() {
        return TYPE.POINT;
    }

    @Override
    public String toString() {
        return "Movable point with coordinates (" + x + "," + y + ")\n";
    }
}
class MovableCircle implements Movable{
    protected int radius;
    protected MovablePoint centerPoint;

    public MovableCircle(int radius, MovablePoint point) {
        this.radius = radius;
        this.centerPoint = point;
    }

    @Override
    public void moveUp() throws ObjectCanNotBeMovedException {
      centerPoint.moveUp();
    }

    @Override
    public void moveLeft() throws ObjectCanNotBeMovedException {
       centerPoint.moveLeft();
    }

    @Override
    public void moveRight() throws ObjectCanNotBeMovedException {
      centerPoint.moveRight();
    }

    @Override
    public void moveDown() throws ObjectCanNotBeMovedException {
       centerPoint.moveDown();
    }

    @Override
    public int getCurrentXPosition() {
        return centerPoint.getCurrentXPosition();
    }

    @Override
    public int getCurrentYPosition() {
        return centerPoint.getCurrentYPosition();
    }

    @Override
    public TYPE getType() {
        return TYPE.CIRCLE;
    }

    public int getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "Movable circle with center coordinates ("+getCurrentXPosition()+ ","+ getCurrentYPosition()+") and radius " + radius +"\n" ;
    }
}
class MovableObjectNotFittableException extends Exception{
    public MovableObjectNotFittableException(String message) {
        super(message);
    }
}

class MovablesCollection{
    List<Movable> movables;

    int count;
    protected static int x_max=0,y_max=0;
    MovablesCollection(int x_MAX, int y_MAX){
        x_max=x_MAX;
        y_max=y_MAX;
        this.count=0;
        this.movables=new ArrayList<Movable>();
    }
    public static void setxMax(int xMax){
        x_max=xMax;
    }
    public static void setyMax(int yMax){
        y_max=yMax;
    }
  public void addMovableObject(Movable m) throws MovableObjectNotFittableException {
        String type="point";
        int r=0,x=m.getCurrentXPosition(),y=m.getCurrentYPosition();
        if(m.getType()==TYPE.CIRCLE){
            r=((MovableCircle ) m  ).getRadius();
            type="circle";
        }
        if(x+r>x_max || x-r < 0 || y+r>y_max || y-r < 0){
            throw new MovableObjectNotFittableException(String.format("Movable %s with center (%d,%d) and radius %d can not be fitted into the collection",type,x,y,r));
        }

        movables.add(m);
  }
  public void moveObjectsFromTypeWithDirection (TYPE type, DIRECTION direction)throws ObjectCanNotBeMovedException{
      for (Movable m: movables) {
          if(m.getType()!=type){
              continue;
          }
          if(direction==DIRECTION.UP){
              try {
                  m.moveUp();
              }
              catch (ObjectCanNotBeMovedException e){
                  System.out.println(e.getMessage());
              }
          }
          else if(direction==DIRECTION.DOWN){
              try {
                  m.moveDown();
              }
              catch (ObjectCanNotBeMovedException e){
                  System.out.println(e.getMessage());
              }
          }
          else if(direction==DIRECTION.RIGHT){
              try {
                  m.moveRight();
              }
              catch (ObjectCanNotBeMovedException e){
                  System.out.println(e.getMessage());
              }
          }
          else {
              try {
              m.moveLeft();
          }
              catch (ObjectCanNotBeMovedException e){
                  System.out.println(e.getMessage());
              }
          }
      }
  }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("Collection of movable objects with size ").append(movables.size()).append(":\n");
        for (Movable i: movables) {
            sb.append(i.toString());
        }
        return sb.toString();
    }
}
