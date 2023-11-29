package Napredno.Lab3.pizerija;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PizzaOrderTest {

    public static void main(String[] args) throws InvalidExtraTypeException, InvalidPizzaTypeException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) { //test Item
            try {
                String type = jin.next();
                String name = jin.next();
                Item item = null;
                if (type.equals("Pizza")) item = new PizzaItem(name);
                else item = new ExtraItem(name);
                System.out.println(item.getPrice());
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
        if (k == 1) { // test simple order
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 2) { // test order with removing
            Order order = new Order();
            while (true) {
                try {
                    String type = jin.next();
                    String name = jin.next();
                    Item item = null;
                    if (type.equals("Pizza")) item = new PizzaItem(name);
                    else item = new ExtraItem(name);
                    if (!jin.hasNextInt()) break;
                    order.addItem(item, jin.nextInt());
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            jin.next();
            System.out.println(order.getPrice());
            order.displayOrder();
            while (jin.hasNextInt()) {
                try {
                    int idx = jin.nextInt();
                    order.removeItem(idx);
                } catch (Exception e) {
                    System.out.println(e.getClass().getSimpleName());
                }
            }
            System.out.println(order.getPrice());
            order.displayOrder();
        }
        if (k == 3) { //test locking & exceptions
            Order order = new Order();
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.addItem(new ExtraItem("Coke"), 1);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.lock();
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
            try {
                order.removeItem(0);
            } catch (Exception e) {
                System.out.println(e.getClass().getSimpleName());
            }
        }
    }

}
interface Item{
    int getPrice();
    String getType();
}

class InvalidExtraTypeException extends Exception{
    public InvalidExtraTypeException(String message) {
        super(message);
    }
}
class InvalidPizzaTypeException extends Exception{
    public InvalidPizzaTypeException(String message) {
        super(message);
    }
}
class ItemOutOfStockException extends Exception{
    public ItemOutOfStockException(Item item) {
        super((Throwable) item);
    }
}
class ExtraItem implements Item{
    private String type;
    private int price;
    public ExtraItem(String type) throws InvalidExtraTypeException {
        if(type.equals("Coke")){
            price=5;
        }
        else if(type.equals("Ketchup")){
            price=3;
        }
        else throw  new InvalidExtraTypeException("InvalidExtraTypeException");
        this.type = type;
    }
    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getPrice() {
        return price;
    }
}
class PizzaItem implements Item{
     private String type;

     private int price;

    public PizzaItem(String type)throws InvalidPizzaTypeException {
        if(type.equals("Standard")){
            price=10;
        }
        else if(type.equals("Pepperoni")){
            price= 12;
        }
        else if(type.equals("Vegetarian")){
            price= 8;
        }
        else throw new InvalidPizzaTypeException("InvalidPizzaTypeException");
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public int getPrice() {
        return price;
    }
}
class ArrayIndexOutOfBоundsException extends Exception{
    public ArrayIndexOutOfBоundsException(int idx) {
        super(String.valueOf(idx));
    }
}
class OrderLockedException extends Exception{
    public OrderLockedException() {
       super("OrderLockedException");
    }
}
class EmptyOrder extends Exception{
    public EmptyOrder() {

    }
}
class Order {
    private List<Item> items;
    private List<Integer>counts;
    private boolean locked=false;

    public Order() {
        this.items =new ArrayList<>();
        this.counts = new ArrayList<>();
    }

    public void addItem(Item item, int count) throws ItemOutOfStockException , OrderLockedException{
        if(locked)throw new OrderLockedException();
        if(count > 10) throw new ItemOutOfStockException(item);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType().equals(item.getType())) {
                items.set(i,item);
                counts.set(i,count);
                return;
            }
        }
        items.add(item);
        counts.add(count);

    }

    public int getPrice() {
        int sum = 0;
        for (int i=0;i<items.size();i++) {
          sum+=items.get(i).getPrice()*counts.get(i);
        }
        return sum;

    }

    public void displayOrder()  {
        for (int i = 0; i < items.size() ; i++) {
            System.out.printf("%3d.%-15sx %1d %4d$%n",i+1,items.get(i).getType(),counts.get(i),items.get(i).getPrice()*counts.get(i));
        }
        System.out.printf("Total:%21d$\n",getPrice());
    }

    public void removeItem(int idx) throws ArrayIndexOutOfBоundsException,OrderLockedException {
        if(locked)throw  new OrderLockedException();
        if (idx >= 5 || idx < 0) throw new ArrayIndexOutOfBоundsException(idx);
        items.remove(idx);
    }
    public void lock() throws EmptyOrder{
        if (items.isEmpty()) {
            throw new EmptyOrder();
        }
        this.locked = true;
    }

}





