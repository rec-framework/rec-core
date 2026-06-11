package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.ColumnVector;
import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataType;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.model.Source;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class JdbiSource implements Source {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbiSource.class);

    private final Jdbi jdbi;
    private final String sql;

    public JdbiSource(Jdbi jdbi, String sql) {
        this.jdbi = jdbi;
        this.sql = sql;
    }

    @Override
    public Stream<DataSet> stream() {
        LOGGER.info("Executing query: {}", sql);
        try (Handle handle = jdbi.open()) {
            List<DataSet> results = handle.createQuery(sql)
                    .map((rs, ctx) -> toDataSet(rs.getMetaData(), rs))
                    .list();
            return results.stream();
        }
    }

    public Stream<DataSet> stream(Object... args) {
        LOGGER.info("Executing query: {} with {} params", sql, args.length);
        try (Handle handle = jdbi.open()) {
            Query query = handle.createQuery(sql);
            for (int i = 0; i < args.length; i++) {
                query.bind(i, args[i]);
            }
            List<DataSet> results = query
                    .map((rs, ctx) -> toDataSet(rs.getMetaData(), rs))
                    .list();
            return results.stream();
        }
    }

    private static DataSet toDataSet(java.sql.ResultSetMetaData meta,
                                      java.sql.ResultSet rs) throws java.sql.SQLException {
        int cols = meta.getColumnCount();
        List<String> names = new ArrayList<>();
        List<DataType> types = new ArrayList<>();
        for (int i = 1; i <= cols; i++) {
            names.add(meta.getColumnLabel(i));
            types.add(DataType.STRING);
        }
        Schema schema = Schema.typed(names, types);
        ColumnVector[] vectors = new ColumnVector[cols];
        for (int i = 0; i < cols; i++) {
            Object val = rs.getObject(i + 1);
            ColumnVector.Builder vb = ColumnVector.builder(DataType.STRING, 1);
            vb.add(val != null ? val.toString() : null);
            vectors[i] = vb.build();
        }
        return new DataRow(schema, vectors, 0);
    }
}
