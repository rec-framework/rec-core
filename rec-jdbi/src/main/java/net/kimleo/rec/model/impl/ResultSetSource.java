package net.kimleo.rec.model.impl;

import net.kimleo.rec.data.ColumnVector;
import net.kimleo.rec.data.DataRow;
import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.DataType;
import net.kimleo.rec.data.Schema;
import net.kimleo.rec.model.Source;
import net.kimleo.rec.stream.adapter.GeneratingSpliteratorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetSource implements Source {
    private final ResultSet rs;
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSource.class);

    public ResultSetSource(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public Stream<DataSet> stream() {
        return StreamSupport.stream(new GeneratingSpliteratorAdapter<DataSet>(() -> {
            try {
                if (rs.next()) {
                    ResultSetMetaData meta = rs.getMetaData();
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
                } else {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }), false);
    }
}
