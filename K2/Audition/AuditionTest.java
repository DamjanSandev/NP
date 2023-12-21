package Napredno.K2.Audition;


import java.util.*;


public class AuditionTest {
    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticpant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }
}
class Participant{
    private final String code,name;
    private final int age;

    Participant(String code, String name, int age) {
        this.code = code;
        this.name = name;
        this.age = age;
    }
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d",code,name,age);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Participant that = (Participant) o;
        return age == that.age && Objects.equals(code, that.code) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}

class Audition{
    Map<String, Set<Participant>> participantsByCity;
    public Audition(){
        participantsByCity=new HashMap<>();
    }
    public void addParticpant(String city, String code, String name, int age) {
        Participant p=new Participant(code,name,age);
        if(!participantsByCity.containsKey(city)){
            participantsByCity.put(city,new HashSet<>());
            participantsByCity.get(city).add(p);
        }
        if(participantsByCity.get(city).stream().noneMatch(participant -> participant.getCode().equals(code))){
            participantsByCity.get(city).add(p);
        }


    }

    public void listByCity(String city) {
        Set<Participant> sorted=new TreeSet<>(Comparator.comparing(Participant::getName).thenComparing(Participant::getAge).thenComparing(Participant::getCode));
        sorted.addAll(participantsByCity.get(city));
        sorted.forEach(System.out::println);
    }
}

