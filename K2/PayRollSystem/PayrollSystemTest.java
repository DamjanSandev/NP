package Napredno.K2.PayRollSystem;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        levels.add("level10");
        for (int i = 5; i <= 9; i++) {
            levels.add("level" + i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }
}

abstract class Employee {
    private final String id;
    private final String level;

    public Employee(String id, String level) {
        this.id = id;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public abstract double getSalary();

}

class HourlyEmployee extends Employee {

    private final double hours;
    private final double regularHours;
    private final double hourlyRateByLevel;
    private final double overtimeHours;

    public HourlyEmployee(String id, String level, double hours, double hourlyRateByLevel) {
        super(id, level);
        this.hours = hours;
        this.hourlyRateByLevel = hourlyRateByLevel;
        if (hours <= 40) {
            regularHours = hours;
            overtimeHours = 0;
        } else {
            regularHours = 40;
            overtimeHours = hours - 40;
        }
    }

    @Override
    public double getSalary() {
        return regularHours * hourlyRateByLevel + overtimeHours * hourlyRateByLevel * 1.50;
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f", getId(), getLevel(), getSalary(), regularHours, overtimeHours);
    }
}

class FreelanceEmployee extends Employee {


    private final List<Integer> ticketPoints;
    private final double ticketRateByLevel;

    public FreelanceEmployee(String id, String level, List<Integer> ticketPoints, double ticketRateByLevel) {
        super(id, level);
        this.ticketPoints = ticketPoints;
        this.ticketRateByLevel = ticketRateByLevel;
    }

    @Override
    public double getSalary() {
        return getSum() * ticketRateByLevel;
    }

    public int getSum() {
        return ticketPoints.stream().mapToInt(value -> value).sum();
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d", getId(), getLevel(), getSalary(), ticketPoints.size(), getSum());
    }
}

class PayrollSystem {
    private final Map<String, Double> hourlyRateByLevel;
    private final Map<String, Double> ticketRateByLevel;

    private final List<Employee> employees;

    public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        employees = new ArrayList<>();
    }

    public void readEmployees(InputStream is) {
        Scanner sc = new Scanner(is);
        while (sc.hasNext()) {
            String[] parts = sc.nextLine().split(";");
            String type = parts[0], id = parts[1], level = parts[2];
            if (type.equals("H")) {
                double hourlyByLevel = hourlyRateByLevel.get(level);
                employees.add(new HourlyEmployee(id, level, Double.parseDouble(parts[3]), hourlyByLevel));
            } else {
                double ticketByLevel = ticketRateByLevel.get(level);
                List<Integer> ticketPoints = new ArrayList<>();
                for (int i = 3; i < parts.length; i++) {
                    ticketPoints.add(Integer.parseInt(parts[i]));
                }
                employees.add(new FreelanceEmployee(id, level, ticketPoints, ticketByLevel));
            }
        }
    }

    public Map<String, Set<Employee>> printEmployeesByLevels(OutputStream out, Set<String> levels) {
        Map<String, Set<Employee>> levelToEmployees = new LinkedHashMap<>();
        levels.forEach(l -> {
            employees.stream().filter(employee -> employee.getLevel().equals(l))
                    .forEach(employee -> {
                        levelToEmployees.putIfAbsent(l, new TreeSet<>(Comparator.comparing(Employee::getSalary).reversed().thenComparing(Employee::getLevel)));
                        levelToEmployees.get(l).add(employee);
                    });
        });

        return levelToEmployees;
    }
}