package net.kimleo.rec.scripting.model;

import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy;
import net.kimleo.rec.repository.RecRepository;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

public class Rec extends ScriptableObject {
    @Override
    public String getClassName() {
        return "Rec";
    }

    private String basePath = "";
    private RecRepository repository = null;
    private Context context = null;

    public String jsGet_basePath() {
        return basePath;
    }

    public void jsConstructor(String basePath) {
        this.basePath = basePath;
        this.repository = DefaultLoadingStrategy.repo(basePath);
        this.context = Context.getCurrentContext();
    }

    public RecSet jsFunction_from(String name) {
        return new RecSet(repository.from(name), context, this);
    }

    public void jsFunction_rule(String name, Function fn) {
        jsFunction_from(name).verify(fn);
    }
}
