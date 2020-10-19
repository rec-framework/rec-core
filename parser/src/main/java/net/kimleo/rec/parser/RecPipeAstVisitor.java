package net.kimleo.rec.parser;

import net.kimleo.rec.ast.Node;

public class RecPipeAstVisitor extends RecPipeBaseVisitor<Node> {
    @Override
    public Node visitStart(RecPipeParser.StartContext ctx) {
        return super.visitStart(ctx);
    }

    @Override
    public Node visitScript(RecPipeParser.ScriptContext ctx) {
        return super.visitScript(ctx);
    }

    @Override
    public Node visitImportDecl(RecPipeParser.ImportDeclContext ctx) {
        return super.visitImportDecl(ctx);
    }

    @Override
    public Node visitPackageName(RecPipeParser.PackageNameContext ctx) {
        return super.visitPackageName(ctx);
    }

    @Override
    public Node visitProcess(RecPipeParser.ProcessContext ctx) {
        return super.visitProcess(ctx);
    }

    @Override
    public Node visitSource(RecPipeParser.SourceContext ctx) {
        return super.visitSource(ctx);
    }

    @Override
    public Node visitProcessing(RecPipeParser.ProcessingContext ctx) {
        return super.visitProcessing(ctx);
    }

    @Override
    public Node visitSimpleProc(RecPipeParser.SimpleProcContext ctx) {
        return super.visitSimpleProc(ctx);
    }

    @Override
    public Node visitParallelProc(RecPipeParser.ParallelProcContext ctx) {
        return super.visitParallelProc(ctx);
    }

    @Override
    public Node visitStream(RecPipeParser.StreamContext ctx) {
        return super.visitStream(ctx);
    }

    @Override
    public Node visitInvokeExpression(RecPipeParser.InvokeExpressionContext ctx) {
        return super.visitInvokeExpression(ctx);
    }

    @Override
    public Node visitValue(RecPipeParser.ValueContext ctx) {
        return super.visitValue(ctx);
    }
}
