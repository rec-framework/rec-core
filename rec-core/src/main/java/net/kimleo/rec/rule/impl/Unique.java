package net.kimleo.rec.rule.impl;

import net.kimleo.rec.Pair;
import net.kimleo.rec.collection.LinkedMultiHashMap;
import net.kimleo.rec.collection.MultiMap;
import net.kimleo.rec.record.Record;
import net.kimleo.rec.repository.RecordSet;
import net.kimleo.rec.rule.RecRule;
import net.kimleo.rec.rule.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Unique implements RecRule {

    @Override
    public Pair<Boolean, List<Result>> verify(List<RecordSet> recs) {
        assert recs.size() == 1;

        RecordSet set = recs.get(0);
        ArrayList<Result> results = new ArrayList<>();

        boolean unique = set.isUnique();

        if (!unique) {
            MultiMap<Record, Object> map = LinkedMultiHashMap.from(set.getRecords(), it -> new Object());
            for (Map.Entry<Record, Collection<Object>> items : map.entrySet()) {
                if (items.getValue().size() > 1) {
                    results.add(new Result() {
                        @Override
                        public String details() {
                            return String.format("Duplicated %s in record [ %s ]",
                                    set.getConfig().format(),
                                    items.getKey().parent().getText());
                        }
                    });
                }
            }
        }

        return new Pair<>(unique, results);
    }
}
