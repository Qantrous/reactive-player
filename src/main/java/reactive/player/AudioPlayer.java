package reactive.player;

import reactor.core.publisher.Mono;

import java.util.Date;

public class AudioPlayer {

    private int id;


    public AudioPlayer(int id) {
        this.id = id;
    }


    public Mono<Boolean> play(Chunk chunk) {
        return Mono.fromCallable(() -> {
            System.out.println("AudioPlayer with id:" + id + " Started playing chunk: " + chunk  + " Date: " + new Date());
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            System.out.println("AudioPlayer with id:" + id + " finished playing chunk: " + chunk);
            return true;
        });

    }

    public int getId() {
        return id;
    }
}
