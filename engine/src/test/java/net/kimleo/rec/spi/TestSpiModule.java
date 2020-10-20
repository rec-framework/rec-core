package net.kimleo.rec.spi;

@Package
public class TestSpiModule implements RecModule {

    @Operator
    public void hello() {
        System.out.println("Hello world");
    }
}
