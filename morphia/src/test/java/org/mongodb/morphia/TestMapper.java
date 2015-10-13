package org.mongodb.morphia;


import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.PostLoad;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.mapping.lazy.LazyFeatureDependencies;

import java.io.Serializable;


/**
 * Tests mapper functions; this is tied to some of the internals.
 *
 * @author scotthernandez
 */
public class TestMapper extends TestBase {
    @Test
    public void serializableId() throws Exception {
        final CustomId cId = new CustomId();
        cId.id = new ObjectId();
        cId.type = "banker";

        final UsesCustomIdObject object = new UsesCustomIdObject();
        object.id = cId;
        object.text = "hllo";
        getDs().save(object);
    }

    @Test
    public void singleLookup() throws Exception {
        A.loadCount = 0;
        final A a = new A();
        HoldsMultipleA holder = new HoldsMultipleA();
        holder.a1 = a;
        holder.a2 = a;
        getDs().save(a, holder);
        holder = getDs().get(HoldsMultipleA.class, holder.id);
        Assert.assertEquals(1, A.loadCount);
        Assert.assertTrue(holder.a1 == holder.a2);
    }

    @Test
    public void singleProxy() throws Exception {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!LazyFeatureDependencies.testDependencyFullFilled()) {
            return;
        }

        A.loadCount = 0;
        final A a = new A();
        HoldsMultipleALazily holder = new HoldsMultipleALazily();
        holder.a1 = a;
        holder.a2 = a;
        holder.a3 = a;
        getDs().save(a, holder);
        Assert.assertEquals(0, A.loadCount);
        holder = getDs().get(HoldsMultipleALazily.class, holder.id);
        Assert.assertNotNull(holder.a2);
        Assert.assertEquals(1, A.loadCount);
        Assert.assertFalse(holder.a1 == holder.a2);
        // FIXME currently not guaranteed:
        // Assert.assertTrue(holder.a1 == holder.a3);

        // A.loadCount=0;
        // Assert.assertEquals(holder.a1.getId(), holder.a2.getId());

    }

    public static class A {
        private static int loadCount;
        @Id
        private ObjectId id;

        @PostLoad
        protected void postConstruct() {
            if (loadCount > 1) {
                throw new RuntimeException();
            }

            loadCount++;
        }

        String getId() {
            return id.toString();
        }
    }

    @Entity("holders")
    public static class HoldsMultipleA {
        @Id
        private ObjectId id;
        @Reference
        private A a1;
        @Reference
        private A a2;
    }

    @Entity("holders")
    public static class HoldsMultipleALazily {
        @Id
        private ObjectId id;
        @Reference(lazy = true)
        private A a1;
        @Reference
        private A a2;
        @Reference(lazy = true)
        private A a3;
    }

    public static class CustomId implements Serializable {

        @Property("v")
        private ObjectId id;
        @Property("t")
        private String type;

        public ObjectId getId() {
            return id;
        }

        public void setId(final ObjectId id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
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

            CustomId customId = (CustomId) o;

            if (id != null ? !id.equals(customId.id) : customId.id != null) {
                return false;
            }
            if (type != null ? !type.equals(customId.type) : customId.type != null) {
                return false;
            }

            return true;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("CustomId [");
            if (id != null) {
                builder.append("id=").append(id).append(", ");
            }
            if (type != null) {
                builder.append("type=").append(type);
            }
            builder.append("]");
            return builder.toString();
        }
    }

    public static class UsesCustomIdObject {
        @Id
        private CustomId id;
        private String text;

        public CustomId getId() {
            return id;
        }

        public void setId(final CustomId id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(final String text) {
            this.text = text;
        }
    }

}
