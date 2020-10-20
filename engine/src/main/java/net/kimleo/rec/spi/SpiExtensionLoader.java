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

        List<SpiModuleInstance> instances = modules.stream().map(module -> {
            SpiModuleInstance instance = new SpiModuleInstance();
            instance.setModule(module);

            String annotatedPackage = module.getClass().getAnnotation(Package.class).value();

            if (isNullOrEmpty(annotatedPackage)) {
                annotatedPackage = module.getClass().getPackage().getName();
            }
            instance.setPackageName(annotatedPackage);

            List<SpiDefinedOperator> operators = Arrays.stream(module.getClass().getDeclaredMethods())
                    .filter(method -> method.isAnnotationPresent(Operator.class))
                    .map(method -> {
                        SpiDefinedOperator operator = new SpiDefinedOperator();

                        operator.setModule(module);
                        operator.setMethod(method);
                        String annotatedMethodName = method.getAnnotation(Operator.class).value();
                        if (annotatedMethodName.isEmpty()) {
                            annotatedMethodName = method.getName();
                        }
                        operator.setOperatorName(annotatedMethodName);
                        operator.setPackageName(instance.getPackageName());

                        return operator;
                    })
                    .collect(Collectors.toList());

            instance.setOperators(operators);

            return instance;
        }).collect(Collectors.toList());

        return instances;
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }
}
