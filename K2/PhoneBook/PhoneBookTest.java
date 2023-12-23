package Napredno.K2.PhoneBook;
import java.util.*;
import java.util.stream.Collectors;

public class PhoneBookTest {

    public static void main(String[] args) {
        PhoneBook phoneBook = new PhoneBook();
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            try {
                phoneBook.addContact(parts[0], parts[1]);
            } catch (DuplicateNumberException e) {
                System.out.println(e.getMessage());
            }
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            System.out.println(line);
            String[] parts = line.split(":");
            if (parts[0].equals("NUM")) {
                phoneBook.contactsByNumber(parts[1]);
            } else {
                phoneBook.contactsByName(parts[1]);
            }
        }
    }

}

// Вашиот код овде

class  DuplicateNumberException extends Exception{
    public DuplicateNumberException(String message) {
        super(String.format("Duplicate number: %s",message));
    }
}
class Contact implements Comparable<Contact>{
    private final String name;
    private final String phone;

    public Contact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public int compareTo(Contact o) {
        return Comparator.comparing(Contact::getName).thenComparing(Contact::getPhone).compare(this,o);
    }
    @Override
    public String toString() {
        return name + " " + phone;
    }
    public boolean isSublist(String number){
        return phone.contains(number);
    }
}
class PhoneBook{
    Set<String> allNumbers;

    Map<String, TreeSet<Contact>> contacts;
    public PhoneBook(){
        allNumbers=new HashSet<>();
        contacts=new HashMap<>();
    }

    public void addContact(String name, String number) throws DuplicateNumberException {
        if (allNumbers.contains(number)){
            throw new DuplicateNumberException(String.format("Duplicate number: %s", number));
        }

        contacts.putIfAbsent(name, new TreeSet<>());

        contacts.get(name).add(new Contact(name, number));

        allNumbers.add(number);
    }
    public void contactsByName(String name) {
        TreeSet<Contact> contactSet = contacts.get(name);

        if (contactSet == null) {
            System.out.println("NOT FOUND");
        } else {
            contactSet.forEach(System.out::println);
        }
    }


    public void contactsByNumber(String number) {
        TreeSet<Contact> contactList=contacts.values().stream().flatMap(TreeSet::stream).filter(contact -> contact.isSublist(number)).collect(Collectors.toCollection(TreeSet::new));
        if(contactList.isEmpty()){
            System.out.println("NOT FOUND");
        }
        else
            contactList.forEach(System.out::println);
    }
}
