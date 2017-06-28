
var createCommonObject = function (rec) {
    return {
        rec: rec
        , action: function (fn) rec.action(fn)
        , pred: function (fn) rec.pred(fn)
        , stream: function (stream) rec.stream(stream)
        , resultSet: function (resultSet) rec.resultSet(resultSet)
        , csv: function (reader, config, accessor) rec.csv(reader, config, accessor)
        , dummy: function () rec.dummy()
        , target: function (fn) rec.target(fn)
        , counter: function (fn) rec.counter(fn)
        , stateless: function (fn) rec.stateless(fn)
        , stateful: function (obj, fn) rec.stateful(obj, fn)
        , collect: function (col) rec.collect(col)
        , unique: function (keys) rec.unique.apply(rec, arguments)
    };
};

module.exports = {
    createCommon: createCommonObject
};