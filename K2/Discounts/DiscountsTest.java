package Napredno.K2.Discounts;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Discounts
 */
public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}

// Vashiot kod ovde
class Price implements Comparable<Price>{
    private final int discountPrice;
    private final int price;

    public Price(int discount, int price) {
        this.discountPrice = discount;
        this.price = price;
    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public int getPrice() {
        return price;
    }
    public int getTotalDiscount(){
        return price-discountPrice;
    }
    public float getDiscountFloat(){
        return (100-((float)discountPrice/price)*100);

    }
    public int getDiscountInt(){
        return (int)(100-((float)discountPrice/price)*100);
    }
    public int diff(){
        return price-discountPrice;
    }

    @Override
    public String toString() {
        String s="";
        if(getDiscountInt()<10)s+=" "+getDiscountInt();
        else s+=getDiscountInt();
        s+="% " +discountPrice+"/"+price;
        return s;
    }

    @Override
    public int compareTo(Price o) {
        Comparator<Price> comparator=Comparator.comparing(Price::getDiscountInt).thenComparing(Price::diff).reversed();
        return comparator.compare(this,o);
    }
}
class Store{
    private final String name;
    private final List<Price> prices;
    public Store(String line){
        String []parts=line.split("\\s+");
        name=parts[0];
        prices=new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String []price=parts[i].split(":");
            prices.add(new Price(Integer.parseInt(price[0]),Integer.parseInt(price[1])));
        }

    }

    public String getName() {
        return name;
    }

    public List<Price> getPrices() {
        return prices;
    }
    public int totalDiscount(){
        return prices.stream().mapToInt(Price::getTotalDiscount).sum();
    }
    public float getDiscountTotal(){
       return (float) prices.stream().mapToDouble(Price::getDiscountInt).sum();
    }
    public float getAverageDiscount(){
        return getDiscountTotal()/prices.size();
    }
    public void sortPrices(){
        Collections.sort(prices);
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(name).append("\n");
        sb.append(String.format("Average discount: %.1f%%",getAverageDiscount())).append("\n");
        sb.append(String.format("Total discount: %d",totalDiscount())).append("\n");
        for (int i = 0; i < prices.size(); i++) {
            sb.append(prices.get(i).toString());
            if(i!=prices.size()-1)sb.append("\n");
        }
        return sb.toString();

    }
}
class Discounts{
    private List<Store> stores;
    private final Comparator<Store> byAverage=Comparator.comparing(Store::getAverageDiscount).reversed().thenComparing(Store::getName);
    private final Comparator<Store> byTotal=Comparator.comparing(Store::totalDiscount).thenComparing(Store::getName);

    public int readStores(InputStream in) {
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        stores=br.lines().map(Store::new).collect(Collectors.toList());
        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        List<Store> result;
        result=stores.stream().sorted(byAverage).limit(3).collect(Collectors.toList());
        for (Store s : result) {
            s.sortPrices();
        }
        return result;
    }

    public List<Store> byTotalDiscount() {
        List<Store> result;
        result=stores.stream().sorted(byTotal).limit(3).collect(Collectors.toList());
        for (Store s : result) {
            s.sortPrices();
        }
        return result;
    }
}
