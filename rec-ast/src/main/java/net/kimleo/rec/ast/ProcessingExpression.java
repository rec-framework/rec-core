package net.kimleo.rec.ast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public interface ProcessingExpression extends Node {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SimpleProcessing implements ProcessingExpression {
        String name;

        @Override
        public String repr() {
            return name;
        }
    }

    class EmptyProcessingExpression implements ProcessingExpression {
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ParallelProcessing implements ProcessingExpression {
        List<ProcessingExpression> processing;

        @Override
        public String repr() {
            return "(" + processing.stream().
                    map(Node::repr)
                    .collect(Collectors.joining(",")) + ")";
        }
    }
}
