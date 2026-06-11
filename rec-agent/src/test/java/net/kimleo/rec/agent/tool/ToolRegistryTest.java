package net.kimleo.rec.agent.tool;

import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class ToolRegistryTest {

    private Tool dummyTool(String name) {
        return new Tool() {
            @Override public String name() { return name; }
            @Override public String description() { return "A " + name + " tool"; }
            @Override public Map<String, Object> parametersSchema() {
                return Map.of("type", "object", "properties", Map.of());
            }
            @Override public String execute(Map<String, Object> arguments) { return name; }
        };
    }

    @Test
    public void testRegisterAndGet() {
        ToolRegistry registry = new ToolRegistry();
        Tool tool = dummyTool("Test");
        registry.register(tool);
        assertSame(tool, registry.get("Test"));
        assertTrue(registry.has("Test"));
    }

    @Test
    public void testUnregister() {
        ToolRegistry registry = new ToolRegistry();
        registry.register(dummyTool("Foo"));
        registry.unregister("Foo");
        assertNull(registry.get("Foo"));
        assertFalse(registry.has("Foo"));
    }

    @Test
    public void testList() {
        ToolRegistry registry = new ToolRegistry();
        registry.register(dummyTool("A"));
        registry.register(dummyTool("B"));
        List<Tool> list = registry.list();
        assertEquals(2, list.size());
        assertEquals("A", list.get(0).name());
        assertEquals("B", list.get(1).name());
    }

    @Test
    public void testToOpenAiTools() {
        ToolRegistry registry = new ToolRegistry();
        registry.register(dummyTool("MyTool"));
        var tools = registry.toOpenAiTools();
        assertEquals(1, tools.size());
        assertTrue(tools.get(0).isFunction());
    }

    @Test
    public void testGetMissingReturnsNull() {
        ToolRegistry registry = new ToolRegistry();
        assertNull(registry.get("Nonexistent"));
    }
}
