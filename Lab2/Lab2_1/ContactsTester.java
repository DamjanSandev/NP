package Napredno.Lab2.Lab2_1;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;


public class ContactsTester {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int tests = scanner.nextInt();
        Faculty faculty = null;

        int rvalue = 0;
        long rindex = -1;

        DecimalFormat df = new DecimalFormat("0.00");

        for (int t = 0; t < tests; t++) {

            rvalue++;
            String operation = scanner.next();

            switch (operation) {
                case "CREATE_FACULTY": {
                    String name = scanner.nextLine().trim();
                    int N = scanner.nextInt();

                    Student[] students = new Student[N];

                    for (int i = 0; i < N; i++) {
                        rvalue++;

                        String firstName = scanner.next();
                        String lastName = scanner.next();
                        String city = scanner.next();
                        int age = scanner.nextInt();
                        long index = scanner.nextLong();

                        if ((rindex == -1) || (rvalue % 13 == 0))
                            rindex = index;

                        Student student = new Student(firstName, lastName, city,
                                age, index);
                        students[i] = student;
                    }

                    faculty = new Faculty(name, students);
                    break;
                }

                case "ADD_EMAIL_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String email = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addEmailContact(date, email);
                    break;
                }

                case "ADD_PHONE_CONTACT": {
                    long index = scanner.nextInt();
                    String date = scanner.next();
                    String phone = scanner.next();

                    rvalue++;

                    if ((rindex == -1) || (rvalue % 3 == 0))
                        rindex = index;

                    faculty.getStudent(index).addPhoneContact(date, phone);
                    break;
                }

                case "CHECK_SIMPLE": {
                    System.out.println("Average number of contacts: "
                            + df.format(faculty.getAverageNumberOfContacts()));

                    rvalue++;

                    String city = faculty.getStudent(rindex).getCity();
                    System.out.println("Number of students from " + city + ": "
                            + faculty.countStudentsFromCity(city));

                    break;
                }

                case "CHECK_DATES": {

                    rvalue++;

                    System.out.print("Latest contact: ");
                    Contact latestContact = faculty.getStudent(rindex)
                            .getLatestContact();
                    if (latestContact.getType().equals("Email"))
                        System.out.println(((EmailContact) latestContact)
                                .getEmail());
                    if (latestContact.getType().equals("Phone"))
                        System.out.println(((PhoneContact) latestContact)
                                .getPhone()
                                + " ("
                                + ((PhoneContact) latestContact).getOperator()
                                .toString() + ")");

                    if (faculty.getStudent(rindex).getEmailContacts().length > 0
                            && faculty.getStudent(rindex).getPhoneContacts().length > 0) {
                        System.out.print("Number of email and phone contacts: ");
                        System.out
                                .println(faculty.getStudent(rindex)
                                        .getEmailContacts().length
                                        + " "
                                        + faculty.getStudent(rindex)
                                        .getPhoneContacts().length);

                        System.out.print("Comparing dates: ");
                        int posEmail = rvalue
                                % faculty.getStudent(rindex).getEmailContacts().length;
                        int posPhone = rvalue
                                % faculty.getStudent(rindex).getPhoneContacts().length;

                        System.out.println(faculty.getStudent(rindex)
                                .getEmailContacts()[posEmail].isNewerThan(faculty
                                .getStudent(rindex).getPhoneContacts()[posPhone]));
                    }

                    break;
                }

                case "PRINT_FACULTY_METHODS": {
                    System.out.println("Faculty: " + faculty.toString());
                    System.out.println("Student with most contacts: "
                            + faculty.getStudentWithMostContacts().toString());
                    break;
                }

            }

        }

        scanner.close();
    }
}
abstract class Contact{
    protected String dateString;

    public Contact(String date) {
        this.dateString = date;
    }

    public boolean isNewerThan(Contact c) {
        return this.dateString.compareTo(c.dateString) > 0;
    }
    public abstract String getType();

}
class EmailContact extends Contact{
    private final String email;

    public EmailContact(String date, String email) {
        super(date);
        this.email = email;
    }

    @Override
    public String getType() {
        return "Email";
    }
    @Override
    public String toString() {
        return "\"" + email + "\"";
    }

