'use strict';

const {csv, target, restartable, println, file} = require("rec");
const {assertTrue} = require('rec/assert');

const {format} = java.lang.String;

restartable(csv(file("restartability.csv"), ",", "first, second, third, forth"))
    .to(target(function ({first, second}) {
        println(first, second);
    }));
