package Napredno.K2.BlockContainer;

import java.util.*;

public class BlockContainerTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int size = scanner.nextInt();
        BlockContainer<Integer> integerBC = new BlockContainer<Integer>(size);
        scanner.nextLine();
        Integer lastInteger = null;
        for(int i = 0; i < n; ++i) {
            int element = scanner.nextInt();
            lastInteger = element;
            integerBC.add(element);
        }
        System.out.println("+++++ Integer Block Container +++++");
        System.out.println(integerBC);
        System.out.println("+++++ Removing element +++++");
        integerBC.remove(lastInteger);
        System.out.println("+++++ Sorting container +++++");
        integerBC.sort();
        System.out.println(integerBC);
        BlockContainer<String> stringBC = new BlockContainer<String>(size);
        String lastString = null;
        for(int i = 0; i < n; ++i) {
            String element = scanner.next();
            lastString = element;
            stringBC.add(element);
        }
        System.out.println("+++++ String Block Container +++++");
        System.out.println(stringBC);
        System.out.println("+++++ Removing element +++++");
        stringBC.remove(lastString);
        System.out.println("+++++ Sorting container +++++");
        stringBC.sort();
        System.out.println(stringBC);
    }
}

// Вашиот код овде
class Block<T extends Comparable<T>>{
    private Set<T> elements;
    private final int n;
    public Block(int n){
        this.n=n;
        elements=new TreeSet<>();
    }
    public boolean addElement(T el){
        if(elements.size()<n){
            elements.add(el);
            return true;
        }
        return false;
    }

    public Set<T> getElements() {
        return elements;
    }

    public void setElements(Set<T> elements) {
        this.elements = elements;
    }

    public void removeElement(T el){
           elements.remove(el);
    }
    public int getSize(){
        return elements.size();
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append("[");
        int i=0;
        for (T el : elements) {
           sb.append(el);
           i++;
           if(i< elements.size())sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
class BlockContainer <T extends Comparable<T>>{
   private final List<Block<T>> blocks;
   private final int maxSize;
    private int block;
   public BlockContainer(int n){
       maxSize=n;
       blocks=new ArrayList<>();
       block=0;
   }


    public void add(T element) {
       if(blocks.isEmpty()){
           blocks.add(new Block<>(maxSize));
       }
       if(!blocks.get(block).addElement(element)){
           blocks.add(new Block<>(maxSize));
           block++;
           blocks.get(block).addElement(element);
       }
    }

    public void remove(T lastInteger) {
          blocks.get(block).removeElement(lastInteger);
          if(blocks.get(block).getSize()==0){
             blocks.remove(block);
             block--;
          }
    }

    public void sort() {
        List<T> allElements = new ArrayList<>();
        for (Block<T> block : blocks) {
            allElements.addAll(block.getElements());
        }

        Collections.sort(allElements);

        blocks.clear();

        for (int i = 0; i < allElements.size(); i += maxSize) {
            List<T> sublist = allElements.subList(i, Math.min(i + maxSize, allElements.size()));
            Block<T> newBlock = new Block<>(maxSize);
            newBlock.setElements(new TreeSet<>(sublist));
            blocks.add(newBlock);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        int i=0;
        for (Block<T> b : blocks) {
            sb.append(b.toString());
            i++;
            if(i<blocks.size())sb.append(",");
        }
        return sb.toString();
    }
}



