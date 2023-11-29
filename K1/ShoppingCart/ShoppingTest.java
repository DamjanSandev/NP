package Napredno.K1.ShoppingCart;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
class InvalidOperationException extends Exception{
    public InvalidOperationException(String message) {
        super(message);
    }
}
class Item implements Comparable<Item>{
    String type,name;
    int id;
    double price,quantity;
    public Item(String line) throws InvalidOperationException{
        String []parts=line.split(";");
        if(parts[4].equals("0")){
                throw new InvalidOperationException(String.format("The quantity of the product with id %d can not be 0.",Integer.parseInt(parts[1])));
            }

        type=parts[0];
        id=Integer.parseInt(parts[1]);
        name=parts[2];
        price=Double.parseDouble(parts[3]);
        quantity=Double.parseDouble(parts[4]);
    }

    public int getId() {
        return id;
    }

    public double totalPrice(){
        if(type.equals("WS")){
            return quantity*price;
        }
        return quantity /1000 * price;
   }

    @Override
    public String toString() {
        return String.format("%d - %.2f",id,totalPrice());
    }

    public String toStringBlackFriday(int num) {
        return String.format("%d - %.2f",id,totalPrice()/num);
    }

    @Override
    public int compareTo(Item o) {
        return Double.compare(totalPrice(),o.totalPrice());
    }
}
class ShoppingCart{
    List<Item> items;
    public ShoppingCart(){
        items=new ArrayList<>();
    }
    public void addItem(String itemData){
        try {
            items.add(new Item(itemData));
        } catch (InvalidOperationException e) {
            System.out.println(e.getMessage());
        }
    }
    public void printShoppingCart(OutputStream os){
        PrintWriter pw=new PrintWriter(os);
        items.stream().sorted(Comparator.reverseOrder()).forEach(pw::println);
        pw.flush();
        pw.close();
    }
    public void blackFridayOffer(List<Integer> discountItems, OutputStream os)throws InvalidOperationException{
        if(discountItems.isEmpty()){
            throw new InvalidOperationException("There are no products with discount.");
        }
        for (Item i : items) {
            for (Integer disc : discountItems) {
                if(i.getId()==disc){
                    System.out.println(i.toStringBlackFriday(10));
                    break;
                }
            }
        }
    }
}
public class ShoppingTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        int items = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < items; i++) {
            cart.addItem(sc.nextLine());
        }

        List<Integer> discountItems = new ArrayList<>();
        int discountItemsCount = Integer.parseInt(sc.nextLine());
        for (int i = 0; i < discountItemsCount; i++) {
            discountItems.add(Integer.parseInt(sc.nextLine()));
        }

        int testCase = Integer.parseInt(sc.nextLine());
        if (testCase == 1) {
            cart.printShoppingCart(System.out);
        } else if (testCase == 2) {
            try {
                cart.blackFridayOffer(discountItems, System.out);
            } catch (InvalidOperationException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Invalid test case");
        }
    }
}
