package com.blackgear.platform.common.data;

import java.util.ArrayList;
import java.util.List;

public class ModifiableList<T> {
    private final List<T> list;
    
    private ModifiableList(List<T> list) {
        this.list = list;
    }
    
    public static <T> ModifiableList<T> of() {
        return new ModifiableList<>(new ArrayList<>());
    }
    
    public static <T> ModifiableList<T> of(List<T> list) {
        return new ModifiableList<>(list);
    }
    
    public ModifiableList<T> addAll(List<T> list) {
        this.list.addAll(list);
        return this;
    }
    
    public ModifiableList<T> add(T element) {
        this.list.add(element);
        return this;
    }
    
    public List<T> build() {
        return this.list;
    }
}