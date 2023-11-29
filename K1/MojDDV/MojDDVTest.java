package Napredno.K1.MojDDV;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);
    }
}
class Item{
    int price;
    double tax;

    public Item(int price, String tax) {
        this.price = price;
        if(tax.equals("A")){
            this.tax=0.18;
        }
        else if(tax.equals("B")){
            this.tax=0.05;
        }
        else{
            this.tax=0.0;
        }
    }

    public int getPrice() {
        return price;
    }

    public double getTax() {
        return tax;
    }
    public double itemDDV(){
        return price*tax*0.15;
    }
}
class Fiscal{
    int id;
    List<Item> items;
    public Fiscal(String line) throws AmountNotAllowedException {
        String []parts=line.split("\\s+");
        id=Integer.parseInt(parts[0]);
        items=new ArrayList<>();
        for (int i = 1; i < parts.length; i+=2) {
              items.add(new Item(Integer.parseInt(parts[i]),parts[i+1]));
        }
        if(fiscalPrice()>30000){
            throw new AmountNotAllowedException(String.format("Receipt with amount %d is not allowed to be scanned",fiscalPrice()));
        }
    }
    public double fiscalDDV(){
        return items.stream().mapToDouble(Item::itemDDV).sum();
    }
    public int fiscalPrice(){
        return items.stream().mapToInt(Item::getPrice).sum();
    }

    @Override
    public String toString() {
        return String.format("%10d\t%10d\t%10.5f",id,fiscalPrice(),fiscalDDV());
    }
}
class AmountNotAllowedException extends Exception{
    public AmountNotAllowedException(String message) {
        super(message);
    }
}
class MojDDV{
    List<Fiscal>fiscals;
    public MojDDV() {
        fiscals=new ArrayList<>();
    }
    public void readRecords(InputStream inputStream){
       Scanner sc=new Scanner(inputStream);
       while (sc.hasNextLine()){
           try {
               fiscals.add(new Fiscal(sc.nextLine()));
           } catch (AmountNotAllowedException e) {
               System.out.println(e.getMessage());
           }
       }
    }
   public void printTaxReturns (OutputStream outputStream){
       PrintWriter pw=new PrintWriter(outputStream);
       for (Fiscal f : fiscals) {
           pw.println(f);
       }
       pw.flush();
   }
   public void printStatistics (OutputStream outputStream){
        PrintWriter pw=new PrintWriter(outputStream);
       DoubleSummaryStatistics summaryStatistics=fiscals.stream().mapToDouble(Fiscal::fiscalDDV).summaryStatistics();
       pw.println(String.format("min:\t%.3f",summaryStatistics.getMin()));
       pw.println(String.format("max:\t%.3f",summaryStatistics.getMax()));
       pw.println(String.format("sum:\t%.3f",summaryStatistics.getSum()));
       pw.println(String.format("count:\t%d",summaryStatistics.getCount()));
       pw.println(String.format("avg:\t%.3f",summaryStatistics.getAverage()));
        pw.flush();
        pw.close();
   }

}
