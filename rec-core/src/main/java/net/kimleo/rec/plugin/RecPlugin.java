package net.kimleo.rec.plugin;

/**
 * Plugin interface for extending the Rec scripting API.
 * Plugins live in rec-core with zero Rhino dependency.
 * They expose a named Java module; rec-scripting handles JS bridging.
 */
public interface RecPlugin {
    /** Name used to register this plugin's module in the JS scope. */
    String name();

    /** The Java object whose public methods become JS functions. */
    Object module();
}
