package reactive.player;

import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

public class AudioProcessor {
    private final int id;

    private final String filename;

    private final Map<Integer, Chunk> processedChunks;

    private Disposable disposable = null;

    private int chunkCount;

    public AudioProcessor(int id, String filename, int chunkCount) {
        this.id = id;
        this.filename = filename;
        this.processedChunks = new HashMap<>();
        this.chunkCount = chunkCount;

        processChunks();
    }

    /**
     * If no chunk by given id, should return Mono with empty chunk, but not Mono.empty
     * @param chunkId
     * @return
     */
    public Mono<Chunk> getChunk(int chunkId) {
        System.out.println("Getting chunk " + getChunkFullName(chunkId));
//        synchronized (this) {
            if (processedChunks.get(chunkId) != null) {
                System.out.println("Chunk " + getChunkFullName(chunkId) + " is already processed, getting from cache");
                return Mono.just(processedChunks.get(chunkId));
            } else {
                System.out.println("Chunk " + getChunkFullName(chunkId) + " is not yet processed, processing");
                disposeBackgroundProcessing();
//                continueProcessingWithChunkId(chunkId);
                return Mono.fromCallable(() -> processChunk(chunkId));

            }
//        }
    }

    private int getChunksCount() {
        Random rand = new Random();
        return 15;
    }

    private void processChunks() {
        int chunksCount = getChunksCount();
        System.out.println("Starting processing file " + filename + " with " + chunksCount + " chunk(s)");
        continueProcessingWithChunkId(0);
//        s.dispose();
//        s = Flux.range(5, chunksCount).map((Function<Integer, Object>) this::processChunk).subscribeOn(Schedulers.parallel()).subscribe();

    }

    private Chunk processChunk(int chunkId) {
        System.out.println("Processing chunk " + getChunkFullName(chunkId));
        try {
            Random random = new Random();
            int rand = 1;
            Thread.sleep(rand * 200);
        } catch (InterruptedException e) {

        }
        String chunkData = filename + "-" + chunkId;
        Chunk chunk = new Chunk(chunkId, chunkData.getBytes(), this.id);
        synchronized (this) {
            processedChunks.put(chunkId, chunk);
        }
        System.out.println("Chunk " + getChunkFullName(chunkId) + " has been processed");
        return chunk;
    }

    private Chunk proccesChunkAndUpdateProcessingId(int chunkId) {
        System.out.println("Processinghhhh chunk " + getChunkFullName(chunkId));
        try {
            Thread.sleep(500);
        }catch (Exception e) {

        }
        String chunkData = filename + "-" + chunkId;

        Chunk chunk = new Chunk(chunkId, chunkData.getBytes(),id);
        synchronized (this) {
            processedChunks.put(chunkId, chunk);
        }
        System.out.println("Chunk " + getChunkFullName(chunkId) + " has been processed");

        return chunk;
    }

    public void disposeBackgroundProcessing() {
        System.out.println("CHECKING DISPOSABLE STATUS");
        if (disposable != null && !disposable.isDisposed()) {
            System.out.println("Disposing process");
            disposable.dispose();
        }
    }


    private void continueProcessingWithChunkId(int chunkId) {
        disposable = Flux.range(chunkId, getChunksCount()- chunkId).map((Function<Integer, Object>) this::processChunk).subscribeOn(Schedulers.parallel()).subscribe();

    }

    private String getChunkFullName(int chunkId) {
        return filename + " [" + chunkId + "]";
    }
}
