package net.kimleo.rec.core;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Cell {
    final Object data;

    public static Cell cell(Object obj) {
        return new Cell(obj);
    }

    public static Cell empty() {
        return new Cell(null);
    }
}
