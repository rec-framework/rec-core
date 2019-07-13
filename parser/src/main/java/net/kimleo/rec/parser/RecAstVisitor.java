package net.kimleo.rec.parser;

import net.kimleo.rec.ast.CaseCondition;
import net.kimleo.rec.ast.ConditionExpression;
import net.kimleo.rec.ast.EmitClause;
import net.kimleo.rec.ast.Expression;
import net.kimleo.rec.ast.ImportDeclaration;
import net.kimleo.rec.ast.InvokeExpression;
import net.kimleo.rec.ast.MatchingExpression;
import net.kimleo.rec.ast.Model;
import net.kimleo.rec.ast.Node;
import net.kimleo.rec.ast.ProcessStatement;
import net.kimleo.rec.ast.ProcessingExpression;
import net.kimleo.rec.ast.ProcessorBody;
import net.kimleo.rec.ast.ProcessorDefinition;
import net.kimleo.rec.ast.Relation;
import net.kimleo.rec.ast.SelectorExpression;
import net.kimleo.rec.ast.SourceNode;
import net.kimleo.rec.ast.StreamDefinition;
import net.kimleo.rec.ast.Value;
import org.antlr.v4.runtime.RuleContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RecAstVisitor extends RecBaseVisitor<Node> {
    @Override
    public Node visitStart(RecParser.StartContext ctx) {
        return ctx.model().accept(this);
    }

    @Override
    public Node visitModel(RecParser.ModelContext ctx) {
        List<ImportDeclaration> imports =  ctx.importDecl().stream()
                .map(it -> (ImportDeclaration) it.accept(this))
                .collect(Collectors.toList());

        List<StreamDefinition> streams = ctx.streamDef().stream()
                .map(it -> (StreamDefinition) it.accept(this))
                .collect(Collectors.toList());

        List<ProcessorDefinition> processors = ctx.processorDef().stream()
                .map(it -> (ProcessorDefinition) it.accept(this))
                .collect(Collectors.toList());

        List<ProcessStatement> process = ctx.process().stream()
                .map(it -> (ProcessStatement) it.accept(this))
                .collect(Collectors.toList());


        return new Model(imports, streams, processors, process);
    }

    @Override
    public Node visitStreamDef(RecParser.StreamDefContext ctx) {

        String name = ctx.name.getText();

        InvokeExpression invoke = (InvokeExpression) ctx.streamContent().accept(this);

        return new StreamDefinition(name, invoke);
    }

    @Override
    public Node visitStreamContent(RecParser.StreamContentContext ctx) {
        return ctx.invokeExpression().accept(this);
    }

    @Override
    public Node visitProcessorDef(RecParser.ProcessorDefContext ctx) {

        String name = ctx.name.getText();

        List<String> params = ctx.params().ID().stream()
                .map(it -> it.getSymbol().getText())
                .collect(Collectors.toList());

        ProcessorBody body = (ProcessorBody) ctx.processorBody().accept(this);

        return new ProcessorDefinition(name, params, body);
    }

    @Override
    public Node visitProcessorBody(RecParser.ProcessorBodyContext ctx) {
        return ctx.processorClause().accept(this);
    }

    @Override
    public Node visitProcessorClause(RecParser.ProcessorClauseContext ctx) {
        if (ctx.matching() != null) {
            return ctx.matching().accept(this);
        } else {
            ProcessingExpression from = (ProcessingExpression) nullableNode(ctx.fromClause());
            ConditionExpression where = (ConditionExpression) nullableNode(ctx.whereClause());
            ConditionExpression skip = (ConditionExpression)  nullableNode(ctx.skipClause());
            EmitClause emit = (EmitClause)  nullableNode(ctx.emitClause());

            return new ProcessorBody.ProcessorClause(from, where, skip, emit);
        }
    }

    private Node nullableNode(RuleContext value) {
        return Optional.ofNullable(value)
                .map(it -> it.accept(this)).orElse(null);
    }

    @Override
    public Node visitFromClause(RecParser.FromClauseContext ctx) {
        return ctx.processing().accept(this);
    }

    @Override
    public Node visitWhereClause(RecParser.WhereClauseContext ctx) {
        return ctx.condition().accept(this);
    }

    @Override
    public Node visitSkipClause(RecParser.SkipClauseContext ctx) {
        return ctx.condition().accept(this);
    }

    @Override
    public Node visitEmitClause(RecParser.EmitClauseContext ctx) {

        List<SelectorExpression> selectors = ctx.selector().stream()
                .map(it -> (SelectorExpression) it.accept(this))
                .collect(Collectors.toList());

        return new EmitClause(selectors);
    }

    @Override
    public Node visitMatching(RecParser.MatchingContext ctx) {

        List<CaseCondition> cases = ctx.caseClause().stream()
                .map(it -> (CaseCondition) it.accept(this))
                .collect(Collectors.toList());

        EmitClause onMismatch = (EmitClause) ctx.matchElseClause().accept(this);

        return new MatchingExpression(cases, onMismatch);
    }

    @Override
    public Node visitCaseClause(RecParser.CaseClauseContext ctx) {
        Value value = (Value) ctx.value().accept(this);

        EmitClause emit = (EmitClause) ctx.emitClause().accept(this);

        boolean skip = ctx.SKIP_() != null;

        return new CaseCondition(value, emit, skip);
    }

    @Override
    public Node visitMatchElseClause(RecParser.MatchElseClauseContext ctx) {
        return ctx.emitClause().accept(this);
    }

    @Override
    public Node visitCondition(RecParser.ConditionContext ctx) {

        SelectorExpression left = (SelectorExpression) ctx.selector(0).accept(this);
        Relation relation = (Relation) ctx.relation().accept(this);

        Expression right;
        if (ctx.selector().size() > 1) {
            right = ((SelectorExpression) ctx.selector(1).accept(this));
        } else {
            right = ((Value) ctx.value().accept(this));
        }

        return new ConditionExpression(relation, left, right);
    }

    @Override
    public Node visitRelation(RecParser.RelationContext ctx) {
        return Relation.of(ctx.getText());
    }

    @Override
    public Node visitSelector(RecParser.SelectorContext ctx) {
        SelectorExpression selector = new SelectorExpression();
        if (ctx.IT() != null) {
            selector.setIt(true);
            selector.setName("it");
        } else if (ctx.ID() != null) {
            selector.setName(ctx.ID().getText());
        }

        if (ctx.value() != null) {
            selector.setValue((Value) ctx.value().accept(this));
        }

        return selector;
    }

    @Override
    public Node visitImportDecl(RecParser.ImportDeclContext ctx) {

        return new ImportDeclaration(ctx.packageName().getText());
    }

    @Override
    public Node visitProcess(RecParser.ProcessContext ctx) {
        SourceNode source = (SourceNode) ctx.source().accept(this);

        List<ProcessingExpression> processing = ctx.processing().stream()
                .map(it -> (ProcessingExpression) it.accept(this))
                .collect(Collectors.toList());

        return new ProcessStatement(source, processing);
    }

    @Override
    public Node visitSource(RecParser.SourceContext ctx) {

        return new SourceNode(ctx.getText(), ctx.STDIN() != null);
    }

    @Override
    public Node visitProcessing(RecParser.ProcessingContext ctx) {

        if (ctx.simpleProc() != null) {
            return ctx.simpleProc().accept(this);
        } else if (ctx.parallelProc() != null) {
            return ctx.parallelProc().accept(this);
        }

        return ctx.invokeExpression().accept(this);
    }

    @Override
    public Node visitSimpleProc(RecParser.SimpleProcContext ctx) {

        if (ctx.STDOUT() != null) {
            return new ProcessingExpression.StdoutProcessing(ctx.getText());
        } else if (ctx.STDERR() != null) {
            return new ProcessingExpression.StderrProcessing(ctx.getText());
        }
        return new ProcessingExpression.SimpleProcessing(ctx.getText());
    }

    @Override
    public Node visitParallelProc(RecParser.ParallelProcContext ctx) {

        List<ProcessingExpression> processing = ctx.simpleProc().stream()
                .map(it -> (ProcessingExpression) it.accept(this))
                .collect(Collectors.toList());

        return new ProcessingExpression.ParallelProcessing(processing);
    }

    @Override
    public Node visitInvokeExpression(RecParser.InvokeExpressionContext ctx) {
        String callsite = ctx.ID().getText();

        List<Value> arguments = ctx.value().stream()
                .map(it -> (Value) it.accept(this))
                .collect(Collectors.toList());


        return new InvokeExpression(callsite, arguments);
    }

    @Override
    public Node visitValue(RecParser.ValueContext ctx) {

        if (ctx.ID() != null) {
            return new Value.ID(ctx.getText());
        } else if (ctx.NUM_VALUE() != null) {
            return new Value.Number(Double.valueOf(ctx.getText()));
        }

        return new Value.StringValue(ctx.getText());
    }
}
