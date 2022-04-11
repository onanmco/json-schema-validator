package com.cemonan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JSONValidator {

    private final static int SEEK = 256;
    private final static int BUF_LEN = 1024;

    private JsonSchemaFactory jsonSchemaFactory;
    private ObjectMapper objectMapper;
    private JsonSchema schema;
    private JsonNode json;
    private List<String> errors;

    public JSONValidator() {
        init(SpecVersion.VersionFlag.V7);
    }

    public JSONValidator(SpecVersion.VersionFlag versionFlag) {
        init(versionFlag);
    }

    private void init(SpecVersion.VersionFlag versionFlag) {
        this.jsonSchemaFactory = JsonSchemaFactory.getInstance(versionFlag);
        this.objectMapper = new ObjectMapper();
        this.errors = new ArrayList<>();
    }

    private byte[] read(InputStream in) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUF_LEN];
        int readBytes = 0;
        while ((readBytes = in.read(buffer, 0, SEEK)) != -1) {
            bos.write(buffer, 0, readBytes);
        }
        return bos.toByteArray();
    }

    public JSONValidator schema(InputStream inputStream) throws IOException {
        this.schema = jsonSchemaFactory.getSchema(objectMapper.readTree(inputStream));
        return this;
    }

    public JSONValidator schema(String pathToSchema) throws IOException {
        InputStream in = new FileInputStream(pathToSchema);
        this.schema = jsonSchemaFactory.getSchema(objectMapper.readTree(in));
        return this;
    }

    public JSONValidator json(InputStream inputStream) throws IOException {
        this.json = objectMapper.readTree(inputStream);
        return this;
    }

    public JSONValidator json(String pathToJson) throws IOException {
        InputStream in = new FileInputStream(pathToJson);
        this.json = objectMapper.readTree(this.read(in));
        return this;
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public boolean isValid() throws IOException {
        Set<ValidationMessage> messages = this.schema.validate(this.json);

        if (messages.isEmpty()) {
            return true;
        }

        messages.forEach((ValidationMessage vm) -> {
            this.errors.add(vm.getMessage());
        });

        return false;
    }
}
