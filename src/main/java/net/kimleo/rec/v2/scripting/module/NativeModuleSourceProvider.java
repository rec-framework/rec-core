package net.kimleo.rec.v2.scripting.module;

import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.lang.String.format;

public class NativeModuleSourceProvider extends ModuleSourceProviderBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeModuleSourceProvider.class);

    @Override
    protected ModuleSource loadFromUri(URI uri, URI base, Object validator)
            throws IOException, URISyntaxException {
        try {
            LOGGER.info(format("Loading js module [%s]", uri.toString()));
            return new ModuleSource(getReader(uri.toString()), null, uri, base, validator);
        } catch (Exception ex) {
            LOGGER.warn(format("Loading js module [%s] failed.", uri.toString()));
            return null;
        }
    }

    @Override
    protected ModuleSource loadFromFallbackLocations(String moduleId, Object validator)
            throws IOException, URISyntaxException {
        LOGGER.info(String.format("Loading js module from fallback location %s", moduleId));
        return new ModuleSource(getReader(moduleId),
                null,
                new URI(moduleId),
                new URI("./"), validator);
    }

    private Reader getReader(String moduleId) throws IOException {
        if (moduleId.endsWith(".js")) {
            try {
                return Files.newBufferedReader(Paths.get(moduleId));
            } catch (IOException ingore) {
                LOGGER.warn(format("Loading user defined js module [%s] failed, trying resource instead",
                        moduleId));
                return loadFromResource(moduleId);

            }
        } else if (Files.exists(Paths.get(moduleId + ".js"))
                && !Files.isDirectory(Paths.get(moduleId + ".js"))) {
            return Files.newBufferedReader(Paths.get(moduleId + ".js"));
        } else if (Files.exists(Paths.get(moduleId))
                && Files.isDirectory(Paths.get(moduleId))) {
            return Files.newBufferedReader(Paths.get(moduleId, "index.js"));
        } else {
            LOGGER.warn(
                    format("Loading user defined js module [%s] failed, trying resource instead", moduleId));
            return loadFromResource(moduleId + ".js");
        }
    }

    private Reader loadFromResource(String moduleId) {
        try {
            return new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(moduleId));
        } catch (Exception ex) {
            LOGGER.error(format("Loading resource module [%s] failed, error: %s", moduleId, ex));
        }
        return null;
    }
}
