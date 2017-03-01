var map = new java.util.HashMap();

rec
    .csv("BufferedCachingTeeTest.csv", "name, type, age").stream()
    .forEach(rec.action(function (x) {
        rec.println(x.get("name"));
        map.put(x.get("name"), x.get("age"));
    }));

rec.println("==================");

rec
    .csv("BufferedCachingTeeTest.csv", "name, type, age")
    .to(rec.target(function (record) {
        rec.println(record.get("age"));
    }));

rec.println(map.size());

rec.println("==================");

var counter = rec.counter(function (it) {
    return true
});


rec.stream(rec.csv("BufferedCachingTeeTest.csv", "name, type, age").stream().filter(rec.pred(function (rec) {
    return rec.get("name").length() > 4
}))).tee(counter).to(rec.target(function (record) {
    rec.println(record.get("name"));
}));

rec.println(counter.count());

rec.println("================");

rec
    .csv("BufferedCachingTeeTest.csv", "name, type, age")
    .tee(rec.unique("name", "type"))
    .to(rec.dummy());