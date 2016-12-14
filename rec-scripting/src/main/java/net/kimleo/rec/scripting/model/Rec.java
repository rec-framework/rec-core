package net.kimleo.rec.scripting.model;

import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy;
import net.kimleo.rec.repository.RecRepository;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptableObject;

/*
class Rec: ScriptableObject() {
    override fun getClassName(): String = "Rec"
    var basePath = ""
    var repository: RecRepository? = null

    var context: Context? = null

    fun jsGet_basePath(): String {
        return basePath
    }

    fun jsConstructor(basePath: String) {
        this.basePath = basePath
        repository = DefaultLoadingStrategy.repo(basePath)
        context = Context.getCurrentContext()
    }

    fun jsFunction_from(name: String): RecSet {
        return RecSet(repository!!.from(name), context!!, this)
    }

    fun jsFunction_rule(name: String, fn: Function) {
        val set = RecSet(repository!!.from(name), context!!, this)
        set.verify(fn)
    }
}
 */
public class Rec extends ScriptableObject {
    @Override
    public String getClassName() {
        return "Rec";
    }

    String basePath = "";
    RecRepository repository = null;
    Context context = null;

    public String jsGet_basePath() {
        return basePath;
    }

    public void jsConstructor(String basePath) {
        this.basePath = basePath;
        this.repository = DefaultLoadingStrategy.Companion.repo(basePath);
        this.context = Context.getCurrentContext();
    }

    public RecSet jsFunction_from(String name) {
        return new RecSet(repository.from(name), context, this);
    }

    public void jsFunction_rule(String name, Function fn) {
        jsFunction_from(name).verify(fn);
    }
}
