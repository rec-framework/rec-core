package net.kimleo.rec.v2.model.impl;

import lombok.extern.slf4j.Slf4j;
import net.kimleo.rec.common.concept.Mapped;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.mapper.ResultSetMapper;
import net.kimleo.rec.v2.stream.adapter.GeneratingSpliteratorAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

@Slf4j
public class ResultSetSource implements Source<Mapped<String>> {
    private final ResultSet rs;

    public ResultSetSource(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public Stream<Mapped<String>> stream() {
        return StreamSupport.stream(new GeneratingSpliteratorAdapter<Mapped<String>>(() -> {
            try {
                if (rs.next()) {
                    log.info(format("Get next item in result set #[%d]", rs.hashCode()));
                    return new ResultSetMapper(rs);
                } else {
                    return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }),false);
    }
}
