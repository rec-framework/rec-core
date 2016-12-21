package net.kimleo.rec.repository.selector;

import net.kimleo.rec.repository.RecRepository;
import net.kimleo.rec.repository.RecordSet;

import java.util.List;

import static java.util.Collections.singletonList;

public class FieldSelector implements Selector {
    public final String type;
    public final List<String> properties;
    private String newType;

    public void setNewType(String type) {
        this.newType = type;
    }

    public FieldSelector(String type, List<String> properties) {
        this.type = type;
        this.properties = properties;
        this.newType = type;
    }

    @Override
    public List<RecordSet> findAll(RecRepository repo) {
        return singletonList(repo.from(type).select(properties, newType));
    }
}
