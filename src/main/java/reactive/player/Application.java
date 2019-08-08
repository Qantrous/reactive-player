package reactive.player;

import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        Controller controller = createController();
        Scanner scanner = new Scanner(System.in);
//        controller.getChunksByTime(0).subscribe(System.out::println, System.out::println);
//        List<Chunk> chunks = controller.getChunksByTime(2).block();
//        System.out.println(chunks);

        try {
            Thread.sleep(1000);
            System.out.println("--------------------------STARTING--------------------------");
            controller.play().subscribe();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
//             controller.getChunksById(scanner.nextInt()).subscribe();
            //controller.getChunksById(scanner.nextInt()).subscribe();
//            controller.getChunksById(scanner.nextInt()).subscribe();
        }
    }

    private static Controller createController() {
        int chunks = 15;
        List<AudioProcessor> audioProcessors = new ArrayList<>();
        List<AudioPlayer> audioPlayers = new ArrayList<>();
        for (int i=0; i < 4; i++) {
            audioProcessors.add(new AudioProcessor(i,"file-" + (i + 1),chunks));
            audioPlayers.add(new AudioPlayer(i));
        }

        return new Controller(audioProcessors, audioPlayers, chunks);
    }
}
