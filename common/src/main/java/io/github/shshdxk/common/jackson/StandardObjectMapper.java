package io.github.shshdxk.common.jackson;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class StandardObjectMapper {
    public StandardObjectMapper() {
    }

    private static ObjectMapper newObjectMapper() {
        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Long.class, LongToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, LongToStringSerializer.instance);
//        simpleModule.addSerializer(long[].class, new LongArrayToStringArraySerializer());
        ObjectMapper mapper = JsonMapper.builder()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .build().registerModule(simpleModule);
        JsonFactory jsonFactory = mapper.getFactory();
        jsonFactory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        jsonFactory.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
        jsonFactory.enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
        return mapper;
    }

    public static ObjectMapper getInstance() {
        return Holder.INSTANCE.objectMapper;
    }

    public static String stringify(Object data) {
        try {
            return getInstance().writeValueAsString(data);
        } catch (JsonProcessingException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static <T> T readValue(String json, TypeReference<T> type) {
        try {
            return getInstance().readValue(json, type);
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }

    private static enum Holder {
        INSTANCE;

        private final ObjectMapper objectMapper = StandardObjectMapper.newObjectMapper();

        private Holder() {
        }
    }
}
