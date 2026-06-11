var createCommonObject = function (rec) {
    var mod = {
        "rec": rec
        , "action": function (fn) {
            return rec.action(fn)
        }
        , "pred": function (fn) {
            return rec.pred(fn)
        }
        , "stream": function (stream) {
            return rec.stream(stream)
        }
        , resultSet: function (resultSet) {
            return rec.resultSet(resultSet)
        }
        , query: function (url, user, password, sql) {
            return rec.query(url, user, password, sql)
        }
        , csv: function (reader, config, accessor) {
            return rec.csv(reader, config, accessor)
        }
        , dummy: function () { return rec.dummy() }
        , target: function (fn) {return rec.target(fn)}
        , counter: function (fn) {return rec.counter(fn)}
        , stateless: function (fn) {return  rec.stateless(fn) }
        , stateful: function (obj, fn) { return rec.stateful(obj, fn) }
        , collect: function (col) {return  rec.collect(col) }
        , unique: function (keys) { return rec.unique.apply(rec, arguments) }
        , "reactive": function () {
            return rec.reactive()
        }
    };

    // Copy plugin methods from rec to mod (jsonl, parquet, agent, etc.)
    for (var key in rec) {
        if (rec.hasOwnProperty(key) && !mod.hasOwnProperty(key)) {
            mod[key] = rec[key];
        }
    }

    return mod;
};

module.exports = {
    createCommon: createCommonObject
};
