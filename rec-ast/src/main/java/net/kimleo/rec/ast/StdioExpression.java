package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public
class StdioExpression implements ProcessingExpression {

    public enum StdioType {

        STDIN("stdin"),
        STDOUT("stdout"),
        STDERR("stderr");

        private final String repr;

        StdioType(String repr) {

            this.repr = repr;
        }

        public String repr() {
            return this.repr;
        }

        public static StdioType of(String type) {
            return Arrays.stream(values())
                    .filter(elem -> Objects.equals(elem.repr(), type))
                    .findAny().orElse(null);
        }

        public boolean contains(String type) {
            return of(type) != null;
        }
    }

    StdioType type;

    @Override
    public String repr() {
        return type.repr();
    }
}
