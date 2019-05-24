package net.kimleo.rec.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PrimitiveIterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Getters {

    private Map<String, Integer> headerPositions;

    public static final Getters NO_HEADER = from(Collections.emptyList());

    private Getters(List<String> headers) {
        PrimitiveIterator.OfInt index = IntStream.range(0, headers.size()).iterator();
        this.headerPositions = headers.stream().collect(Collectors.toMap(Function.identity(), x -> index.nextInt()));
    }

    public Getter of(Record record) {
        return new Getter(record::at, headerPositions::get);
    }

    public static Getters from(List<String> headers) {
        return new Getters(headers);
    }

    public static class Getter {

        final Function<Integer, Cell> retriever;
        final Function<String, Integer> mapper;


        Getter(Function<Integer, Cell> retriever, Function<String, Integer> mapper) {
            this.retriever = retriever;
            this.mapper = mapper;
        }


        public Cell get(int index) {
            Cell cell = retriever.apply(index);
            if (cell == null) {
                return Cell.empty();
            }
            return cell;
        }

        public Cell get(String key) {
            Integer index = mapper.apply(key);
            if (index == null) {
                return Cell.empty();
            }
            return get(index);
        }
    }

}
