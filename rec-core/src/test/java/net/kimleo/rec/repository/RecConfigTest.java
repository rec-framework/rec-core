package net.kimleo.rec.repository;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class RecConfigTest {


    private List<String> linesOfRes(String resource) {
        InputStream res = RecRepositoryTest.class.getClassLoader().getResourceAsStream(resource);

        return new BufferedReader(new InputStreamReader(res)).lines().collect(toList());
    }


    @Test
    public void shouldReturnType() {
        List<String> lines = linesOfRes("person_test.txt.rec");

        RecConfig type = DefaultRecConfig.makeTypeFrom(lines);

        assertNotNull(type);
    }

    @Test
    public void shouldParseRecord() {
        List<String> records = linesOfRes("person_test.txt");
        List<String> rec = linesOfRes("person_test.txt.rec");

        RecConfig type = DefaultRecConfig.makeTypeFrom(rec);

        RecordSet collect = RecordSet.loadData(records.stream(), type);

        assertNotNull(collect);

        assertEquals(collect.where("first name", it -> it.contains("Kim")).getRecords().count(), 3);
        assertEquals(collect.where("first name", it -> it.contains("Kimm")).getRecords().count(), 1);

    }

}