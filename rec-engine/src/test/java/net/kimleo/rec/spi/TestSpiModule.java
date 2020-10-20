package net.kimleo.rec.spi;

public class TestSpiModule implements RecModule {

    @Operator("the-operator-name")
    public void hello() {
        System.out.println("Hello world");
    }
}
