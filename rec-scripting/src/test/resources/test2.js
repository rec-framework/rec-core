'use strict';

const LOGGER = require("rec/logging").getLogger(module.id);

function one() {
    LOGGER.info("calling one() function");
    return "1";
}

module.exports = {
    hello: "hello world",
    one: one
};