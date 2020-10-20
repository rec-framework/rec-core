package net.kimleo.rec.core;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode
@AllArgsConstructor
public class Record {

    List<Cell> cells;

    public static Record rec(Object... objs) {
        List<Cell> cells = Arrays.stream(objs).map(Cell::new).collect(Collectors.toList());
        return new Record(cells);
    }


    public Cell at(int index) {
        Cell cell = cells.get(index);
        if (cell == null) {
            return Cell.empty();
        }
        return cell;
    }
}
