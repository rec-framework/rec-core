package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.model.Target;
import net.kimleo.rec.model.Tee;

import java.util.function.Predicate;
import java.util.stream.Stream;

public class ReactiveTee implements Tee, Source {
    private Target target = null;
    protected final Tee parent;

    public ReactiveTee(Tee parent) {
        this.parent = parent;
    }

    @Override
    public DataSet emit(DataSet record) {
        return toTarget(record);
    }

    @Override
    public void to(Target target) {
        this.target = target;
    }

    @Override
    public Source filter(Predicate<DataSet> predicate) {
        return new FilterNode(this, predicate);
    }

    @Override
    public Source tee(Tee tee) {
        return new TeeNode(this, tee);
    }

    @Override
    public Source skip(int n) {
        return new SkipNode(this, n);
    }

    @Override
    public Stream<DataSet> stream() {
        throw new IllegalStateException("Reactive tee has no source");
    }

    protected DataSet toTarget(DataSet item) {
        if (target != null) {
            target.put(item);
        }
        return item;
    }

    class FilterNode extends ReactiveTee {
        private final Predicate<DataSet> filter;

        FilterNode(Tee parent, Predicate<DataSet> filter) {
            super(parent);
            this.filter = filter;
        }

        @Override
        public DataSet emit(DataSet record) {
            DataSet result = this.parent.emit(record);
            if (result != null) {
                if (filter.test(result)) {
                    return this.toTarget(result);
                }
            }
            return null;
        }
    }

    class TeeNode extends ReactiveTee {
        private final Tee tee;

        TeeNode(Tee parent, Tee tee) {
            super(parent);
            this.tee = tee;
        }

        @Override
        public DataSet emit(DataSet record) {
            DataSet result = this.parent.emit(record);
            if (result != null) {
                return this.toTarget(tee.emit(result));
            }
            return null;
        }
    }

    class SkipNode extends ReactiveTee {
        private int skips;

        SkipNode(Tee reactive, int n) {
            super(reactive);
            this.skips = n;
        }

        @Override
        public DataSet emit(DataSet record) {
            if (skips > 0) {
                this.skips--;
                this.parent.emit(record);
                return null;
            } else {
                return this.toTarget(this.parent.emit(record));
            }
        }
    }
}
