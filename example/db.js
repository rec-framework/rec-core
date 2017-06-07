
const {resultSet, println, target} = require("rec");
const format = java.lang.String.format;

const handle = net.kimleo.dblite.DB.connect("jdbc:postgresql://localhost/test", "postgres", "").handle();

resultSet(handle.query("select * from simple_sql_test"))
    .to(target(function ({id, name}) {
        println(format("%s => %s", id, name));
    }));
