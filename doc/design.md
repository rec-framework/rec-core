Rec
======

## Main components:
- Source
- Tee
- Target
- Re-enterable
- Cached

## Techs:
- Stream
- Bytebuffers (Direct, Mapped)

## Interfaces:
```typescript
interface Record {}

interface Source {
    stream(): Stream<Record>
    to(target: Target)
    tee(tee: Tee): Source
}

interface Tee {
    emit(record: Record)
    source(): Source
    to(target: Target) = source().to(target)
}

interface Target {
    put(record: Record);
    tee(tee: Tee): Target
    putAll(source: Source) = source.stream.forEach(this::put)
}
```