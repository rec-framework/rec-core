package net.kimleo.rec.repository;

import net.kimleo.rec.Pair;
import net.kimleo.rec.rule.Result;
import net.kimleo.rec.rule.RuleLoader;
import net.kimleo.rec.rule.impl.Unique;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

public class RecRepositoryTest {
    private List<String> records = linesOfRes("person_test.txt");

    private List<String> rec = linesOfRes("person_test.txt.rec");


    private List<String> linesOfRes(String resource) {
        InputStream res = RecRepositoryTest.class.getClassLoader().getResourceAsStream(resource);

        return new BufferedReader(new InputStreamReader(res)).lines().collect(toList());
    }

    @Test
    public void shouldSuccess() throws Exception {
        RecConfig type = DefaultRecConfig.makeTypeFrom(rec);
        RecordSet collect = RecordSet.loadData(records.stream(), type);
        RecRepository repo = new RecRepository(singletonList(collect));

        assertNotNull(repo);

        assertTrue(repo
                .from("Person")
                .where("first name", it ->  it.contains("Kimmy"))
                .getRecords().count() == 1L);

        Pair<Boolean, List<Result>> unique1 = new Unique().verify(singletonList(collect.select("first name")));
        assertTrue(unique1.getFirst());

        Pair<Boolean, List<Result>> unique2 = new Unique().verify(singletonList(collect.select("comment")));

        assertFalse(unique2.getFirst());
        assertTrue(unique2.getSecond().size() == 1);

        RecRepository names = repo.select("Person[first name], Person[comment] as Comment");

        assertTrue(names.getSize() == 2);
        assertTrue(names.get(1).getRecords().count() == 5L);

        List<Pair<Boolean, List<Result>>> ruleResult = new RuleLoader()
                .load(asList("unique: Person[first name]", "unique: Person[comment]")).stream().map(it -> {
                    return it.runOn(repo);
                }).collect(toList());

        assert(ruleResult.size() == 2);

        assert(ruleResult.get(1).getSecond().size() == 1);

    }
}