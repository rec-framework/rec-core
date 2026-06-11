package net.kimleo.rec.scripting.model;

import net.kimleo.rec.data.DataSet;
import net.kimleo.rec.data.Schema;
import org.mozilla.javascript.Scriptable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Wraps a DataSet for Rhino JS interop.
 * Fields are accessible as JS object properties via getString().
 */
public class JSRecord implements Scriptable, DataSet {

    private final DataSet delegate;
    Scriptable parent = this;

    public JSRecord(DataSet delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getClassName() {
        return "JSRecord";
    }

    @Override
    public Object get(String name, Scriptable start) {
        if (!delegate.hasField(name)) return null;
        return delegate.getString(name);
    }

    @Override
    public Object get(int index, Scriptable start) {
        if (index < 0 || index >= delegate.columnCount()) return null;
        return delegate.getString(index);
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return delegate.hasField(name);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return index >= 0 && index < delegate.columnCount();
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        throw new UnsupportedOperationException("JSRecord is immutable");
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        throw new UnsupportedOperationException("JSRecord is immutable");
    }

    @Override
    public void delete(String name) {
        throw new UnsupportedOperationException("JSRecord is immutable");
    }

    @Override
    public void delete(int index) {
        throw new UnsupportedOperationException("JSRecord is immutable");
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
        return delegate.fieldNames().toArray();
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        return null;
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return false;
    }

    // ---- DataSet delegation ----

    @Override
    public Schema schema() { return delegate.schema(); }

    @Override
    public boolean isNull(int col) { return delegate.isNull(col); }

    @Override
    public boolean isNull(String field) { return delegate.isNull(field); }

    @Override
    public String getString(int col) { return delegate.getString(col); }

    @Override
    public int getInt(int col) { return delegate.getInt(col); }

    @Override
    public long getLong(int col) { return delegate.getLong(col); }

    @Override
    public float getFloat(int col) { return delegate.getFloat(col); }

    @Override
    public double getDouble(int col) { return delegate.getDouble(col); }

    @Override
    public boolean getBoolean(int col) { return delegate.getBoolean(col); }

    @Override
    public byte[] getBytes(int col) { return delegate.getBytes(col); }

    @Override
    public BigDecimal getDecimal(int col) { return delegate.getDecimal(col); }

    @Override
    public LocalDate getDate(int col) { return delegate.getDate(col); }

    @Override
    public LocalDateTime getTimestamp(int col) { return delegate.getTimestamp(col); }

    @Override
    public Object get(int col) { return delegate.get(col); }

    @Override
    public List<String> fieldNames() { return delegate.fieldNames(); }
}
