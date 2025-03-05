package com.orphy.inpensa_backend.v1.model;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Supplier;

public enum Role {
    /**
     * Changes to these values need to be also changed in the DB T_ROLE Table Schema and Database values
     */
    USER_READ("user:read"),
    USER_WRITE("user:write"),
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write"),
    SUPER_ADMIN("superadmin");

    private final String SCP_VALUE = "SCOPE_";

    private final String ROLE_VALUE;

    Role(String roleValue) {
        this.ROLE_VALUE = roleValue;
    }

    public String getScope() {
        return SCP_VALUE + ROLE_VALUE;
    }

    public String getRoleValue() {
        return ROLE_VALUE;
    }

    public static Role valueOfRole(String role, Supplier<? extends RuntimeException> exSupplier) {
        return Arrays.stream(values())
                .filter(each -> each.ROLE_VALUE.equals(role))
                .findFirst()
                .orElseThrow(exSupplier);
    }

    public static class RoleDeserializer extends StdDeserializer<Role> {

        protected RoleDeserializer() {
            this(null);
        }
        protected RoleDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public Role deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {

            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String role = node.asText();

            return Role.valueOfRole(role, () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role Value is incorrect."));
        }
    }

    public static class RoleSerializer extends StdSerializer<Role> {

        protected RoleSerializer() {
            this(null);
        }
        protected RoleSerializer(Class<Role> t) {
            super(t);
        }

        @Override
        public void serialize(Role role, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(role.getRoleValue());
        }
    }
}
