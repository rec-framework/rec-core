package net.kimleo.rec.spi;

import org.junit.Test;

import java.util.List;

public class SpiExtensionLoaderTest {
    @Test
    public void spiLoader() {
        SpiExtensionLoader loader = new SpiExtensionLoader();
        SpiModuleRegistry registry = new SpiModuleRegistry();


        List<SpiModuleInstance> modules = loader.modules();

        modules.forEach(registry::registerModule);

        List<SpiDefinedOperator> operators = registry.openPackage("net.kimleo.rec.spi");

        operators.forEach(op -> {
            op.invoke(new Object[]{});
        });
    }
}
