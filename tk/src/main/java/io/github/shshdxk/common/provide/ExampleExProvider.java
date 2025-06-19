package io.github.shshdxk.common.provide;

import io.github.shshdxk.common.mapperhelper.SqlHelperEx;
import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

/**
 * 批量更新
 */
public class ExampleExProvider extends MapperTemplate {

    /**
     * 构造函数
     * @param mapperClass mapperClass
     * @param mapperHelper mapperHelper
     */
    public ExampleExProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 根据Example更新
     *
     * @param ms ms
     * @return sql
     */
    public String updateColumnsByExample(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        if (isCheckExampleEntityClass()) {
            sql.append(SqlHelper.exampleCheck(entityClass));
        }
        //安全更新，Example 必须包含条件
        if (getConfig().isSafeUpdate()) {
            sql.append(SqlHelper.exampleHasAtLeastOneCriteriaCheck("example"));
        }
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass), "example"));


        sql.append(SqlHelperEx.updateSetColumnsIgnoreVersion(entityClass, "record", "updateColumn"));
        sql.append(SqlHelper.updateByExampleWhereClause());
        return sql.toString();
    }


}
