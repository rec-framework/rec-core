package net.kimleo.rec.v2.model.impl;

import net.kimleo.rec.common.concept.Mapped;
import net.kimleo.rec.v2.model.Source;
import net.kimleo.rec.v2.model.mapper.ResultSetMapper;
import net.kimleo.rec.v2.stream.adapter.GeneratingSpliteratorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.String.format;

public class ResultSetSource implements Source<Mapped<String>> {
    private final ResultSet rs;
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSource.class);

    public ResultSetSource(ResultSet rs) {
        this.rs = rs;
    }

    @Override
    public Stream<Mapped<String>> stream() {
        return StreamSupport.stream(new GeneratingSpliteratorAdapter<Mapped<String>>(() -> {
            try {
                if (rs.next()) {
                    LOGGER.info(format("Get next item in result set #[%d]", rs.hashCode()));
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
