package net.kimleo.rec.v2;

import net.kimleo.rec.v2.logging.Logger;
import net.kimleo.rec.v2.logging.impl.LogManager;
import net.kimleo.rec.v2.scripting.Scripting;

import java.io.File;

public class App {

    static Logger logger = LogManager.logger("RecApplication");

    public static void main(String[] args) throws Exception {

        logger.info("Application started");

        System.out.println("Rec v2 ...");

        logger.info("Application ended");
    }
}
