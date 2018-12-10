package net.kimleo.rec.v2.scripting.model;

@Deprecated
public class State {
    public Object state;

    public State(Object state) {
        this.state = state;
    }

    public Object get() { return state; }
    public State set(Object state) { this.state = state; return this;}

    public Object getValue() { return state; }

}
