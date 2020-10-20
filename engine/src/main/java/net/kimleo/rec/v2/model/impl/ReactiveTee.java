package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.Target;
import net.kimleo.rec.v2.model.Tee;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReactiveTee<T> implements Tee<T>, Source<T> {
    private Target<T> target = null;
    protected final Tee<T> parent;

    public ReactiveTee(Tee<T> parent) {
        this.parent = parent;
    }

    @Override
    public T emit(T record) {
        return toTarget(record);
    }

    @Override
    public void to(Target<T> target) {
        this.target = target;
    }

    @Override
    public Source<T> filter(Predicate<T> predicate) {
        return new FilterNode(this, predicate);
    }

    @Override
    public Source<T> tee(Tee<T> tee) {
        return new TeeNode(this, tee);
    }

    @Override
    public Source<T> skip(int n) {
        return new SkipNode(this, n);
    }

    @Override
    public Stream<T> stream() {
        throw new IllegalStateException("Reactive tee has no source");
    }

    protected T toTarget(T item) {
        if (target != null) {
            target.put(item);
        }
        return item;
    }

    class FilterNode extends ReactiveTee<T>{
        private final Predicate<T> filter;

        FilterNode(Tee<T> parent, Predicate<T> filter) {
            super(parent);
            this.filter = filter;
        }

        @Override
        public T emit(T record) {
            T result = this.parent.emit(record);
            if (result != null) {
                if (filter.test(result)) {
                    return this.toTarget(result);
                }
            }
            return null;
        }
    }

    class TeeNode extends ReactiveTee<T> {

        private final Tee<T> tee;

        TeeNode(Tee<T> parent, Tee<T> tee) {
            super(parent);
            this.tee = tee;
        }

        @Override
        public T emit(T record) {
            T result = this.parent.emit(record);
            if (result != null) {
                return this.toTarget(tee.emit(result));
            }
            return null;
        }
    }

    class SkipNode extends ReactiveTee<T> {
        private int skips;

        SkipNode(Tee<T> reactive, int n) {
            super(reactive);
            this.skips = n;
        }

        @Override
        public T emit(T record) {

            if (skips > 0) {
                this.skips --;
                this.parent.emit(record);
                return null;
            } else {
                return this.toTarget(this.parent.emit(record));
            }
        }
    }
}


