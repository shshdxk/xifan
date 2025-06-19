package io.github.shshdxk.common.example;

import io.github.shshdxk.common.provide.ExampleExProvider;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;
import tk.mybatis.mapper.annotation.RegisterMapper;

import java.util.List;

/**
 * 自定义Mapper
 */
@RegisterMapper
public interface UpdateByExampleExMapper<T> {

    /**
     * 根据条件更新指定字段
     * @param record 更新的内容
     * @param updateColumn 需要更新的字段
     * @param example 条件
     * @return 更新数量
     */
    @UpdateProvider(type = ExampleExProvider.class, method = "dynamicSQL")
    int updateColumnsByExample(@Param("record") T record, @Param("updateColumn") List<String> updateColumn, @Param("example") Object example);

}
