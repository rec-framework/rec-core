package net.kimleo.rec.rule.impl;

import net.kimleo.rec.Pair;
import net.kimleo.rec.record.Record;
import net.kimleo.rec.repository.RecordSet;
import net.kimleo.rec.rule.RecRule;
import net.kimleo.rec.rule.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Exist implements RecRule {

    @Override
    public Pair<Boolean, List<Result>> verify(List<RecordSet> recs) {
        assert recs.size() == 2;

        boolean successful = true;
        ArrayList<Result> result = new ArrayList<>();

        RecordSet image = recs.get(0);
        RecordSet complete = recs.get(1);

        Set<Record> set = complete.getRecords().collect(Collectors.toSet());

        for (Record rec: image) {
            if (!set.contains(rec)) {
                result.add(() -> String.format("%s of %s cannot be found in %s",
                        rec.getText(),
                        image.getConfig().name(),
                        complete.getConfig().name()));
                successful = false;
            }
        }

        return new Pair<>(successful, result);
    }
}
