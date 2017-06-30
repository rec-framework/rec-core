"use strict";

var rec = new net.kimleo.rec.v2.scripting.module.NativeRec(context);

var mod = require("rec/common").createCommon(rec);

mod.println = function () {
    rec.println.apply(rec, arguments);
};

mod.file = function (filename) {
    return rec.file(filename);
};

mod.flat = function (filename) {
    return rec.flat(filename);
};

mod.cache = function (size) {
    return rec.cache(size);
};

mod.restartable = function (source) {
    return rec.restartable(source);
};

module.exports = mod;