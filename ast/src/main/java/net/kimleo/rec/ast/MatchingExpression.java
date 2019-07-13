package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingExpression implements Node {

    List<CaseCondition> cases;
    EmitClause onMismatch;
}
