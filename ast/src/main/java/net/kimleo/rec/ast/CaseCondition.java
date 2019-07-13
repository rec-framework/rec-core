package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCondition implements Node{
    Value condition;
    EmitClause emit;
    boolean skip = false;
}
