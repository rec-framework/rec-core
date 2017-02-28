package net.kimleo.rec.v2.utils;

import net.kimleo.rec.Pair;
import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.v2.exception.ResourceAccessException;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Records {
    public static ByteBuffer encode(Mapped<String> record, List<String> keys) {
        int size = keys.size();
        ByteBuffer byteBuffer = ByteBuffer.allocate(sizeof(record, keys));

        byteBuffer.putInt(size);
        for (String key : keys) {
            String field = record.get(key);
            byteBuffer.putInt(field.getBytes().length);
            byteBuffer.put(field.getBytes());
        }
        return byteBuffer;
    }

    private static int sizeof(Mapped<String> record, List<String> keys) {
        int size = 4;

        for (String key : keys) {
            size += 4;
            size += record.get(key).getBytes().length;
        }
        return size;
    }

    public static Pair<List<String>, Integer> decode(ByteBuffer buffer, int readPos, int expectedSize) {

        buffer.position(readPos);
        int size = buffer.getInt();
        if (size != expectedSize) return null;
        ArrayList<String> items = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            int length = buffer.getInt();
            byte[] bytes = new byte[length];
            buffer.get(bytes, 0, length);
            items.add(new String(bytes));

        }

        return new Pair<>(items, buffer.position() - readPos);
    }

    public static void dump(File file, int expectSize) {
        try {
            long length = file.length();
            MappedByteBuffer byteBuffer = FileChannel.open(file.toPath(), StandardOpenOption.READ)
                    .map(FileChannel.MapMode.READ_ONLY, 0, (int) length);

            int readPos = 0;

            while (readPos < byteBuffer.limit()) {
                Pair<List<String>, Integer> result = decode(byteBuffer, readPos, expectSize);
                if (result == null) break;
                readPos += result.second;

                System.out.println(result.first.stream().collect(Collectors.joining(", ")));
            }
        } catch (IOException e) {
            throw new ResourceAccessException("Unable to dump file: [" + file.getName() + "]", e);
        }
    }

}
