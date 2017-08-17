'use strict';

const map = new java.util.HashMap();

const {csv, counter, action, pred, target, unique, stateful, dummy} = require("rec");
const {assertTrue, assertEquals, fail} = require("rec/assert");

csv("CSVFileSource.csv", "name, type, age")
    .to(target(function ({name, age})
        map.put(name, age)));

csv("BufferedCachingTeeTest.csv", "name, type, age")
    .to(target(function ({type})
        assertTrue(type.length <= 5)));

assertTrue(map.size() == 1);

map.entrySet()
    .forEach(action(function ({value})
        assertEquals(value, "1999/99/99")));

const alwaysCounter =
    counter(function () true);

csv("CSVFileSource.csv", "name, type, age")
    .filter(pred(function ({name})
                    name.length > 4))
    .tee(alwaysCounter)
    .to(target(function () fail()));


assertTrue(alwaysCounter.count == 0);

let statefulCounter = stateful({count: 0},
    function (obj, {count})
        ({count: count + 1}));

csv("BufferedCachingTeeTest.csv", "name, type, age")
    .tee(unique("name", "type"))
    .to(dummy()
        .tee(statefulCounter));

assertEquals(statefulCounter.state.count, 3);

assertEquals(require("./test2").hello, "hello world");

const {one} = require("./test2");

assertTrue(one() == 1);

assertTrue(true);

assertEquals(1, 1);