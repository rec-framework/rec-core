package net.kimleo.rec.scripting

import net.kimleo.rec.loader.strategy.DefaultLoadingStrategy
import net.kimleo.rec.repository.RecRepository
import net.kimleo.rec.repository.RecordSet
import org.mozilla.javascript.Context
import org.mozilla.javascript.Function
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject

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


class RecSet(val set: RecordSet, val context: Context, val scope: Scriptable){
    val name = set.config.name

    fun where(fn: Function): RecSet {
        return RecSet(set.where( {
            val result = fn.call(context, scope, null, arrayOf(it))
            if (result is Boolean) result else false
        }), context, scope)
    }

    fun select(attrs: Array<String>): RecSet {
        return RecSet(set.select(attrs.asList()), context, scope)
    }

    fun verify(fn: Function) {
        set.verify({
            val result = fn.call(context, scope, null, arrayOf(it))
            if (result is Boolean) result else false
        })
    }


}