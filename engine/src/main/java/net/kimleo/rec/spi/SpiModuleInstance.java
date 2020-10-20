package net.kimleo.rec.spi;

import lombok.Data;

import java.util.List;

@Data
public class SpiModuleInstance {
    String packageName;
    RecModule module;
    List<SpiDefinedOperator> operators;
}
