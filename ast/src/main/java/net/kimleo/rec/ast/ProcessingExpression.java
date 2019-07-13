package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public interface ProcessingExpression extends Node {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SimpleProcessing implements ProcessingExpression {
        String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class StdoutProcessing implements ProcessingExpression {
        String name;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class StderrProcessing implements ProcessingExpression {
        String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ParallelProcessing implements ProcessingExpression {
        List<ProcessingExpression> processing;
    }
}
