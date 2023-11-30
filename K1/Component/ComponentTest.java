package Napredno.K1.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if(what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.printf("=== CHANGED COLOR (%d, %s) ===%n", weight, color);
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.printf("=== SWITCHED COMPONENTS %d <-> %d ===%n", pos1, pos2);
        window.swichComponents(pos1, pos2);
        System.out.println(window);
    }
}
class Component implements Comparable<Component>{
    private String color;
    private final int weight;
    int pos;

    List<Component> components;

    public Component(String color, int weight) {
        this.color = color;
        this.weight = weight;
        components=new ArrayList<>();
        pos=-1;
    }

    public Component setPos(int pos) {
        this.pos = pos;
        return this;
    }

    public void addComponent(Component component){
        components.add(component);
        sort();
    }

    public int getPos() {
        return pos;
    }

    public void sort(){
        components=components.stream().sorted().collect(Collectors.toList());
    }

    @Override
    public int compareTo(Component o) {
        if(weight==o.weight){
            if(color.compareTo(o.color)==0) {
                return Integer.compare(o.components.stream().mapToInt(component -> component.weight).sum(), components.stream().mapToInt(component -> component.weight).sum());
            }
            return color.compareTo(o.color);


        }

        return Integer.compare(weight,o.weight);

    }
    public String getComponentString(int intend){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < intend; i++) {
            sb.append("---");
        }
        sb.append(String.format("%d:%s\n",weight,color));
        components.forEach(c->sb.append(c.getComponentString(intend+1)));
        return sb.toString();
    }
    public void changeColor(int weight,String color){
        if(this.weight<weight)this.color = color;
        if(!components.isEmpty()){
            components.forEach(c->c.changeColor(weight,color));
        }
        sort();
    }

}
class InvalidPositionException extends Exception{
    public InvalidPositionException(String message) {
        super(message);
    }
}
class Window{
    private final String name;
    private List<Component> components;

    public Window(String name) {
        this.name = name;
        components=new ArrayList<>();
    }
    void addComponent(int position, Component component) throws InvalidPositionException {
          if(components.isEmpty()){
              components.add(component.setPos(position));
              return;
          }
        for (Component value : components) {
            if (value.pos == position) {
                throw new InvalidPositionException(String.format("Invalid position %d, alredy taken!", position));
            }
        }
          components.add(component.setPos(position));
          sort();
    }
    public void sort(){
        components=components.stream().sorted(Comparator.comparingInt(Component::getPos)).collect(Collectors.toList());
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WINDOW ").append(name).append('\n');
        components.forEach(c->sb.append(c.getPos()).append(":").append(c.getComponentString(0)));
        return sb.toString();
    }
    public void changeColor(int weight,String color){
        for (Component c : components) {
            c.changeColor(weight,color);
        }
        sort();
    }
    public void swichComponents(int pos1, int pos2){
        int  tmp1 = 0,tmp2=0;
        Component c1=null,c2=null;
        for (Component c : components) {
            if(c.getPos()==pos1){
                tmp1=pos1;
                c1=c;
            }
            else if(c.getPos()==pos2){
                tmp2=pos2;
                c2=c;
            }
        }
        int tmp=tmp1;
        c1.setPos(tmp2);
        c2.setPos(tmp);
        sort();

    }
}