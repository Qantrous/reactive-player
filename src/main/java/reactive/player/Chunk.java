package reactive.player;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Chunk {
    private final int id;
    private final byte[] data;
    private final int processorId;

    public Chunk(int id, byte[] data, int processorId) {
        this.id = id;
        this.data = data;
        this.processorId = processorId;
    }

    public int getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "id=" + id +
                "data=" + new String(data, StandardCharsets.UTF_8) +
                '}';
    }

    public int getProcessorId() {
        return this.processorId;
    }
}
