package Napredno.K1.MinMax;

import java.util.Scanner;

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for(int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        MinMax<Integer> ints = new MinMax<Integer>();
        for(int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);
    }
}

class MinMax<T extends Comparable<T>>{
    private T min,max;
    private int countmax,coutmin,updates;
    public MinMax() {
        countmax=coutmin=updates=0;
    }
    public void update(T element){
        if(updates==0){
            min=max=element;
        }
        if(min.compareTo(element)>0){
            min=element;
            coutmin=1;
        }
        else if(min.compareTo(element)==0){
            coutmin++;
        }
        if(max.compareTo(element)<0){
            max=element;
            countmax=1;
        }
        else if(max.compareTo(element)==0){
            countmax++;
        }
        updates++;
    }
    public T max(){
        return max;
    }
    public T min(){
        return min;
    }

    @Override
    public String toString() {
        return min + " " + max + " " + (updates-countmax-coutmin) + "\n";
    }
}
