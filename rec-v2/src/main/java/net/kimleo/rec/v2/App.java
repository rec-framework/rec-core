package net.kimleo.rec.v2;

import net.kimleo.rec.v2.scripting.Scripting;

import java.io.File;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Rec v2 ...");
        Scripting.runfile(new File(args[0]), args[0]);
    }
}
