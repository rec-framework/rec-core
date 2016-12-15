
declare interface Cell {
    value: string
}

declare interface Record {
    cells: Cell[]
    origin: boolean
    size: number
    text: string

    get(index: number): string
}

declare interface RecConfig {

}

declare interface JavaString {
    length(): number
}

declare interface RecObj {
    get(attr: string): JavaString
}

declare interface RecordSet {
    where(fn: (RecObj) => boolean): RecordSet
    select(fields: string[]): RecordSet
    verify(fn: (RecObj) => boolean): RecordSet
    contains(key: string, value: string): boolean
    each(fn: (RecObj) => void): void

    // TODO
    save(name: string)
}

declare class Rec {
    constructor(basePath: string)

    constructor(config: RecConfig)

    basePath: string;

    from(name: string): RecordSet

    rule(name: string, fn: (RecObj) => boolean): boolean

    // TODO
    transform(name: string, fn: (RecObj) => RecObj): RecordSet

    // TODO
    relation(name: string, fn: (RecObj, RecObj) => boolean): boolean
}

declare var out: {
    println(obj: any)
};
