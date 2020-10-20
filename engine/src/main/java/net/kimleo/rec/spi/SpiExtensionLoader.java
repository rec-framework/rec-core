package net.kimleo.rec.spi;

import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpiExtensionLoader {

    List<SpiModuleInstance> modules() {
        Iterable<RecModule> iter = () -> ServiceLoader.load(RecModule.class)
                .iterator();

        List<RecModule> modules = StreamSupport.stream(iter.spliterator(), false).collect(Collectors.toList());

        return modules.stream()
                .map(this::loadModule)
                .collect(Collectors.toList());
    }

    private SpiModuleInstance loadModule(RecModule module) {
        SpiModuleInstance instance = new SpiModuleInstance();
        instance.setModule(module);

        Class<? extends RecModule> moduleClass = module.getClass();
        instance.setPackageName(moduleClass.getPackage().getName());

        if (moduleClass.isAnnotationPresent(Package.class)) {
            String aliasPackageName = moduleClass.getAnnotation(Package.class).value();
            if (!aliasPackageName.equals("")) {
                instance.setPackageName(aliasPackageName);
            }
        }

        List<SpiDefinedOperator> operators = Arrays.stream(moduleClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Operator.class))
                .map(method -> loadOperator(module, instance, method))
                .collect(Collectors.toList());

        instance.setOperators(operators);

        return instance;
    }

    private SpiDefinedOperator loadOperator(RecModule module, SpiModuleInstance instance, java.lang.reflect.Method method) {
        SpiDefinedOperator operator = new SpiDefinedOperator();

        operator.setModule(module);
        operator.setMethod(method);
        String aliasOperatorName = method.getAnnotation(Operator.class).value();
        if (!aliasOperatorName.isEmpty()) {
            operator.setOperatorName(aliasOperatorName);
        } else {
            operator.setOperatorName(method.getName());
        }
        operator.setPackageName(instance.getPackageName());

        return operator;
    }

}
