'use strict';

const {csv, target, restartable, println} = require("rec");

const {format} = java.lang.String;

restartable(csv("restartability.csv", "first, second, third, forth"))
    .to(target(function ({first, second})
        println(format("%s => %s", first, second))));