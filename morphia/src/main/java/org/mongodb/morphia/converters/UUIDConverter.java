package org.mongodb.morphia.converters;


import org.jetbrains.annotations.NotNull;
import org.mongodb.morphia.mapping.MappedField;

import java.util.Optional;
import java.util.UUID;


/**
 * provided by http://code.google.com/p/morphia/issues/detail?id=320
 *
 * @author stummb
 * @author scotthernandez
 */
public class UUIDConverter extends TypeConverter<UUID> implements SimpleValueConverter {

    /**
     * Creates the Converter.
     */
    public UUIDConverter() {
        super(UUID.class);
    }

    @Override
    public UUID decode(final Class targetClass, @NotNull final Object fromDBObject, final MappedField optionalExtraInfo) {
        return UUID.fromString((String) fromDBObject);
    }

    @Override
    public Optional<String> encode(@NotNull final UUID value, final MappedField optionalExtraInfo) {
        return Optional.ofNullable(value.toString());
    }
}
