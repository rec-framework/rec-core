
const {query, println, target} = require("rec");
const format = java.lang.String.format;

query("jdbc:postgresql://localhost/test", "postgres", "", "select * from simple_sql_test")
    .to(target(function ({id, name}) {
        println(format("%s => %s", id, name));
    }));
