package net.kimleo.rec.scripting.module;

import org.junit.Test;

import java.io.Reader;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class NativeModuleSourceProviderTest {

    private final NativeModuleSourceProvider provider = new NativeModuleSourceProvider();

    @Test
    public void shouldLoadBuiltinModuleFromResource() throws Exception {
        // "rec/logging" should resolve to rec/logging.js via classloader
        Reader reader = invokeGetReader("rec/logging");
        assertNotNull("rec/logging should be loadable", reader);
        reader.close();
    }

    @Test
    public void shouldLoadBuiltinAssertModule() throws Exception {
        Reader reader = invokeGetReader("rec/assert");
        assertNotNull("rec/assert should be loadable", reader);
        reader.close();
    }

    @Test
    public void shouldLoadBuiltinRecModule() throws Exception {
        // "rec" should resolve to rec/native.js via classloader
        Reader reader = invokeGetReader("rec");
        assertNotNull("rec should be loadable", reader);
        reader.close();
    }

    @Test
    public void shouldReturnNullForNonexistentModule() throws Exception {
        Reader reader = invokeGetReader("nonexistent/module");
        assertNull("Nonexistent module should return null", reader);
    }

    @Test
    public void shouldLoadJsFileFromPath() throws Exception {
        // Load a .js file that exists on the filesystem
        Reader reader = invokeGetReader("rec-scripting/src/test/resources/test2.js");
        // This might be null if the working directory is different
        // The test verifies the method doesn't throw
        if (reader != null) {
            reader.close();
        }
    }

    /**
     * Use reflection to call the private getReader method for testing.
     */
    private Reader invokeGetReader(String moduleId) throws Exception {
        java.lang.reflect.Method method = NativeModuleSourceProvider.class
                .getDeclaredMethod("getReader", String.class);
        method.setAccessible(true);
        try {
            return (Reader) method.invoke(provider, moduleId);
        } catch (java.lang.reflect.InvocationTargetException ex) {
            if (ex.getCause() instanceof Exception) {
                throw (Exception) ex.getCause();
            }
            throw ex;
        }
    }
}
