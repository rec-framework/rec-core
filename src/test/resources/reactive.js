const {println, reactive, csv, file, dummy, target, pred, stateless} = require("rec");

const rxTee = reactive()
    .tee(stateless(function ({first}) {
        println(first);
    }))
    .skip(1)
    .filter(pred(function ({first}) {
        return first.length > 5
    }));

rxTee.to(target(function (it) {
        println(it.first);
    }));

csv(file("BufferedCachingTeeTest.csv"), ",", "first, second, third")
    .tee(rxTee).to(dummy());