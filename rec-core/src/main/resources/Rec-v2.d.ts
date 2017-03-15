
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

interface Rec {
    pred<T>(pred: (any: T) => boolean): WrappedPredicate<T>;
    action<T>(func: (any: T) => void): WrappedAction<T>;
    println(...args: any[]);

    //source
    csv(source: string, format: string): Source;
    stream(stream: Stream): Source;

    // target
    target(func: (record: Record) => void): Target;
    flat(file: string): Target;
    dummy(): Target;

    //tee
    counter(condition: (Record) => boolean): ItemCounterTee;
    unique(...fields: string[]): Tee;

    stateless(func: (record: Record) => void): Tee;
    stateful<T>(state: T, reducer: (record: Record, state: T) => T): StatefulTee<T>;

    cache(size: number): Tee;
    collect<T>(collection: T): CollectTee<T>;
}

declare const rec: Rec;