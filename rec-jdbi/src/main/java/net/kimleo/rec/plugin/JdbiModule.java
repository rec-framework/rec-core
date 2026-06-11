package net.kimleo.rec.plugin;

import net.kimleo.rec.model.impl.JdbiSource;
import net.kimleo.rec.model.impl.ResultSetSource;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;

/**
 * JDBI plugin module — zero Rhino dependency.
 * Public methods are automatically exposed to JS by Rhino's NativeJavaObject.
 */
public class JdbiModule implements RecPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbiModule.class);

    @Override
    public String name() {
        return "jdbi";
    }

    @Override
    public Object module() {
        return this;
    }

    public JdbiSource query(String url, String user, String password, String sql) {
        LOGGER.info("Created JDBI source for query: {}", sql);
        Jdbi jdbi = Jdbi.create(url, user, password);
        return new JdbiSource(jdbi, sql);
    }

    public ResultSetSource resultSet(ResultSet rs) {
        LOGGER.info("Created source from ResultSet #{}", rs.hashCode());
        return new ResultSetSource(rs);
    }
}
