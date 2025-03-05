package com.orphy.inpensa_backend.v1.model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

public enum TransactionType {
    EXPENSE,
    INCOME;

    public static class TransactionTypeDeserializer extends StdDeserializer<TransactionType>  {
        protected TransactionTypeDeserializer() {
            this(null);
        }
        protected TransactionTypeDeserializer(Class vc) {
            super(vc);
        }

        @Override
        public TransactionType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String role = node.asText();
            role = role.toUpperCase();
            return TransactionType.valueOf(role);
        }
    }
}
