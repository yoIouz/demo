package by.test.sample.debezium;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.apache.kafka.connect.runtime.WorkerConfig;
import org.apache.kafka.connect.storage.OffsetBackingStore;
import org.apache.kafka.connect.util.Callback;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static by.test.sample.utils.ApplicationConstants.OFFSET_STORAGE_PREFIX;

public class RedisOffsetBackingStore implements OffsetBackingStore {

    private String redisUri;

    private RedisClient redisClient;

    private StatefulRedisConnection<String, byte[]> connection;

    private RedisCommands<String, byte[]> sync;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void start() {}

    @Override
    public void stop() {
        connection.close();
        redisClient.shutdown();
        executor.shutdown();
    }

    @Override
    public Future<Map<ByteBuffer, ByteBuffer>> get(Collection<ByteBuffer> keys) {
        return executor.submit(() -> {
            Map<ByteBuffer, ByteBuffer> result = new HashMap<>();
            for (ByteBuffer key : keys) {
                String redisKey = this.keyToString(key);
                byte[] value = sync.get(redisKey);
                if (value != null) {
                    result.put(key, ByteBuffer.wrap(value));
                }
            }
            return result;
        });
    }

    @Override
    public Future<Void> set(Map<ByteBuffer, ByteBuffer> values, Callback<Void> callback) {
        return executor.submit(() -> {
            try {
                for (Map.Entry<ByteBuffer, ByteBuffer> entry : values.entrySet()) {
                    String redisKey = this.keyToString(entry.getKey());
                    byte[] redisValue = this.toByteArray(entry.getValue());
                    sync.set(redisKey, redisValue);
                }
                callback.onCompletion(null, null);
            } catch (Exception e) {
                callback.onCompletion(e, null);
            }
            return null;
        });
    }

    @Override
    public Set<Map<String, Object>> connectorPartitions(String s) {
        return Collections.emptySet();
    }

    @Override
    public void configure(WorkerConfig workerConfig) {
        Object uri = workerConfig.originals().get("offset.storage.redis.uri");
        if (uri != null) {
            redisUri = uri.toString();
        }
        redisClient = RedisClient.create(redisUri);
        connection = redisClient.connect(new RedisOffsetCodec());
        sync = connection.sync();
    }

    private String keyToString(ByteBuffer key) {
        return OFFSET_STORAGE_PREFIX + Base64.getEncoder().encodeToString(toByteArray(key));
    }

    private byte[] toByteArray(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.remaining()];
        buffer.duplicate().get(bytes);
        return bytes;
    }
}
