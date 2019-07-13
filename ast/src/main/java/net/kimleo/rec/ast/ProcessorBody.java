package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface ProcessorBody extends Node {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ProcessorClause implements ProcessorBody {
        ProcessingExpression from;
        ConditionExpression where;
        ConditionExpression skip;
        EmitClause emit;
    }

    @Data
    class MatchProcessor implements ProcessorBody {
        MatchingExpression matching;
    }
}
