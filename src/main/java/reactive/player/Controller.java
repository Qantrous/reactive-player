package reactive.player;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    private List<AudioProcessor> audioProcessors;
    private List<AudioPlayer> audioPlayers;
    private int chunkCount;

    public Controller(List<AudioProcessor> audioProcessor, List<AudioPlayer> audioPlayers, int chunkCount) {
        this.audioProcessors = audioProcessor;
        this.audioPlayers = audioPlayers;
        this.chunkCount = chunkCount;
    }


    public Mono<List<Chunk>> getChunksById(int id) {
        List<Mono<Chunk>> results = audioProcessors.stream().map(p -> p.getChunk(id).subscribeOn(Schedulers.parallel())).collect(Collectors.toList());
        return Mono.zip(results, (Object[] chunksAsObjects) -> (Arrays.stream(chunksAsObjects).map(o -> (Chunk) o)).collect(Collectors.toList()));

    }

    private int timeToChunkId(int time) {
        return 0;
    }


//    public void play() {
//        Flux.range(0, 15).map(e -> {
//            System.out.println("GETTING CHUNK WITH ID " + e);
//            getChunksById(e).subscribeOn(Schedulers.parallel()).subscribe(i -> {
//               i.forEach(b -> {
//                   audioPlayers.get(b.getProcessorId()).play(b);
////                   System.out.println(b.getId());
//               });
//            });
//            return e;
//        }).subscribe();
//
//    }

    public Mono<String> play() {
        Scheduler playersScheduler = Schedulers.newParallel("qwerty");
        List<Integer> chunksToGet = new ArrayList<>();

        for (int i =0; i < chunkCount; i ++) {
            chunksToGet.add(i);
        }

        List<Mono<String>> results = chunksToGet.stream().map(chunkId -> {
            System.out.println("GETTING CHUNK WITH ID " + chunkId);
            return getChunksById(chunkId).flatMap((List<Chunk> chunks) -> {
                List<Mono<Boolean>> plays = chunks.stream().map(chunk ->
                        audioPlayers
                                .get(chunk.getProcessorId())
                                .play(chunk).subscribeOn(playersScheduler))
                        .collect(Collectors.toList());
                return Mono.zip(plays, (Object[] playResultsAsObject) -> "done");
            });
        }).collect(Collectors.toList());

        return Mono.zip(results, (Object[] playResultsAsObject) -> "done");
    }

}
