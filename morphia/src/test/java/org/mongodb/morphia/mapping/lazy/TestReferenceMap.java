package org.mongodb.morphia.mapping.lazy;


import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.testutil.TestEntity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class TestReferenceMap extends ProxyTestBase {
    @Test
    public final void testCreateProxy() {
        // TODO us: exclusion does not work properly with maven + junit4
        if (!LazyFeatureDependencies.testDependencyFullFilled()) {
            return;
        }

        A a = new A();
        final B b1 = new B();
        final B b2 = new B();

        a.bs.put("b1", b1);
        a.bs.put("b1+", b1);
        a.bs.put("b2", b2);

        getDs().save(b2, b1, a);
        a = getDs().get(a);

        assertIsProxy(a.bs);
        assertNotFetched(a.bs);
        Assert.assertEquals(3, a.bs.size());
        assertFetched(a.bs);

        final B b1read = a.bs.get("b1");
        Assert.assertNotNull(b1read);

        Assert.assertEquals(b1, a.bs.get("b1"));
        Assert.assertEquals(b1, a.bs.get("b1+"));
        // currently fails:
        // assertSame(a.bs.get("b1"), a.bs.get("b1+"));
        Assert.assertNotNull(a.bs.get("b2"));

        a = deserialize(a);
        assertNotFetched(a.bs);
        Assert.assertEquals(3, a.bs.size());
        assertFetched(a.bs);
        Assert.assertEquals(b1, a.bs.get("b1"));
        Assert.assertEquals(b1, a.bs.get("b1+"));
        Assert.assertNotNull(a.bs.get("b2"));

        // make sure, saving does not fetch
        a = deserialize(a);
        assertNotFetched(a.bs);
        getDs().save(a);
        assertNotFetched(a.bs);
    }


    public static class A extends TestEntity {
        @Reference(lazy = true)
        private final Map<String, B> bs = new HashMap<String, B>();
    }

    public static class B implements Serializable {
        @Id
        private final String id = new ObjectId().toString();

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            B b = (B) o;

            if (!id.equals(b.id)) {
                return false;
            }

            return true;
        }
    }

}
