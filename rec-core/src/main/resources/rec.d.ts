
//TODO: complete the record interface
interface Record {
    get(value: string): string;
    keys(): string[];
}

//TODO: complete the stream interface which is Java 8 Stream
interface Stream {

}

interface Source {
    tee(tee: Tee): Source;
    to(target: Target);
    filter(predicate: WrappedPredicate<Record>): Source
}

interface Target {
    tee(tee: Tee): Target;
}

interface Tee {
    source(): Source;
}

interface StatefulTee<T> extends Tee {
    state: T
}

interface ItemCounterTee extends Tee {
    count: number;
}

interface CollectTee<T> extends Tee {
    collect(): T;
}

interface WrappedPredicate<T> {
    test(value: T): boolean
}

interface WrappedAction<T> {
    apply(value: T): void
}

export module rec {
    function pred<T>(pred: (any: T) => boolean): WrappedPredicate<T>;

    function action<T>(func: (any: T) => void): WrappedAction<T>;

    function println(...args: any[]);

    //source
    function csv(source: string, format: string): Source;

    function stream(stream: Stream): Source;

    // target
    function target(func: (record: Record) => void): Target;

    function flat(file: string): Target;

    function dummy(): Target;

    //tee
    function counter(condition: (Record) => boolean): ItemCounterTee;

    function unique(...fields: string[]): Tee;

    function stateless(func: (record: Record) => void): Tee;

    function stateful<T>(state: T, reducer: (record: Record, state: T) => T): StatefulTee<T>;

    function cache(size: number): Tee;

    function collect<T>(collection: T): CollectTee<T>;
}