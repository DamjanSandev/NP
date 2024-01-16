package Napredno.K2.LogCollector;

import java.util.*;
import java.util.stream.Collectors;

public class LogsTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        LogCollector collector = new LogCollector();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.startsWith("addLog")) {
                collector.addLog(line.replace("addLog ", ""));
            } else if (line.startsWith("printServicesBySeverity")) {
                collector.printServicesBySeverity();
            } else if (line.startsWith("getSeverityDistribution")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                if (parts.length == 3) {
                    microservice = parts[2];
                }
                collector.getSeverityDistribution(service, microservice).forEach((k, v) -> System.out.printf("%d -> %d%n", k, v));
            } else if (line.startsWith("displayLogs")) {
                String[] parts = line.split("\\s+");
                String service = parts[1];
                String microservice = null;
                String order = null;
                if (parts.length == 4) {
                    microservice = parts[2];
                    order = parts[3];
                } else {
                    order = parts[2];
                }
                System.out.println(line);

                collector.displayLogs(service, microservice, order);
            }
        }
    }
}

abstract class Log {
    private final String service_name;
    private final String microservice_name;
    private final String message;
    private final long timestamp;

    public Log(String service_name, String microservice_name, String message, long timestamp) {
        this.service_name = service_name;
        this.microservice_name = microservice_name;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getService_name() {
        return service_name;
    }

    public String getMicroservice_name() {
        return microservice_name;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public abstract int severity();

}

class InfoLog extends Log {
    public InfoLog(String service_name, String microservice_name, String message, long timestamp) {
        super(service_name, microservice_name, message, timestamp);
    }

    @Override
    public int severity() {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%s|%s [INFO] %s T:%d", getService_name(), getMicroservice_name(), getMessage(), getTimestamp());
    }
}

class WarnLog extends Log {

    public WarnLog(String service_name, String microservice_name, String message, long timestamp) {
        super(service_name, microservice_name, message, timestamp);
    }

    @Override
    public int severity() {
        if (getMessage().contains("might cause error"))
            return 2;
        return 1;
    }

    @Override
    public String toString() {
        return String.format("%s|%s [WARN] %s T:%d", getService_name(), getMicroservice_name(), getMessage(), getTimestamp());
    }
}

class ErrorLog extends Log {

    public ErrorLog(String service_name, String microservice_name, String message, long timestamp) {
        super(service_name, microservice_name, message, timestamp);
    }

    @Override
    public int severity() {
        int severity = 3;
        if (getMessage().contains("fatal")) {
            severity += 2;
        }
        if (getMessage().contains("exception")) {
            severity += 3;
        }
        return severity;
    }

    @Override
    public String toString() {
        return String.format("%s|%s [ERROR] %s T:%d", getService_name(), getMicroservice_name(), getMessage(), getTimestamp());
    }
}

class LogFactory {
    public static Log createLog(String line) {
        String[] parts = line.split("\\s+");
        if (parts[2].equals("INFO")) {
            return new InfoLog(parts[0], parts[1], Arrays.stream(parts).skip(3).limit(parts.length - 4).collect(Collectors.joining(" ")), Long.parseLong(parts[parts.length - 1]));
        } else if (parts[2].equals("WARN")) {
            return new WarnLog(parts[0], parts[1], Arrays.stream(parts).skip(3).limit(parts.length - 4).collect(Collectors.joining(" ")), Long.parseLong(parts[parts.length - 1]));
        }
        return new ErrorLog(parts[0], parts[1], Arrays.stream(parts).skip(3).limit(parts.length - 4).collect(Collectors.joining(" ")), Long.parseLong(parts[parts.length - 1]));

    }
}

class LogCollector {

    Map<String, List<Log>> logsByService;
    Map<String, Map<String, List<Log>>> microservicesByService;


    public LogCollector() {
        logsByService = new HashMap<>();
        microservicesByService = new HashMap<>();
    }

    public void addLog(String addLog) {
        Log log = LogFactory.createLog(addLog);
        String service = log.getService_name();
        logsByService.putIfAbsent(service, new ArrayList<>());
        logsByService.get(service).add(log);
        microservicesByService.putIfAbsent(service, new HashMap<>());
        String microservice = log.getMicroservice_name();
        microservicesByService.get(service).putIfAbsent(microservice, new ArrayList<>());
        microservicesByService.get(service).get(microservice).add(log);

    }

    public void printServicesBySeverity() {
        logsByService.entrySet().stream()
                .sorted(Comparator.comparing(stringListEntry -> averageSeverity(stringListEntry.getValue()), Comparator.reverseOrder()))
                .forEach(stringListEntry -> System.out.println(microservicesReport(stringListEntry.getKey())));

    }

    public double averageSeverity(List<Log> logs) {
        return logs.stream().mapToInt(Log::severity).average().orElse(0.0);
    }

    public String microservicesReport(String serviceName) {
        int countMicroServices = microservicesByService.get(serviceName).size();
        int totalLogs = logsByService.get(serviceName).size();
        double avgSeverity = averageSeverity(logsByService.get(serviceName));
        double avgNumberOfLogsPerMicroservice = microservicesByService.get(serviceName)
                .values().stream().mapToLong(List::size)
                .sum() / (double) countMicroServices;

        return String.format("Service name: %s Count of microservices: %d Total logs in service: %d Average severity for all logs: %.2f Average number of logs per microservice: %.2f", serviceName, countMicroServices, totalLogs, avgSeverity, avgNumberOfLogsPerMicroservice);

    }

    public Map<Integer, Integer> getSeverityDistribution(String service, String microservice) {
        Map<Integer, Integer> result;
        if (microservice != null) {
            result = microservicesByService.get(service).get(microservice).stream()
                    .collect(Collectors.groupingBy(
                            Log::severity,
                            TreeMap::new,
                            Collectors.collectingAndThen(Collectors.toList(), List::size)
                    ));
        } else {
            result = logsByService.get(service).stream()
                    .collect(Collectors.groupingBy(
                            Log::severity,
                            TreeMap::new,
                            Collectors.collectingAndThen(Collectors.toList(), List::size)
                    ));
        }
        return result;
    }

    public void displayLogs(String service, String microservice, String order) {
        if (microservice != null)
            microservicesByService.get(service).get(microservice).stream().sorted(comparatorByOrder(order)).forEach(System.out::println);
        else
            logsByService.get(service).stream().sorted(comparatorByOrder(order)).forEach(System.out::println);
    }

    private static Comparator<Log> comparatorByOrder(String order) {
        if (order.equals("NEWEST_FIRST")) {
            return Comparator.comparing(Log::getTimestamp).reversed();
        } else if (order.equals("OLDEST_FIRST")) {
            return Comparator.comparing(Log::getTimestamp);
        } else if (order.equals("MOST_SEVERE_FIRST")) {
            return Comparator.comparing(Log::severity).thenComparing(Log::getTimestamp).thenComparing(Log::getMicroservice_name).reversed();
        }
        return Comparator.comparing(Log::severity).thenComparing(Log::getTimestamp).thenComparing(Log::getMicroservice_name);
    }

}