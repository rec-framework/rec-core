package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.concept.Mapped;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.mapper.ResultSetMapper;
import net.kimleo.rec.v2.stream.adapter.GeneratingSpliteratorAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ResultSetSource implements Source {
    private final ResultSet rs;

    public ResultSetSource(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public Stream<Mapped<String>> stream() {
        return StreamSupport.stream(new GeneratingSpliteratorAdapter<Mapped<String>>(() -> {
            try {
                while (rs.next()) {
                    new ResultSetMapper(rs);
                }
            } catch (SQLException e) {
                return null;
            }
            return null;
        }),false);
    }
}
