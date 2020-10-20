package net.kimleo.rec.spi;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SpiExtensionLoaderTest {
    @Test
    public void spiLoader() {
        SpiExtensionLoader loader = new SpiExtensionLoader();
        SpiModuleRegistry registry = new SpiModuleRegistry();


        List<SpiModuleInstance> modules = loader.modules();

        assertThat(modules.size(), is(1));

        modules.forEach(registry::registerModule);

        List<SpiDefinedOperator> operators = registry.openPackage("net.kimleo.rec.spi");

        assertThat(operators.size(), is(1));

        operators.forEach(op -> {
            assertThat(op.getOperatorName(), is("the-operator-name"));
            op.invoke(new Object[]{});
        });
    }
}
