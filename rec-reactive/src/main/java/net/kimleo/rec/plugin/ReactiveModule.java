package net.kimleo.rec.plugin;

import net.kimleo.rec.model.impl.ReactiveTee;

/**
 * Reactive plugin module — zero Rhino dependency.
 */
public class ReactiveModule implements RecPlugin {
    @Override
    public String name() {
        return "reactive";
    }

    @Override
    public Object module() {
        return this;
    }

    public ReactiveTee reactive() {
        return new ReactiveTee(null);
    }
}
