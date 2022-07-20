package io.streamnative.util.schema;

import org.apache.pulsar.client.api.Schema;

public class PulsarSchemaUtils {

    public static Schema getSchemaByClass(Class clazz) {
        if (clazz.getName().equals("java.lang.Float")) {
            return Schema.FLOAT;
        }

        if (clazz.getName().equals("java.lang.Short")) {
            return Schema.INT16;
        }

        if (clazz.getName().equals("java.lang.Integer")) {
            return Schema.INT32;
        }

        if (clazz.getName().equals("java.lang.Long")) {
            return Schema.INT64;
        }

        if (clazz.getName().equals("java.lang.String")) {
            return Schema.STRING;
        } else {
            return Schema.JSON(clazz);
        }
    }
}
