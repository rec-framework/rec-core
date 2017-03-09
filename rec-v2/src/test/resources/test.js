const map = new java.util.HashMap();
const format = java.lang.String.format;
const {csv, counter, println, action, pred, target, unique, stateful} = rec;

csv("BufferedCachingTeeTest.csv", "name, type, age")
    .to(target(function ({name, age}) {
        map.put(name, age);
    }));

println("==================");

csv("BufferedCachingTeeTest.csv", "name, type, age")
    .to(target(function ({type}) {
        println(type);
    }));

println(map.size());

map.entrySet()
    .forEach(action(function ({key, value}) {
        println(format("%s => %s", key, value))
    }));

println("==================");

const alwaysCounter =
    counter(function () {
        return true
    });

csv("BufferedCachingTeeTest.csv", "name, type, age")
    .filter(pred(function ({name}) {
        return name.length > 4
    }))
    .tee(alwaysCounter)
    .to(target(function (record) {
        println(JSON.stringify(record));
    }));


println(alwaysCounter.count);

println("================");


let counter2 = stateful({count: 0},
    function ({name, type}, {count}) {
        println(format("%s: %s", name, type));
        return {count: count + 1};
    });

csv("BufferedCachingTeeTest.csv", "name, type, age")
    .tee(unique("name", "type"))
    .to(rec.dummy()
        .tee(counter2));

println(counter2.state.count);