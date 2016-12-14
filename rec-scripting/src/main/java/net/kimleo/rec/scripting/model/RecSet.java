package net.kimleo.rec.scripting.model;

import net.kimleo.rec.repository.RecordSet;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class RecSet {
    private final RecordSet set;
    private final Context context;
    private final Scriptable scope;

    public RecSet(RecordSet set, Context context, Scriptable scope) {
        this.set = set;
        this.context = context;
        this.scope = scope;
    }

    public RecSet where(Function fn) {
        return new RecSet(set.where(wrapper -> {
            Object array = fn.call(context, scope, null, new Object[]{wrapper});
            if (array instanceof Boolean) {
                return (Boolean) array;
            } else {
                return false;
            }
        }), context, scope);
    }

    public RecSet select(String[] attrs) {
        return new RecSet(set.select(attrs), context, scope);
    }

    public void verify(Function fn) {
        set.verify(wrapper -> {
            Object array = fn.call(context, scope, null, new Object[]{wrapper});
            if (array instanceof Boolean) {
                return (Boolean) array;
            } else {
                return false;
            }
        });
    }
}
