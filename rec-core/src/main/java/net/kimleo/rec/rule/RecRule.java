package net.kimleo.rec.rule;

import net.kimleo.rec.Pair;
import net.kimleo.rec.repository.RecordSet;

import java.util.List;

public interface RecRule {
    Pair<Boolean, List<Result>> verify(List<RecordSet> recs);
}
