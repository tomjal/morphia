package org.mongodb.morphia.mapping.validation.fieldrules;


import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Serialized;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.mapping.MappedField;
import org.mongodb.morphia.mapping.validation.ConstraintViolation;
import org.mongodb.morphia.utils.ReflectionUtils;

import java.util.Optional;
import java.util.Set;

import static org.mongodb.morphia.mapping.validation.ConstraintViolation.Level.FATAL;
import static org.mongodb.morphia.mapping.validation.ConstraintViolation.Level.WARNING;


/**
 * @author Uwe Schaefer, (us@thomas-daily.de)
 */
public class MapKeyDifferentFromString extends FieldConstraint {
    private static final String SUPPORTED = "(Map<String/Enum/Long/ObjectId/..., ?>)";

    @Override
    protected void check(final MappedClass mc, final MappedField mf, final Set<ConstraintViolation> ve) {
        if (mf.isMap() && (!mf.hasAnnotation(Serialized.class))) {
            final Optional<Class> aClass = ReflectionUtils.getParameterizedClass(mf.getField(), 0);
            // WARN if not parameterized : null or Object...
            if (!aClass.isPresent()) {
                ve.add(new ConstraintViolation(WARNING, mc, mf, getClass(), "Map is not parameterized" + SUPPORTED));
            } else {
                Class classType = aClass.get();
                if (Object.class.equals(classType)) {
                    ve.add(new ConstraintViolation(WARNING, mc, mf, getClass(),
                                                   "Maps cannot be keyed by Object (Map<Object,?>); Use a parametrized type that is supported "
                                                   + SUPPORTED));
                } else if (!classType.equals(String.class)
                           && !classType.equals(ObjectId.class)
                           && !ReflectionUtils.isPrimitiveLike(classType)) {
                    ve.add(new ConstraintViolation(FATAL, mc, mf, getClass(),
                                                   "Maps must be keyed by a simple type " + SUPPORTED + "; " + classType
                                                   + " is not supported as a map key type."));
                }
            }
        }
    }
}
