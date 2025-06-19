package io.github.shshdxk.common.mapperhelper;

import tk.mybatis.mapper.LogicDeleteException;
import tk.mybatis.mapper.annotation.LogicDelete;
import tk.mybatis.mapper.annotation.Version;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

import java.util.Set;

/**
 * SqlHelper
 */
public class SqlHelperEx {

    /**
     * 生成 update 语句
     * @param entityClass 实体类
     * @param entityName 实体类名称
     * @param updateColumn 需要更新的字段
     * @return sql
     */
    public static String updateSetColumnsIgnoreVersion(Class<?> entityClass, String entityName, String updateColumn) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        EntityColumn logicDeleteColumn = null;

        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                if (logicDeleteColumn != null) {
                    throw new LogicDeleteException(entityClass.getCanonicalName() + " 中包含多个带有 @LogicDelete 注解的字段，一个类中只能存在一个带有 @LogicDelete 注解的字段!");
                }

                logicDeleteColumn = column;
            }

            if (!column.isId() && column.isUpdatable() && !column.getEntityField().isAnnotationPresent(Version.class)) {
                if (column == logicDeleteColumn) {
                    sql.append(getIfInColumn(column, SqlHelper.logicDeleteColumnEqualsValue(column, false) + ",", updateColumn));
                } else {
                    sql.append(getIfInColumn(column, column.getColumnEqualsHolder(entityName) + ",", updateColumn));
                }
            }
        }

        sql.append("</set>");
        return sql.toString();
    }

    /**
     * 获取 if in column
     * @param column 字段
     * @param contents sql语句内容
     * @param updateColumn 需要更新的字段
     * @return sql
     */
    public static String getIfInColumn(EntityColumn column, String contents, String updateColumn) {
        return "<if test=\"" +
                updateColumn + ".contains('" + column.getProperty() + "')" +
                "\">" +
                contents +
                "</if>";
    }
}
