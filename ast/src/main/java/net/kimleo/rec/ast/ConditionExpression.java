package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConditionExpression implements Node {
    Relation relation;

    SelectorExpression left;

    Expression right;
}
