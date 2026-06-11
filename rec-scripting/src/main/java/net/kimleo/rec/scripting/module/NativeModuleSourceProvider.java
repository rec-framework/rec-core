package net.kimleo.rec.scripting.module;

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
            Reader reader = getReader(uri.toString());
            if (reader == null) {
                return null;
            }
            return new ModuleSource(reader, null, uri, base, validator);
        } catch (Exception ex) {
            LOGGER.warn(format("Loading js module [%s] failed.", uri.toString()));
            return null;
        }
    }

    @Override
    protected ModuleSource loadFromFallbackLocations(String moduleId, Object validator)
            throws IOException, URISyntaxException {
        LOGGER.info(format("Loading js module from fallback location %s", moduleId));
        Reader reader = getReader(moduleId);
        if (reader == null) {
            LOGGER.error(format("Module [%s] not found in any location", moduleId));
            return null;
        }
        return new ModuleSource(reader, null, new URI(moduleId), new URI("./"), validator);
    }

    private Reader getReader(String moduleId) throws IOException {
        if (moduleId.endsWith(".js")) {
            return resolveJsFile(moduleId);
        }
        if (isRelativePath(moduleId)) {
            return resolveRelativeModule(moduleId);
        }
        return resolveBareModule(moduleId);
    }

    private Reader resolveJsFile(String moduleId) throws IOException {
        if (Files.exists(Paths.get(moduleId)) && Files.isRegularFile(Paths.get(moduleId))) {
            return Files.newBufferedReader(Paths.get(moduleId));
        }
        return loadFromResource(moduleId);
    }

    private Reader resolveRelativeModule(String moduleId) throws IOException {
        if (Files.exists(Paths.get(moduleId + ".js"))
                && Files.isRegularFile(Paths.get(moduleId + ".js"))) {
            return Files.newBufferedReader(Paths.get(moduleId + ".js"));
        }
        if (Files.exists(Paths.get(moduleId)) && Files.isDirectory(Paths.get(moduleId))) {
            return Files.newBufferedReader(Paths.get(moduleId, "index.js"));
        }
        return null;
    }

    private Reader resolveBareModule(String moduleId) throws IOException {
        Reader resource = loadFromResource(moduleId + ".js");
        if (resource != null) {
            return resource;
        }
        if (Files.exists(Paths.get(moduleId + ".js"))
                && Files.isRegularFile(Paths.get(moduleId + ".js"))) {
            return Files.newBufferedReader(Paths.get(moduleId + ".js"));
        }
        return null;
    }

    private boolean isRelativePath(String moduleId) {
        return moduleId.startsWith("./") || moduleId.startsWith("/") || moduleId.startsWith("../");
    }

    private Reader loadFromResource(String moduleId) {
        try {
            ClassLoader cl = this.getClass().getClassLoader();
            java.io.InputStream is = cl.getResourceAsStream(moduleId);
            if (is != null) {
                return new InputStreamReader(is);
            }
            String nativePath = moduleId.endsWith(".js")
                    ? moduleId.replace(".js", "/native.js")
                    : moduleId + "/native.js";
            is = cl.getResourceAsStream(nativePath);
            if (is != null) {
                return new InputStreamReader(is);
            }
            String indexPath = moduleId.endsWith(".js")
                    ? moduleId.replace(".js", "/index.js")
                    : moduleId + "/index.js";
            is = cl.getResourceAsStream(indexPath);
            if (is != null) {
                return new InputStreamReader(is);
            }
        } catch (Exception ex) {
            LOGGER.error(format("Loading resource module [%s] failed, error: %s", moduleId, ex));
        }
        return null;
    }
}
