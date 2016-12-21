package net.kimleo.rec.rule.impl;

import net.kimleo.rec.Pair;
import net.kimleo.rec.repository.RecRepository;
import net.kimleo.rec.repository.RecordSet;
import net.kimleo.rec.rule.RecRule;
import net.kimleo.rec.rule.Result;
import net.kimleo.rec.rule.RuleLoader;
import net.kimleo.rec.rule.RuleRunner;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.kimleo.rec.API.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ExistTest {
    @Test
    public void testShouldCheckExistence() throws Exception {
        RecRule exist = new Exist();

        RecordSet stmt = collect(singletonList(rec("hello world")),
                type("Stmt", "value"));
        RecordSet lineNum = collect(singletonList(rec("1, hello world")),
                type("LineNum", "line, stmt"));
        RecordSet program = collect(singletonList(rec("1, hello world")),
                type("Program", "line, stmt"));


        RecRepository repo = repo(asList(stmt, lineNum, program));


        Pair<Boolean, List<Result>> verify = exist.verify(asList(lineNum, program));

        assertThat(verify.getFirst(), is(true));
        assertTrue(verify.getSecond().isEmpty());

        Pair<Boolean, List<Result>> verify1 = exist.verify(asList(program, stmt));

        assertThat(verify1.getFirst(), is(false));
        assertTrue(verify1.getSecond().size() == 1);

        List<RuleRunner> runners = new RuleLoader().load(singletonList("exist: Stmt[value], Program[stmt]"));

        Pair<Boolean, List<Result>> verify2 = runners.get(0).runOn(repo);


        assertThat(verify2.getFirst(), is(true));
        assertTrue(verify2.getSecond().isEmpty());

    }
}