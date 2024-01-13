package Napredno.K2.TaskManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class TasksManagerTest {

    public static void main(String[] args) {

        TaskManager manager = new TaskManager();

        System.out.println("Tasks reading");
        manager.readTasks(System.in);
        System.out.println("By categories with priority");
        manager.printTasks(System.out, true, true);
        System.out.println("-------------------------");
        System.out.println("By categories without priority");
        manager.printTasks(System.out, false, true);
        System.out.println("-------------------------");
        System.out.println("All tasks without priority");
        manager.printTasks(System.out, false, false);
        System.out.println("-------------------------");
        System.out.println("All tasks with priority");
        manager.printTasks(System.out, true, false);
        System.out.println("-------------------------");

    }
}


class DeadlineNotValidException extends Exception {
    public DeadlineNotValidException(LocalDateTime deadline) {
        super(String.format("The deadline %s has already passed", deadline));
    }
}

interface ITask {
    int getPriority();

    String getCategory();

    LocalDateTime getDeadline();
}

class SimpleTask implements ITask {
    private final String category;
    private final String name;
    private final String description;

    public SimpleTask(String category, String name, String description) {
        this.category = category;
        this.name = name;
        this.description = description;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public LocalDateTime getDeadline() {
        return LocalDateTime.MAX;
    }

    @Override
    public String toString() {
        return "Task{name='" + name + "', description='" + description + "'}";
    }
}

abstract class TaskDecorator implements ITask {
    ITask iTask;

    public TaskDecorator(ITask iTask) {
        this.iTask = iTask;
    }
}

class PriorityTaskDecorator extends TaskDecorator {

    private final int priority;

    public PriorityTaskDecorator(ITask iTask, int priority) {
        super(iTask);
        this.priority = priority;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public String getCategory() {
        return iTask.getCategory();
    }

    @Override
    public LocalDateTime getDeadline() {
        return iTask.getDeadline();
    }

    @Override
    public String toString() {
        return iTask.toString().substring(0, iTask.toString().length() - 1) + ", priority=" + priority + "}";
    }
}

class DeadLineTaskDecorator extends TaskDecorator {
    private final LocalDateTime deadline;

    public DeadLineTaskDecorator(ITask iTask, LocalDateTime deadline) {
        super(iTask);
        this.deadline = deadline;
    }

    @Override
    public int getPriority() {
        return iTask.getPriority();
    }

    @Override
    public String getCategory() {
        return iTask.getCategory();
    }

    @Override
    public LocalDateTime getDeadline() {
        return deadline;
    }

    @Override
    public String toString() {
        return iTask.toString().substring(0, iTask.toString().length() - 1) + ", deadline=" + deadline + "}";
    }
}

class TaskFactory {
    public static ITask createTask(String line) throws DeadlineNotValidException {
        String[] parts = line.split(",");
        String category = parts[0], name = parts[1], desc = parts[2];
        SimpleTask simpleTask = new SimpleTask(category, name, desc);
        if (parts.length == 3) {
            return simpleTask;
        } else if (parts.length == 4) {
            try {
                int priority = Integer.parseInt(parts[3]);
                return new PriorityTaskDecorator(simpleTask, priority);
            } catch (Exception e) {
                LocalDateTime deadline = LocalDateTime.parse(parts[3]);
                checkDeadLine(deadline);
                return new DeadLineTaskDecorator(simpleTask, deadline);
            }
        } else {
            LocalDateTime deadline = LocalDateTime.parse(parts[3]);
            checkDeadLine(deadline);
            int priority = Integer.parseInt(parts[4]);
            return new PriorityTaskDecorator(new DeadLineTaskDecorator(simpleTask, deadline), priority);
        }
    }

    private static void checkDeadLine(LocalDateTime localDateTime) throws DeadlineNotValidException {
        if (localDateTime.isBefore(LocalDateTime.of(2020, 6, 2, 23, 59, 59))) {
            throw new DeadlineNotValidException(localDateTime);
        }
    }
}

class TaskManager {

    Map<String, List<ITask>> tasksByCategory;

    public TaskManager() {
        tasksByCategory = new TreeMap<>();
    }

    public void readTasks(InputStream in) {
        Scanner sc = new Scanner(in);
        while (sc.hasNext()) {
            String line = sc.nextLine();
            ITask iTask = null;
            try {
                iTask = TaskFactory.createTask(line);
            } catch (DeadlineNotValidException e) {
                System.out.println(e.getMessage());
            }
            String category = line.split(",")[0];
            tasksByCategory.putIfAbsent(category, new ArrayList<>());
            if (iTask != null) {
                tasksByCategory.get(category).add(iTask);
            }

        }
    }

    public void printTasks(OutputStream out, boolean includePriority, boolean includeCategory) {
        PrintWriter pw = new PrintWriter(out);
        Comparator<ITask> priorityComparator = Comparator.comparing(ITask::getPriority).thenComparing(task -> Duration.between(LocalDateTime.of(2020, 6, 2, 23, 59, 59), task.getDeadline()));
        Comparator<ITask> timeComparator = Comparator.comparing(task -> Duration.between(LocalDateTime.of(2020, 6, 2, 23, 59, 59), task.getDeadline()));

        if (includeCategory) {
            tasksByCategory.forEach((category, task) -> {
                pw.println(category.toUpperCase());
                task.stream().sorted(includePriority ? priorityComparator : timeComparator).forEach(pw::println);
            });
        } else {
            tasksByCategory.values().stream()
                    .flatMap(List::stream)
                    .sorted(includePriority ? priorityComparator : timeComparator)
                    .forEach(pw::println);
        }
        pw.flush();

    }
}
