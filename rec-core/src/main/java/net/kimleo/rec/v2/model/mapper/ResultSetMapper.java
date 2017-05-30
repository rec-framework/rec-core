package net.kimleo.rec.v2.model.mapper;

import net.kimleo.rec.concept.Mapped;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultSetMapper implements Mapped<String> {

    private final ResultSet rs;

    public ResultSetMapper(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public String get(String field) {
        try {
            return rs.getObject(field).toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> keys() {
        ArrayList<String> keys = new ArrayList<>();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                keys.add(metaData.getColumnName(i + 1));
            }
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return keys;
    }
}
