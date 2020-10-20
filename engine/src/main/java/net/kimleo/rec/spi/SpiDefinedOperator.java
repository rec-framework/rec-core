package net.kimleo.rec.spi;

import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

@Data
public class SpiDefinedOperator {
    String packageName;
    String operatorName;

    RecModule module;
    Method method;

    String repr() {
        return packageName + "#" + operatorName;
    }

    @SneakyThrows
    Object invoke(Object[] args) {
        return method.invoke(module, args);
    }
}
