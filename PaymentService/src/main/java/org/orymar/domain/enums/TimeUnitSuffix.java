package org.orymar.domain.enums;

import java.util.Arrays;

public enum TimeUnitSuffix {
    MINUTES('m'),
    HOURS('h'),
    DAYS('d');

    private final char suffix;

    TimeUnitSuffix(char suffix) {
        this.suffix = suffix;
    }

    public static TimeUnitSuffix fromSuffix(String value) {
        char lastChar = value.charAt(value.length() - 1);
        return Arrays.stream(values())
                .filter(u -> u.suffix == lastChar)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown time format: " + value));
    }
}