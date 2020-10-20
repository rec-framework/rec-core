package net.kimleo.rec.v2.utils;

import java.io.Serializable;

public class PersistableClass implements Serializable {
    public static final long serialVersionUID = 1453356753764L;

    private String name;
    private String password;

    PersistableClass(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
