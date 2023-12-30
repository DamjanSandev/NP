package Napredno.Lab7.ChatRoom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

//eden testcase ne raboti pecati prazno mesto na start, vrednostite se tocno ispecateni

public class ChatSystemTest {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if ( k == 0 ) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr.addUser(jin.next());
                if ( k == 1 ) cr.removeUser(jin.next());
                if ( k == 2 ) System.out.println(cr.hasUser(jin.next()));
            }
            System.out.println();
            System.out.println(cr);
            n = jin.nextInt();
            if ( n == 0 ) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for ( int i = 0 ; i < n ; ++i ) {
                k = jin.nextInt();
                if ( k == 0 ) cr2.addUser(jin.next());
                if ( k == 1 ) cr2.removeUser(jin.next());
                if ( k == 2 ) cr2.hasUser(jin.next());
            }
            System.out.println(cr2);
        }
        if ( k == 1 ) {
            ChatSystem cs = new ChatSystem();
            Method[] mts = cs.getClass().getMethods();
            while ( true ) {
                String cmd = jin.next();
                if ( cmd.equals("stop") ) break;
                if ( cmd.equals("print") ) {
                    System.out.println(cs.getRoom(jin.next())+"\n");continue;
                }
                for ( Method m : mts ) {
                    if ( m.getName().equals(cmd) ) {
                        String[] params = new String[m.getParameterTypes().length];
                        for ( int i = 0 ; i < params.length ; ++i ) params[i] = jin.next();
                        m.invoke(cs, (Object[]) params);
                    }
                }
            }
        }

    }
}
class NoSuchRoomException extends Exception{
    public NoSuchRoomException(String roomName) {
        super(roomName);
    }
}

class User implements Comparable<User>{
    private final String username;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.username);
    }
}
class ChatRoom{
    private final String roomName;
    private final Set<User> users;
    public ChatRoom(String name){
        this.roomName=name;
        users=new TreeSet<>();
    }

    public void addUser(String username) {
        users.add(new User(username));
    }

    public void removeUser(String username) {
        for (User u : users) {
            if(u.getUsername().equals(username)){
                users.remove(u);
                return;
            }
        }
    }

    public boolean hasUser(String username) {
        return users.stream().anyMatch(user -> user.getUsername().equals(username));
    }
    public int numUsers(){
        return users.size();
    }

    public String getRoomName() {
        return roomName;
    }

    @Override
    public String toString() {
        StringBuilder sb=new StringBuilder();
        sb.append(roomName).append("\n");
        if(numUsers()==0){
            sb.append("EMPTY").append("\n");
        }
        else
        {
            for (User u : users) {
                sb.append(u.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
class NoSuchUserException extends Exception{
    public NoSuchUserException(String message) {
        super(message);
    }
}
class ChatSystem{
    Map<String,ChatRoom> rooms;
    Set<User> users;
    public ChatSystem(){
        rooms=new TreeMap<>();
        users=new TreeSet<>();
    }
    public void addRoom(String roomName){
        rooms.putIfAbsent(roomName,new ChatRoom(roomName));
    }
    public void removeRoom(String roomName){
        rooms.remove(roomName);
    }
    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        if(!rooms.containsKey(roomName)){
            throw new NoSuchRoomException(roomName);
        }
        return rooms.get(roomName);
    }
    public void register(String userName){
        ChatRoom room=rooms.values().stream().min(Comparator.comparing(ChatRoom::numUsers).thenComparing(ChatRoom::getRoomName)).orElse(null);
        users.add(new User(userName));
        if(room!=null){
            rooms.get(room.getRoomName()).addUser(userName);
        }
    }
    public void registerAndJoin(String userName, String roomName){
        users.add(new User(userName));
        if(rooms.containsKey(roomName)){
            rooms.get(roomName).addUser(userName);
        }
    }
    public void joinRoom(String userName, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if(users.stream().noneMatch(user -> user.getUsername().equals(userName))){
            throw new NoSuchUserException(userName);
        }
       if(!rooms.containsKey(roomName)){
           throw new NoSuchRoomException(roomName);
       }
       rooms.get(roomName).addUser(userName);
    }
    public void leaveRoom(String userName, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if(users.stream().noneMatch(user -> user.getUsername().equals(userName))){
            throw new NoSuchUserException(userName);
        }
        if(!rooms.containsKey(roomName)){
            throw new NoSuchRoomException(roomName);
        }
        rooms.get(roomName).removeUser(userName);
    }
    public void followFriend(String userName, String friend_username) throws NoSuchUserException {
        if(users.stream().noneMatch(user -> user.getUsername().equals(userName))){
            throw new NoSuchUserException(userName);
        }
        for (ChatRoom cr:rooms.values()) {
            if(cr.hasUser(friend_username)){
                cr.addUser(userName);
            }
        }
    }

}

