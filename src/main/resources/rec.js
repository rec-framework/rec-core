
module.exports = context.isNative() ? require("rec/native") : require("rec/cloud");