package net.kimleo.rec.core;

import io.reactivex.Flowable;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TableTest {


    @Test
    public void shoudlSuccessfullyAccessATable() {
        String[] headers = {"name", "age", "dob"};

        String[][] records = {
                {"Kimmy", "25", "2000-12-12"},
                {"Jack", "24", "2000-11-12"},
                {"Pony", "23", "2000-11-11"}
        };

        List<Record> recs = Arrays.stream(records).map(Record::rec)
                .collect(Collectors.toList());

        List<Cell> collect = Arrays.stream(headers).map(Cell::new).collect(Collectors.toList());

        Table table = new Table(collect, recs);

        Getters.TableGetter getter = Getters.from(table);

        assertThat(getter.at(1).get("name").data, is("Jack"));

        assertTrue(table.hasHeader());
        assertEquals(3, table.size());
    }

    @Test
    public void shouldSuccessfullyCreateTableWithoutHeader() {
        String[][] records = {
                {"Kimmy", "25", "2000-12-12"},
                {"Jack", "24", "2000-11-12"},
                {"Pony", "23", "2000-11-11"}
        };
        List<Record> recs = Arrays.stream(records).map(Record::rec)
                .collect(Collectors.toList());

        Table table = new Table(recs);


        assertFalse(table.hasHeader());
        assertEquals(3, table.size());
    }
}