    public String getEmail() {
        return email;
    }

}
enum Operator { VIP, ONE, TMOBILE }
class PhoneContact extends Contact{
    private final String phone;
    private Operator operator;

    public PhoneContact(String date, String phone) {
        super(date);
        this.phone = phone;
        char op=phone.charAt(2);
        if(op == '0' || op=='1' || op=='2' )operator=Operator.TMOBILE;
        else if(op=='5' || op=='6')operator=Operator.ONE;
        else if(op=='7' || op=='8') operator=Operator.VIP;
    }
    @Override
    public String getType() {
        return "Phone";
    }
    @Override
    public String toString() {
        return "\"" + phone + "\"";
    }

    public String getPhone() {
        return phone;
    }

    public Operator getOperator() {
        return operator;
    }
}
class Student{
    private List<Contact> contacts;
    private final String firstName;
    private final String lastName;
    private final String city;
    private int age;
    private final long index;


    public Student(String firstName, String lastName, String city, int age, long index) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.age = age;
        this.index = index;
        contacts=new ArrayList<Contact>();

    }
    public void addEmailContact(String date, String email){
          contacts.add(new EmailContact(date,email));
    }
    public void addPhoneContact(String date, String phone){
        contacts.add(new PhoneContact(date,phone));
    }
    public Contact [] getEmailContacts(){
        List<Contact> emails=new ArrayList<Contact>();
        for (Contact con:contacts) {
            if(con.getType()=="Email"){
                emails.add(con);
            }
        }
        return emails.toArray(new Contact[emails.size()]);
    }

    public Contact [] getPhoneContacts(){
        List<Contact> phones=new ArrayList<Contact>();
        for (Contact con:contacts) {
            if(con.getType()=="Phone"){
                phones.add(con);
            }
        }
        return phones.toArray(new Contact[phones.size()]);
    }
    public int getNumberOfContacts(){
        return contacts.size();
    }

    String getFullName(){
        return firstName + " " + lastName;
    }
    public String getCity() {
        return city;
    }

    public long getIndex() {
        return index;
    }
    public Contact getLatestContact(){
        Contact max=contacts.get(0);
        for (int i = 1; i < contacts.size(); i++) {
           if(contacts.get(i).isNewerThan(max)){
               max=contacts.get(i);
           }
        }
        return max;
    }

    @Override
    public String toString() {
        return "{" +
                "\"ime\":\"" + firstName + "\", " +
                "\"prezime\":\"" + lastName + "\", " +
                "\"vozrast\":" + age + ", " +
                "\"grad\":\"" + city + "\", "+
                "\"indeks\":" + index + ", " +
                "\"telefonskiKontakti\":"+ Arrays.toString(getPhoneContacts()) + ", "  +
                "\"emailKontakti\":" + Arrays.toString(getEmailContacts())  +
                "}";
    }
}
class Faculty{
    private final String name;
    private  List<Student>students;

    public Faculty(String name, Student [] students) {
        this.name = name;
        this.students = List.of(students);
    }
    public int countStudentsFromCity(String cityName){
        int count=0;
        for (Student s : students) {
           if(s.getCity().equals(cityName)){
               count++;
           }
        }
        return count;
    }
    public Student getStudent(long index){
        for (Student s: students) {
            if(s.getIndex()==index) return s;
        }
        return null;
    }
    public double getAverageNumberOfContacts(){
        double sum=0.0;
        for (Student s : students) {
            sum+=s.getNumberOfContacts();
        }
        return sum/ (float) students.size();
    }
    public Student getStudentWithMostContacts(){
        Student max=students.get(0);
        for (int i = 1; i < students.size(); i++) {
            if(students.get(i).getNumberOfContacts()> max.getNumberOfContacts()){
                max=students.get(i);
            }
            else if(students.get(i).getNumberOfContacts() == max.getNumberOfContacts() && students.get(i).getIndex()> max.getIndex()){
                max=students.get(i);
            }
        }
        return max;
    }

    @Override
    public String toString() {
        return "{" +
                "\"fakultet\":\"" + name + "\", " +
                "\"studenti\":" + students.toString()  +
                "}";
    }
}


