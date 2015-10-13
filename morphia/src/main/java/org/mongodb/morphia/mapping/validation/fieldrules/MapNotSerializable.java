package org.mongodb.morphia.mapping.validation.fieldrules;


import org.mongodb.morphia.annotations.Serialized;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.validation.ConstraintViolation;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

import static org.mongodb.morphia.mapping.validation.ConstraintViolation.Level.FATAL;

/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class MapNotSerializable extends FieldConstraint {

    @Override
    protected void check(final MappedClass mc, final MappedField mf, final Set<ConstraintViolation> ve) {
        if (mf.isMap()) {
            if (mf.hasAnnotation(Serialized.class)) {
                final Optional<Class> key = ReflectionUtils.getParameterizedClass(mf.getField(), 0);
                final Optional<Class> value = ReflectionUtils.getParameterizedClass(mf.getField(), 1);
                key.ifPresent((Class keyClass) -> {
                    if (!Serializable.class.isAssignableFrom(keyClass)) {
                        ve.add(new ConstraintViolation(FATAL, mc, mf, getClass(),
                                                       "Key class (" + keyClass.getName() + ") is not Serializable"));
                    }
                });
                value.ifPresent((Class valueClass) -> {
                    if (!Serializable.class.isAssignableFrom(valueClass)) {
                        ve.add(new ConstraintViolation(FATAL, mc, mf, getClass(),
                                                       "Value class (" + valueClass.getName()
                                                       + ") is not Serializable"));
                    }
                });
            }
        }
    }
}
