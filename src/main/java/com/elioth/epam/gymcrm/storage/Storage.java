package com.elioth.epam.gymcrm.storage;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;


public interface Storage <K, V> {
    public Map<K, V> getData();

}
