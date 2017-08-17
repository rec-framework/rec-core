
//TODO: complete the record interface
interface Record {
    get(value: string): string;
    keys(): string[];
}

//TODO: complete the stream interface which is Java 8 Stream
interface Stream {

}

interface Consumer<T> {
    consume(t: T)
}

interface Predicate<T> {
    test(t: T): boolean
}

interface Source {
    tee(tee: Tee): Source;
    to(target: Target);
    filter(predicate: Predicate<Record>): Source
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

export module rec {
    function pred<T>(pred: (any: T) => boolean): Predicate<T>;

    function action<T>(func: (any: T) => void): Consumer<T>;

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

module rec.logging {

    interface Logger {
        error(message: string);
        warn(message: string);
        info(message: string);
        debug(message: string);
        trace(message: string);
    }

    function logger(name: string): Logger;
}

module rec.assert {
    function assertTrue(test: boolean)
    function assertEquals(left: any, right: any)
    function assertNotNull(obj: any)
    function fail()
}

interface CommonJSModule {
    id: string
    exports: any
}

declare const module: CommonJSModule;
