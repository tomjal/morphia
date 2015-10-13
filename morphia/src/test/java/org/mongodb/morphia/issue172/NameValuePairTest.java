package org.mongodb.morphia.issue172;


import org.bson.types.ObjectId;
import org.junit.Ignore;
import org.junit.Test;
import org.mongodb.morphia.TestBase;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.entities.SimpleEnum;

import java.io.Serializable;


public class NameValuePairTest extends TestBase {

    @Test
    @Ignore("add back when TypeLiteral support is in; issue 175")
    public void testNameValuePairWithDoubleIn() throws Exception {
        getMorphia().map(NameValuePairContainer.class);
        final NameValuePairContainer container = new NameValuePairContainer();
        container.pair = new NameValuePair<SimpleEnum, Double>(SimpleEnum.FOO, 1.2d);
        getDs().save(container);

        getDs().get(container);
    }

    @Entity
    private static class NameValuePairContainer {
        @Id
        private ObjectId id;
        private NameValuePair<SimpleEnum, Double> pair;
    }

    private static class NameValuePair<T1 extends Enum<?>, T2> implements Serializable {
        private final T2 value;
        private final T1 name;

        public NameValuePair(final T1 name, final T2 value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public int hashCode() {
            int result = value != null ? value.hashCode() : 0;
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            NameValuePair<?, ?> that = (NameValuePair<?, ?>) o;

            if (value != null ? !value.equals(that.value) : that.value != null) {
                return false;
            }
            if (name != null ? !name.equals(that.name) : that.name != null) {
                return false;
            }

            return true;
        }
    }

}
