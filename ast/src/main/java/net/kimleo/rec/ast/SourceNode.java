package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SourceNode implements Node {
    String name;
    boolean stdin = false;
}
