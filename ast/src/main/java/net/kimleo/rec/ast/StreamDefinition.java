package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamDefinition implements Node {
    String name;
    InvokeExpression invoke;
}
