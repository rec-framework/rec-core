package net.kimleo.rec.spi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpiModuleRegistry {

    private final Map<String, SpiModuleInstance> modules = new HashMap<>();

    public void registerModule(SpiModuleInstance module) {
        modules.put(module.getPackageName(), module);
    }

    public List<SpiDefinedOperator> openPackage(String packageName) {
        return modules.getOrDefault(packageName, new SpiModuleInstance())
                .getOperators();
    }
}
