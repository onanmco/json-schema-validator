package com.cemonan;

import static org.junit.Assert.assertFalse;
import org.junit.Test;
import java.io.IOException;

public class AppTest
{
    @Test
    public void testWithAbsolutePaths() throws IOException {
        JSONValidator validator = new JSONValidator()
                .schema("/Users/cemonan/Desktop/json-schema-validator/src/example/java/com/cemonan/resources/schema.json")
                .json("/Users/cemonan/Desktop/json-schema-validator/src/example/java/com/cemonan/resources/data.json");

        assertFalse(validator.isValid());

        validator.getErrors().forEach(System.out::println);
    }

    @Test
    public void testWithInputStreams() throws IOException {
        JSONValidator validator = new JSONValidator()
                .schema(getClass().getResourceAsStream("resources/schema.json"))
                .json(getClass().getResourceAsStream("resources/data.json"));

        assertFalse(validator.isValid());

        validator.getErrors().forEach(System.out::println);
    }
}
