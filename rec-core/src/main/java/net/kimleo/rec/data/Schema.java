package net.kimleo.rec.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Schema {

    private final List<String> names;
    private final List<DataType> types;
    private final Map<String, Integer> nameIndex;

    public Schema(List<String> names, List<DataType> types) {
        if (names.size() != types.size()) {
            throw new IllegalArgumentException("names and types must have the same size");
        }
        this.names = Collections.unmodifiableList(new ArrayList<>(names));
        this.types = Collections.unmodifiableList(new ArrayList<>(types));
        Map<String, Integer> idx = new LinkedHashMap<>();
        for (int i = 0; i < names.size(); i++) {
            idx.put(names.get(i), i);
        }
        this.nameIndex = Collections.unmodifiableMap(idx);
    }

    public static Schema of(String... names) {
        List<DataType> types = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            types.add(DataType.STRING);
        }
        List<String> nameList = new ArrayList<>();
        Collections.addAll(nameList, names);
        return new Schema(nameList, types);
    }

    public static Schema of(List<String> names) {
        List<DataType> types = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            types.add(DataType.STRING);
        }
        return new Schema(names, types);
    }

    public static Schema typed(List<String> names, List<DataType> types) {
        return new Schema(names, types);
    }

    public int columnCount() {
        return names.size();
    }

    public String name(int index) {
        return names.get(index);
    }

    public DataType type(int index) {
        return types.get(index);
    }

    public List<String> names() {
        return names;
    }

    public List<DataType> types() {
        return types;
    }

    public int indexOf(String name) {
        Integer idx = nameIndex.get(name);
        return idx != null ? idx : -1;
    }

    public boolean hasField(String name) {
        return nameIndex.containsKey(name);
    }
}
