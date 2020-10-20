package net.kimleo.rec.v2.stream.adapter;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GeneratingSpliteratorAdapter<T> extends Spliterators.AbstractSpliterator<T> {


    private final Supplier<T> supplier;

    public GeneratingSpliteratorAdapter(Supplier<T> supplier) {
        super(Long.MAX_VALUE, 0);
        this.supplier = supplier;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        T item = supplier.get();
        if (item != null) {
            action.accept(item);
            return true;
        }

        return false;
    }
}
