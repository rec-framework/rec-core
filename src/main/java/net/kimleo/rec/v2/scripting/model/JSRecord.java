package net.kimleo.rec.v2.scripting.model;

import net.kimleo.rec.common.concept.Mapped;
import org.mozilla.javascript.Scriptable;

import java.util.List;

@Deprecated
public class JSRecord implements Scriptable, Mapped<String> {

    private final Mapped<String> record;
    Scriptable parent = this;

    public JSRecord(Mapped<String> record) {
        this.record = record;
    }

    @Override
    public String getClassName() {
        return "JSRecord";
    }

    @Override
    public Object get(String name, Scriptable start) {
        return has(name, start) ? record.get(name) : null;
    }

    @Override
    public Object get(int index, Scriptable start) {
        return null;
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return record.keys().contains(name);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return false;
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Scriptable getPrototype() {
        return parent;
    }

    @Override
    public void setPrototype(Scriptable prototype) {
        parent = prototype;
    }

    @Override
    public Scriptable getParentScope() {
        return parent;
    }

    @Override
    public void setParentScope(Scriptable parent) {
        this.parent = parent;
    }

    @Override
    public Object[] getIds() {
        return record.keys().toArray();
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        return null;
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return false;
    }

    @Override
    public String get(String field) {
        return record.get(field);
    }

    @Override
    public List<String> keys() {
        return record.keys();
    }
}
