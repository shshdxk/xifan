package io.github.shshdxk.common.jackson;


import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

/**
 * @author shshdxk
 */
public class StandardObjectMapper {

    private static ObjectMapper newObjectMapper() {
        SimpleModule simpleModule = new SimpleModule();
        return JsonMapper.builder()
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .disable(MapperFeature.USE_GETTERS_AS_SETTERS)
                .enable(JsonReadFeature.ALLOW_JAVA_COMMENTS)
                .enable(JsonReadFeature.ALLOW_YAML_COMMENTS)
                .enable(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES)
                .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES)
                .addModules(simpleModule)
                .build();
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
        return getInstance().writeValueAsString(data);
    }

    /**
     * 将json转为对象
     * @param json json字符串
     * @param type 对象类型
     * @return 对象
     * @param <T> 对象类型
     */
    public static <T> T readValue(String json, TypeReference<T> type) {
        return getInstance().readValue(json, type);
    }

    private enum Holder {
        /**
         * 单例
         */
        INSTANCE;

        private ObjectMapper objectMapper = StandardObjectMapper.newObjectMapper();

        /**
         * 重新构建ObjectMapper
         * @param simpleModule 模块
         */
        public void rebuild(SimpleModule simpleModule) {
            objectMapper = objectMapper.rebuild().addModule(simpleModule).build();
        }

    }

    /**
     * 重新构建ObjectMapper
     * @param simpleModule 模块
     */
    public static void rebuild(SimpleModule simpleModule) {
        Holder.INSTANCE.rebuild(simpleModule);
    }
}
