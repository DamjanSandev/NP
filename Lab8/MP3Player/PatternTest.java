package Napredno.Lab8.MP3Player;

import java.util.ArrayList;
import java.util.List;


public class PatternTest {
    public static void main(String[] args) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player);
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player);
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player);
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player);
    }
}

class Song {
    private final String title;
    private final String artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Song{" + "title=" + title + ", artist=" + artist + '}';
    }
}

//Vasiot kod ovde
class MP3Player implements State {
    int currSong;
    List<Song> songList;
    State state;

    public MP3Player(List<Song> songList) {
        this.songList = songList;
        currSong = 0;
        state = new PausedState(this);
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void pressPlay() {
        state.pressPlay();
    }

    @Override
    public void pressStop() {
        state.pressStop();
    }

    @Override
    public void pressFWD() {
        state.pressFWD();
    }

    @Override
    public void pressREW() {
        state.pressREW();
    }

    public void printCurrentSong() {
        System.out.println(songList.get(currSong));
    }

    public void nextSong() {
        currSong++;
        currSong = currSong % songList.size();
    }

    public void prevSong() {
        currSong--;
        if (currSong == -1) currSong = songList.size() - 1;
    }

    public void reset() {
        currSong = 0;
        state = new PausedState(this);
    }

    @Override
    public String toString() {
        return "MP3Player{" + "currentSong = " + currSong + ", songList = " + songList + '}';
    }
}

interface State {
    void pressPlay();

    void pressStop();

    void pressFWD();

    void pressREW();

}

abstract class MP3State implements State {
    MP3Player mp3Player;

    public MP3State(MP3Player mp3Player) {
        this.mp3Player = mp3Player;
    }
}

class PlayingState extends MP3State {
    public PlayingState(MP3Player mp3Player) {
        super(mp3Player);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song is already playing");
    }

    @Override
    public void pressStop() {
        System.out.printf("Song %d is paused%n", mp3Player.currSong);
        mp3Player.setState(new PausedState(mp3Player));
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        mp3Player.nextSong();
        mp3Player.setState(new FWDState(mp3Player));
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        mp3Player.prevSong();
        mp3Player.setState(new RWDState(mp3Player));
    }

}

class PausedState extends MP3State {
    public PausedState(MP3Player mp3Player) {
        super(mp3Player);
    }

    @Override
    public void pressPlay() {
        System.out.printf("Song %d is playing%n", mp3Player.currSong);
        mp3Player.setState(new PlayingState(mp3Player));
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are already stopped");
        mp3Player.reset();
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        mp3Player.nextSong();
        mp3Player.setState(new FWDState(mp3Player));
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        mp3Player.prevSong();
        mp3Player.setState(new RWDState(mp3Player));
    }

}

class FWDState extends MP3State {
    public FWDState(MP3Player mp3Player) {
        super(mp3Player);
    }

    @Override
    public void pressPlay() {
        System.out.printf("Song %d is playing%n", mp3Player.currSong);
        mp3Player.setState(new PlayingState(mp3Player));
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are stopped");
        mp3Player.reset();
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        mp3Player.nextSong();
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        mp3Player.prevSong();
        mp3Player.setState(new RWDState(mp3Player));
    }

}

class RWDState extends MP3State {
    public RWDState(MP3Player mp3Player) {
        super(mp3Player);
    }

    @Override
    public void pressPlay() {
        System.out.printf("Song %d is playing%n", mp3Player.currSong);
        mp3Player.setState(new PlayingState(mp3Player));
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are stopped");
        mp3Player.reset();
    }

    @Override
    public void pressFWD() {
        System.out.println("Forward...");
        mp3Player.nextSong();
        mp3Player.setState(new FWDState(mp3Player));
    }

    @Override
    public void pressREW() {
        System.out.println("Reward...");
        mp3Player.prevSong();
        mp3Player.setState(new RWDState(mp3Player));
    }

}


