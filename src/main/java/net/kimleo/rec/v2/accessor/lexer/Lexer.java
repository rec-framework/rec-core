package net.kimleo.rec.v2.accessor.lexer;

import net.kimleo.rec.common.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.kimleo.rec.util.CollectUtils.reversed;

public class Lexer {
    public static final Pattern PADDING_PATTERN = Pattern.compile("\\{(\\d+)}");


    public static Pair<Map<String, Integer>, Integer> buildFieldMapPair(String... fields) {
        final Map<String, Integer> accessorMap = new LinkedHashMap<>();
        final Pair<List<FieldType>, Integer> lex = lex(fields);
        final List<FieldType> accessors = lex.getFirst();
        final Integer leastCapacity = lex.getSecond();
        boolean reversed = buildFieldMaps(accessorMap, accessors, 0, 1);

        if (reversed) {
            buildFieldMaps(accessorMap, reversed(accessors), -1,-1);
        }

        return new Pair<>(accessorMap, leastCapacity);
    }

    private static boolean buildFieldMaps(Map<String, Integer> accessorMap,
                                          Iterable<FieldType> accessors,
                                          int index, int factor) {
        boolean reversed = false;
        for (FieldType accessor : accessors) {
            if (accessor instanceof FieldName) {
                accessorMap.put(((FieldName) accessor).getName(), index);
                index += factor;
            } else if (accessor instanceof Padding) {
                index += factor * ((Padding) accessor).getSize();
            } else if (accessor instanceof Placeholder) {
                reversed = true;
                break;
            }
        }
        return reversed;
    }

    public static Pair<List<FieldType>, Integer> lex(String[] fields) {

        int segmentSize = 1;
        int currentSegmentSize = 0;
        ArrayList<FieldType> accessors = new ArrayList<>();
        for (String field : fields) {
            if (field.trim().startsWith("{")) {
                Matcher matcher = PADDING_PATTERN.matcher(field);
                if (matcher.matches()) {
                    int amount = Integer.parseInt(matcher.group(1));
                    currentSegmentSize += amount;
                    accessors.add(new Padding(amount));
                } else {
                    throw new UnsupportedOperationException("Unknown format for padding: " + field);
                }
            } else if ("...".equals(field.trim())) {
                accessors.add(new Placeholder(field.trim()));
                segmentSize = Math.max(currentSegmentSize, segmentSize);
                currentSegmentSize = 0;
            } else {
                accessors.add(new FieldName(field.trim()));
                currentSegmentSize++;
            }
        }

        return new Pair<>(accessors, Math.max(currentSegmentSize, segmentSize));
    }
}
