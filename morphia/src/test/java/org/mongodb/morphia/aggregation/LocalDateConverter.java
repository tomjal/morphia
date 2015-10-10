package org.mongodb.morphia.aggregation;

import org.mongodb.morphia.converters.DateConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.time.LocalDate;

public class LocalDateConverter extends DateConverter {
    public LocalDateConverter() {
        super(LocalDate.class);
    }

    @Override
    public Object decode(Class<?> targetClass, Object val, MappedField optionalExtraInfo) {
        return LocalDate.ofEpochDay((long)val);
    }

    @Override
    public Object encode(Object value, MappedField optionalExtraInfo) {
        return ((LocalDate) value).toEpochDay();
    }
}
