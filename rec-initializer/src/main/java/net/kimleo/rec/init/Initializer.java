package net.kimleo.rec.init;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static net.kimleo.rec.util.Sys.die;

public class Initializer {
    final String file;
    final HashMap<String, String> parameters;
    static final List<String> REQUIRED_PROPERTIES = Arrays.asList("name", "delimiter", "escape", "format");

    public Initializer(String file, HashMap<String, String> parameters) {
        this.file = file;
        this.parameters = parameters;
    }

    public void init() throws IOException {
        String recFile = file + ".rec";

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        for (String property : REQUIRED_PROPERTIES) {
            if (parameters.containsKey(property)) {
                System.out.printf("Please input the %s:", property);
                String value = reader.readLine().trim();
                if (!checkProperty(property, value)) {
                    die(String.format("Unexpected %s: %s", property, value));
                }
                parameters.put(property, value);
            }
        }

        try (PrintWriter writer =
                     new PrintWriter(new OutputStreamWriter(new FileOutputStream(new File(recFile))))) {
            writer.println(parameters.get("name"));

            for (String property : parameters.keySet()) {
                if (!"name".equals(property)) {
                    writer.println(String.format("%s=%s", property, parameters.get(property)));
                }
            }
        } catch (Exception e) {
            die("Unexpected exception found: " + e.getMessage());
        }
    }

    private boolean checkProperty(String property, String value) {
        if (value == null || value.isEmpty()) return false;
        switch (property) {
            case "name":
                return value.chars().allMatch(Character::isJavaIdentifierPart);
            case "delimiter":
            case "escape":
                return value.length() == 1 || value.matches("^%\\d+&");
        }
        return true;
    }
}
