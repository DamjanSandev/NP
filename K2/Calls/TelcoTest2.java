package Napredno.K2.Calls;

import java.util.*;
import java.util.stream.Collectors;

class DurationConverter {
    public static String convert(long duration) {
        long minutes = duration / 60;
        duration %= 60;
        return String.format("%02d:%02d", minutes, duration);
    }
}


public class TelcoTest2 {
    public static void main(String[] args) {
        TelcoApp app = new TelcoApp();

        Scanner sc = new Scanner(System.in);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");
            String command = parts[0];

            if (command.equals("addCall")) {
                String uuid = parts[1];
                String dialer = parts[2];
                String receiver = parts[3];
                long timestamp = Long.parseLong(parts[4]);
                app.addCall(uuid, dialer, receiver, timestamp);
            } else if (command.equals("updateCall")) {
                String uuid = parts[1];
                long timestamp = Long.parseLong(parts[2]);
                String action = parts[3];
                app.updateCall(uuid, timestamp, action);
            } else if (command.equals("printChronologicalReport")) {
                String phoneNumber = parts[1];
                app.printChronologicalReport(phoneNumber);
            } else if (command.equals("printReportByDuration")) {
                String phoneNumber = parts[1];
                app.printReportByDuration(phoneNumber);
            } else {
                app.printCallsDuration();
            }
        }

    }
}

class TelcoApp {
    Map<String, Set<Call>> callsByNumber;
    Map<String, Call> callsByUuid;

    public TelcoApp() {
        callsByNumber = new HashMap<>();
        callsByUuid = new HashMap<>();
    }

    public void addCall(String uuid, String dialer, String receiver, long timestamp) {
        Call c = new Call(uuid, dialer, receiver, timestamp);
        callsByNumber.putIfAbsent(dialer, new TreeSet<>(Call.sortedByTimesStamp));
        callsByNumber.get(dialer).add(c);

        callsByNumber.putIfAbsent(receiver, new TreeSet<>(Call.sortedByTimesStamp));
        callsByNumber.get(receiver).add(c);

        callsByUuid.put(uuid, c);
    }

    public void updateCall(String uuid, long timestamp, String action) {
        if (action.equals("ANSWER")) {
            callsByUuid.get(uuid).answer(timestamp);
        } else if (action.equals("END")) {
            callsByUuid.get(uuid).end(timestamp);
        } else if (action.equals("HOLD")) {
            callsByUuid.get(uuid).hold(timestamp);
        } else {
            callsByUuid.get(uuid).resume(timestamp);
        }

    }

    public void printChronologicalReport(String phoneNumber) {
        callsByNumber.get(phoneNumber).forEach(call -> Print(phoneNumber, call));
    }

    public void printReportByDuration(String phoneNumber) {
        callsByNumber.get(phoneNumber).stream().sorted(Call.sortedByCallDuration).forEach(call -> Print(phoneNumber, call));
    }

    public void printCallsDuration() {
        Map<String, Long> map = callsByUuid.values().stream().collect(Collectors.groupingBy(
                call -> String.format("%s <-> %s", call.dialer, call.receiver),
                Collectors.summingLong(Call::callDuration)
        ));
        map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEach(stringLongEntry -> System.out.printf("%s : %s%n", stringLongEntry.getKey(), DurationConverter.convert(stringLongEntry.getValue())));
    }

    private static void Print(String phoneNumber, Call call) {
        if (call.dialer.equals(phoneNumber)) {
            if (call.callDuration() == 0) {
                System.out.printf("D %s %d %s %s%n", call.receiver, call.callStarted, "MISSED CALL", DurationConverter.convert(call.callDuration()));
            } else {
                System.out.printf("D %s %d %d %s%n", call.receiver, call.callStarted, call.callEnded, DurationConverter.convert(call.callDuration()));
            }

        } else {
            if (call.callDuration() == 0) {
                System.out.printf("R %s %d %s %s%n", call.dialer, call.callStarted, "MISSED CALL", DurationConverter.convert(call.callDuration()));
            } else {
                System.out.printf("R %s %d %d %s%n", call.dialer, call.callStarted, call.callEnded, DurationConverter.convert(call.callDuration()));
            }
        }
    }
}


class Call {
    String uuid, dialer, receiver;
    long timestamp;
    long callStarted, callEnded, totalTimeInHold = 0, holdStarted;

    boolean missedCall;

    CallState state = new CallStartedState(this);

    static Comparator<Call> sortedByTimesStamp = Comparator.comparing(Call::getTimestamp);
    static Comparator<Call> sortedByCallDuration = Comparator.comparing(Call::callDuration).thenComparing(Call::getTimestamp).reversed();


    public Call(String uuid, String dialer, String receiver, long timestamp) {
        this.uuid = uuid;
        this.dialer = dialer;
        this.receiver = receiver;
        this.timestamp = timestamp;
    }

    public long callDuration() {
        if (missedCall) {
            return 0;
        } else {
            return (callEnded - callStarted) - totalTimeInHold;
        }
    }

    void answer(long timestamp) {
        state.answer(timestamp);
    }

    void hold(long timestamp) {
        state.hold(timestamp);
    }

    void resume(long timestamp) {
        state.resume(timestamp);
    }

    void end(long timestamp) {
        state.end(timestamp);
    }

    public long getTimestamp() {
        return timestamp;
    }
}

interface ICallState {
    void answer(long timestamp);

    void hold(long timestamp);

    void end(long timestamp);

    void resume(long timestamp);
}

abstract class CallState implements ICallState {
    Call call;

    public CallState(Call call) {
        this.call = call;
    }
}

class CallStartedState extends CallState {

    public CallStartedState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {
        call.callStarted = timestamp;
        call.state = new CallInProgressState(call);
    }

    @Override
    public void hold(long timestamp) {

    }

    @Override
    public void end(long timestamp) {
        call.callStarted = timestamp;
        call.callEnded = timestamp;
        call.missedCall = true;
        call.state = new TerminatedCallState(call);
    }

    @Override
    public void resume(long timestamp) {

    }
}

class TerminatedCallState extends CallState {

    public TerminatedCallState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {

    }

    @Override
    public void hold(long timestamp) {

    }

    @Override
    public void end(long timestamp) {

    }

    @Override
    public void resume(long timestamp) {

    }
}

class CallInProgressState extends CallState {

    public CallInProgressState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {

    }

    @Override
    public void hold(long timestamp) {
        call.holdStarted = timestamp;
        call.state = new CallHoldState(call);
    }

    @Override
    public void end(long timestamp) {
        call.callEnded = timestamp;
        call.state = new TerminatedCallState(call);
    }

    @Override
    public void resume(long timestamp) {

    }
}

class CallHoldState extends CallState {

    public CallHoldState(Call call) {
        super(call);
    }

    @Override
    public void answer(long timestamp) {

    }

    @Override
    public void hold(long timestamp) {

    }

    @Override
    public void end(long timestamp) {
        call.callEnded = timestamp;
        call.totalTimeInHold += (timestamp - call.holdStarted);
        call.state = new TerminatedCallState(call);
    }

    @Override
    public void resume(long timestamp) {
        call.totalTimeInHold += (timestamp - call.holdStarted);
        call.state = new CallInProgressState(call);
    }
}

