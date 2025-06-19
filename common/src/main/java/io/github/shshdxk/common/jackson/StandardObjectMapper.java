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

/**
 * @author shshdxk
 */
public class StandardObjectMapper {

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

    /**
     * 获取单例
     * @return ObjectMapper
     */
    public static ObjectMapper getInstance() {
        return Holder.INSTANCE.objectMapper;
    }

    /**
     * 将对象转为json字符串
     * @param data 对象
     * @return json字符串
     */
    public static String stringify(Object data) {
        try {
            return getInstance().writeValueAsString(data);
        } catch (JsonProcessingException var2) {
            throw new RuntimeException(var2);
        }
    }

    /**
     * 将json转为对象
     * @param json json字符串
     * @param type 对象类型
     * @return 对象
     * @param <T> 对象类型
     */
    public static <T> T readValue(String json, TypeReference<T> type) {
        try {
            return getInstance().readValue(json, type);
        } catch (IOException var3) {
            throw new RuntimeException(var3);
        }
    }

    private enum Holder {
        /**
         * 单例
         */
        INSTANCE;

        private final ObjectMapper objectMapper = StandardObjectMapper.newObjectMapper();
    }
}
