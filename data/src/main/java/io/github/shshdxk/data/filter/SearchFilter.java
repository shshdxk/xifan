package io.github.shshdxk.data.filter;

import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

/**
 */
public interface SearchFilter<T> extends Serializable {

    /**
     * 获取字段名
     * @return 字段名
     */
    String getName();

    /**
     * 转换为Specification
     * @return Specification
     */
    Specification<T> toSpecification();

    /**
     * 转换为表达式
     * @return 表达式
     */
    String toExpression();

    /**
     * 是否有效
     * @return 有效
     */
    boolean isValid();
}
