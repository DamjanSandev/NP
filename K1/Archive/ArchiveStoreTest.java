package Napredno.K1.Archive;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
abstract class Archive{
    private int id;
    LocalDate localDate;

    public Archive(int id) {
        this.id = id;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public int getId() {
        return id;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }
    abstract String type();
}
class LockedArchive extends Archive{
    LocalDate dateToOpen;

    public LockedArchive(int id,LocalDate localDate) {
        super(id);
        dateToOpen=localDate;
    }

    public LocalDate getDateToOpen() {
        return dateToOpen;
    }

    @Override
    String type() {
        return "Locked";
    }
}
class SpecialArchive extends Archive{
   private int maxOpen,opened;
    public SpecialArchive(int id,int maxOpen) {
        super(id);
        this.maxOpen=maxOpen;
        opened=0;
    }

    public void setOpened() {
        opened++;
    }

    public int getOpened() {
        return opened;
    }

    public int getMaxOpen() {
        return maxOpen;
    }

    @Override
    String type() {
        return "Special";
    }
}
class ArchiveStore{
    List<Archive> archives;
    List<StringBuilder> logs;

    public ArchiveStore() {
        archives= new ArrayList<>();
        logs=new ArrayList<>();
    }
  public   void archiveItem(Archive item, LocalDate date){
        item.setLocalDate(date);
        archives.add(item);
        logs.add(new StringBuilder(String.format("Item %d archived at %s",item.getId(),date.toString())));
  }
  public void openItem(int id, LocalDate date) throws NonExistingItemException {
        boolean found=false;
      for (Archive a : archives) {
          if(id==a.getId()){
              found=true;
              if(a.type().equals("Locked")){
                  if(((LockedArchive) a).getDateToOpen().isAfter(date)){
                      logs.add(new StringBuilder(String.format("Item %d cannot be opened before %s",a.getId(),((LockedArchive) a).getDateToOpen().toString())));
                  }
                  else{
                      logs.add(new StringBuilder(String.format("Item %d opened at %s",a.getId(),date.toString())));
                  }
              }
              else{
                  if(((SpecialArchive) a).getOpened()>= ((SpecialArchive) a).getMaxOpen()){
                      logs.add(new StringBuilder(String.format("Item %d cannot be opened more than %d times", a.getId(), ((SpecialArchive) a).getMaxOpen())));
                  }
                  else{
                      logs.add(new StringBuilder(String.format("Item %d opened at %s",a.getId(),date.toString())));
                      ((SpecialArchive) a).setOpened();
                  }
              }
              break;
          }
      }
      if(!found)throw  new NonExistingItemException(String.format("Item with id %d doesn't exist",id));
  }
  public String getLog(){
        StringBuilder sb= new StringBuilder();
      for (StringBuilder s : logs) {
          sb.append(s).append("\n");
      }
      return sb.toString();
  }
}
class NonExistingItemException extends Exception{
    public NonExistingItemException(String message) {
        super(message);
    }
}
public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        LocalDate date = LocalDate.of(2013, 10, 7);
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();

            LocalDate dateToOpen = date.atStartOfDay().plusSeconds(days * 24 * 60 * 60).toLocalDate();
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}
