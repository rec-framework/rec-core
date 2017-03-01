var map = new java.util.HashMap();

rec
    .csv("BufferedCachingTeeTest.csv", "name, type, age").to(rec.target(function (record) {
        map.put(record.get("name"), record.get("age"));
        rec.println(record.get("name"))
    }));

rec.println("==================");

rec
    .csv("BufferedCachingTeeTest.csv", "name, type, age")
    .to(rec.target(function (record) {
        rec.println(record.get("age"));
    }));

rec.println(map.size());

map.entrySet().forEach(rec.action(function (entry) {
    rec.println([entry.key, entry.value].join("=> "))
}));

rec.println("==================");

var counter = rec.counter(function (it) {
    return true
});


rec.csv("BufferedCachingTeeTest.csv", "name, type, age").filter(rec.pred(function (rec) {
    return rec.get("name").length() > 4
})).tee(counter).to(rec.target(function (record) {
    rec.println(record.get("name"));
}));

rec.println(counter.count());

rec.println("================");

rec
    .csv("BufferedCachingTeeTest.csv", "name, type, age")
    .tee(rec.unique("name", "type"))
    .to(rec.dummy());