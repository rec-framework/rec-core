package net.kimleo.rec.model.mapper;

import net.kimleo.rec.common.concept.Mapped;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapRecord implements Mapped<String> {

    private final Map<String, String> data;

    public MapRecord(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public String get(String field) {
        return data.get(field);
    }

    @Override
    public List<String> keys() {
        return new ArrayList<>(data.keySet());
    }
}
