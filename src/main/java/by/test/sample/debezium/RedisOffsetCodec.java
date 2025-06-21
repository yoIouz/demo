package by.test.sample.debezium;

import io.lettuce.core.codec.RedisCodec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RedisOffsetCodec implements RedisCodec<String, byte[]> {

    @Override
    public String decodeKey(ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
    }

    @Override
    public byte[] decodeValue(ByteBuffer bytes) {
        byte[] value = new byte[bytes.remaining()];
        bytes.get(value);
        return value;
    }

    @Override
    public ByteBuffer encodeKey(String key) {
        return StandardCharsets.UTF_8.encode(key);
    }

    @Override
    public ByteBuffer encodeValue(byte[] value) {
        return ByteBuffer.wrap(value);
    }
}
