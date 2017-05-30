package net.kimleo.rec.v2.scripting.module;

import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProviderBase;

import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RecModuleSourceProvider extends ModuleSourceProviderBase {
    @Override
    protected ModuleSource loadFromUri(URI uri, URI base, Object validator) throws IOException, URISyntaxException {
        try {
            return new ModuleSource(getReader(uri.toString()), null, uri, base, validator);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected ModuleSource loadFromFallbackLocations(String moduleId, Object validator) throws IOException, URISyntaxException {
        return new ModuleSource(getReader(moduleId), null, new URI(moduleId), new URI("./"), validator);

    }

    private Reader getReader(String moduleId) throws IOException {
        Reader reader = null;
        if (moduleId.endsWith(".js"))
            reader = Files.newBufferedReader(Paths.get(moduleId));
        else if (Files.exists(Paths.get(moduleId + ".js")) && !Files.isDirectory(Paths.get(moduleId + ".js"))) {
            reader = Files.newBufferedReader(Paths.get(moduleId + ".js"));
        } else if (Files.exists(Paths.get(moduleId)) && Files.isDirectory(Paths.get(moduleId))) {
            reader = Files.newBufferedReader(Paths.get(moduleId, "index.js"));
        }
        return reader;
    }
}
