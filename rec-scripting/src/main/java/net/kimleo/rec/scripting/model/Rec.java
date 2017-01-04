package net.kimleo.rec.scripting.model;

import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy;
import net.kimleo.rec.record.Record;
import net.kimleo.rec.repository.RecRepository;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import java.nio.file.Paths;

public class Rec extends ScriptableObject {
    @Override
    public String getClassName() {
        return "Rec";
    }

    private String basePath = "";
    private RecRepository repository = null;
    private Context context = null;

    @JSGetter
    public String basePath() {
        return basePath;
    }

    public Rec() {
        this(".");
    }

    @JSConstructor
    public Rec(String basePath) {
        this.context = Context.getCurrentContext();

        String scriptPath = (String) context.getThreadLocal("SCRIPT_PATH");
        if (scriptPath != null) {
            basePath = Paths.get(scriptPath, basePath).toString();
        }
        this.basePath = basePath;

        this.repository = DefaultLoadingStrategy.repo(basePath);
    }
    @JSFunction
    public RecSet from(String name) {
        return new RecSet(repository.from(name), context, this);
    }

    @JSFunction
    public void rule(String name, Function fn) {
        from(name).verify(fn);
    }
}
