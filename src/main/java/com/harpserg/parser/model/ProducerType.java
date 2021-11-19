package com.harpserg.parser.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ProducerType {
    CSV,
    JSONL,
    UNKNOWN;

    private static final Map<String, ProducerType> ENUM_MAP;

    static {
        ENUM_MAP = Collections.unmodifiableMap(Arrays.stream(ProducerType.values()).collect(Collectors.toMap(ProducerType::name, Function.identity())));
    }

    public static ProducerType getByName(String name) {
        ProducerType producerType = ENUM_MAP.get(name);
        return producerType != null ? producerType : UNKNOWN;
    }
}
