package net.kimleo.rec.v2.logging;

public enum LoggingLevel {
    TRACE(0),
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4);

    private final int level;

    LoggingLevel(int level){
        this.level = level;
    }

    public int level() {
        return level;
    }
}
