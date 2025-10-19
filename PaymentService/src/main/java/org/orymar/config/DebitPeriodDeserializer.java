package org.orymar.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.orymar.domain.enums.TimeUnitSuffix;

import java.io.IOException;
import java.time.Duration;

public class DebitPeriodDeserializer extends JsonDeserializer<Duration> {

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getText().trim().toLowerCase();

        TimeUnitSuffix unit = TimeUnitSuffix.fromSuffix(value);
        long amount = Long.parseLong(value.substring(0, value.length() - 1));

        return switch (unit) {
            case MINUTES -> Duration.ofMinutes(amount);
            case HOURS -> Duration.ofHours(amount);
            case DAYS -> Duration.ofDays(amount);
        };
    }
}