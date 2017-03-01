
var map = new java.util.HashMap();

var csv = rec.csv("BufferedCachingTeeTest.csv", "name, type, age");

var stream = csv.stream();

stream.forEach(rec.func(function (x) {
    rec.println(x.get("name"));
    map.put(x.get("name"), x.get("age"));
}));

rec.println("==================");

var csv2 = rec.csv("BufferedCachingTeeTest.csv", "name, type, age");

csv2.to(rec.target(function (record) {
    rec.println(record.get("age"));
}));

rec.println(map.size());

rec.println("==================");

var csv3 = rec.csv("BufferedCachingTeeTest.csv", "name, type, age");

var counter = rec.counter(function(it) { return true });


rec.stream(csv3.stream().filter(rec.pred(function (rec) {
    return rec.get("name").length() > 4
}))).tee(counter).to(rec.target(function (record) {
    rec.println(record.get("name"));
}));

rec.println(counter.count());
