package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface Value extends Expression {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ID implements Value {
        String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Number implements Value {
        double value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class StringValue implements Value {
        String value;
    }
}
