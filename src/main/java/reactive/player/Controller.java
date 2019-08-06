package reactive.player;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {

    private List<AudioProcessor> audioProcessors;
    private List<AudioPlayer> audioPlayers;

    public Controller(List<AudioProcessor> audioProcessor, List<AudioPlayer> audioPlayers) {
        this.audioProcessors = audioProcessor;
        this.audioPlayers = audioPlayers;
    }

    public Mono<List<Chunk>> getChunksByTime(int time) {
        int chunkId = timeToChunkId(time);
        List<Mono<Chunk>> results = audioProcessors.stream().map(p -> p.getChunk(chunkId).subscribeOn(Schedulers.parallel())).collect(Collectors.toList());
        return Mono.zip(results, (Object[] chunksAsObjects) -> (Arrays.stream(chunksAsObjects).map(o -> (Chunk) o)).collect(Collectors.toList()));
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
        List<Integer> chunksToGet = new ArrayList<>();
        for (int i =0; i < 15; i ++) {
            chunksToGet.add(i);
        }

        List<Mono<String>> results = chunksToGet.stream().map(chunkId -> {
            System.out.println("GETTING CHUNK WITH ID " + chunkId);
            return getChunksById(chunkId).flatMap((List<Chunk> chunks) -> {
                List<Mono<Boolean>> plays = chunks.stream().map(chunk -> audioPlayers.get(chunk.getProcessorId()).play(chunk).subscribeOn(Schedulers.parallel())).collect(Collectors.toList());
                return Mono.zip(plays, (Object[] playResultsAsObject) -> "done");
            });
        }).collect(Collectors.toList());

        return Mono.zip(results, (Object[] playResultsAsObject) -> "done");
    }
}
