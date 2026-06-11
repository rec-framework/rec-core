package net.kimleo.rec.utils;

import net.kimleo.rec.common.Pair;
import net.kimleo.rec.common.exception.ResourceAccessException;
import net.kimleo.rec.data.ColumnVector;
import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataType;
import net.kimleo.rec.data.Schema;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Records {

    public static ByteBuffer encode(DataSet record, Schema schema) {
        int cols = schema.columnCount();
        ByteBuffer buf = ByteBuffer.allocate(sizeof(record, schema));
        buf.putInt(cols);
        for (int i = 0; i < cols; i++) {
            String val = record.getString(i);
            byte[] bytes = val != null ? val.getBytes() : new byte[0];
            buf.putInt(bytes.length);
            buf.put(bytes);
        }
        return buf;
    }

    private static int sizeof(DataSet record, Schema schema) {
        int size = 4;
        for (int i = 0; i < schema.columnCount(); i++) {
            size += 4;
            String val = record.getString(i);
            size += val != null ? val.getBytes().length : 0;
        }
        return size;
    }

    public static Pair<List<String>, Integer> decode(ByteBuffer buffer, int readPos, int expectedCols) {
        buffer.position(readPos);
        int cols = buffer.getInt();
        if (cols != expectedCols) return null;
        ArrayList<String> items = new ArrayList<>(cols);
        for (int i = 0; i < cols; i++) {
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
