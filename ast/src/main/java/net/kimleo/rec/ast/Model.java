package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Model implements Node {

    List<ImportDeclaration> imports;
    List<StreamDefinition> streams;
    List<ProcessorDefinition> processors;
    List<ProcessStatement> process;
}
