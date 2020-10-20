package net.kimleo.rec.parser;

import net.kimleo.rec.ast.*;

import java.util.List;
import java.util.stream.Collectors;

public class RecPipeAstVisitor extends RecPipeBaseVisitor<Node> {
    @Override
    public Node visitStart(RecPipeParser.StartContext ctx) {
        return super.visitStart(ctx);
    }

    @Override
    public Node visitScript(RecPipeParser.ScriptContext ctx) {
        List<ImportDeclaration> imports = ctx.importDecl().stream()
                .map(it -> (ImportDeclaration) it.accept(this))
                .collect(Collectors.toList());

        List<ProcessStatement> processes = ctx.process().stream()
                .map(it -> (ProcessStatement) it.accept(this))
                .collect(Collectors.toList());

        return new Script(imports, processes);
    }

    @Override
    public Node visitImportDecl(RecPipeParser.ImportDeclContext ctx) {
        String packageName = ((Value.ID) ctx.packageName().accept(this)).getName();


        return new ImportDeclaration(packageName);
    }

    @Override
    public Node visitPackageName(RecPipeParser.PackageNameContext ctx) {
        String packageName = ctx.ID().stream().map(id -> id.getSymbol().getText())
                .collect(Collectors.joining("."));

        return new Value.ID(packageName);
    }

    @Override
    public Node visitProcess(RecPipeParser.ProcessContext ctx) {

        List<ProcessingExpression> processing = ctx.processing().stream()
                .map(proc -> ((ProcessingExpression) proc.accept(this)))
                .collect(Collectors.toList());

        return new ProcessStatement(processing);
    }

    @Override
    public Node visitProcessing(RecPipeParser.ProcessingContext ctx) {
        if (ctx.simpleProc() != null) {
            return ctx.simpleProc().accept(this);
        }
        if (ctx.invokeExpression() != null) {
            return ctx.invokeExpression().accept(this);
        }
        if (ctx.parallelProc() != null) {
            return ctx.parallelProc().accept(this);
        }
        return new ProcessingExpression.EmptyProcessingExpression();
    }

    @Override
    public Node visitSimpleProc(RecPipeParser.SimpleProcContext ctx) {
        if (ctx.STDOUT() != null || ctx.STDERR() != null || ctx.STDIN() != null) {
            return new StdioExpression(StdioExpression.StdioType.of(ctx.getText()));
        }
        if (ctx.stream() != null) {
            String name = ((Value.ID) ctx.stream().accept(this)).getName();

            return new ProcessingExpression.SimpleProcessing(name);
        }

        return new ProcessingExpression.EmptyProcessingExpression();
    }

    @Override
    public Node visitParallelProc(RecPipeParser.ParallelProcContext ctx) {
        List<ProcessingExpression> processing = ctx.processing().stream()
                .map(proc -> ((ProcessingExpression) proc.accept(this)))
                .collect(Collectors.toList());

        return new ProcessingExpression.ParallelProcessing(processing);
    }

    @Override
    public Node visitStream(RecPipeParser.StreamContext ctx) {
        return new Value.ID(ctx.ID().getSymbol().getText());
    }

    @Override
    public Node visitInvokeExpression(RecPipeParser.InvokeExpressionContext ctx) {
        String name = ctx.ID().getSymbol().getText();

        List<Value> values = ctx.value().stream().map(it -> ((Value) it.accept(this))).collect(Collectors.toList());

        return new InvokeExpression(name, values);
    }

    @Override
    public Node visitValue(RecPipeParser.ValueContext ctx) {
        if (ctx.NUM_VALUE() != null) {
            return new Value.Number(Double.parseDouble(ctx.getText()));
        }
        if (ctx.STRING() != null) {
            return new Value.StringValue(ctx.getText());
        }
        if (ctx.ID() != null) {
            return new Value.ID(ctx.getText());
        }
        return super.visitValue(ctx);
    }
}
