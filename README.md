# Rec


[![Build Status](https://travis-ci.org/rec-framework/rec-core.svg?branch=master)](https://travis-ci.org/rec-framework/rec-core)
[![](https://jitpack.io/v/rec-framework/rec-core.svg)](https://jitpack.io/#rec-framework/rec-core)
[![Join the chat at https://gitter.im/rec-framework/rec-core](https://badges.gitter.im/rec-framework/rec-core.svg)](https://gitter.im/rec-framework/rec-core?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## ** WARNING **

Since JavaScript become more unstable and rhino engine are no longer actively maintained, I've decided to abandon the scripting features
from now, using a self-made DSL instead.

Most of the scripting features are marked deprecated and will be moved soon.

## Redesign

Rec now is a data/stream processing utility support massive operations and high performance. With
the DSL inspired by [matz/streem](https://github.com/matz/streem), you can do most of the data 
processing / transformation with in a single script.

### Concepts

 - Source
 - Target
 - Tee / Pipe
 - Operators

### Examples

`cat`:
```
stdin | stdout
```

`wc -l`:
```
stdin | count | stdout
```
