package net.kimleo.rec.plugin;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class AgentModuleTest {

    @Test
    public void testName() {
        AgentModule module = new AgentModule();
        assertEquals("agent", module.name());
    }

    @Test
    public void testModuleReturnsSelf() {
        AgentModule module = new AgentModule();
        assertSame(module, module.module());
    }
}
