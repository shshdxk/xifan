package io.github.shshdxk.data.specification;


import jakarta.persistence.criteria.*;
import org.springframework.lang.NonNull;

/**
 * Between specification
 * @param <T> beanType
 * @param <Y> valueType
 */
public class Between<T, Y extends Comparable<? super Y>> extends PathSpecification<T> {
    /**
     * 最小值
     */
    private final Y min;
    /**
     * 最大值
     */
    private final Y max;

    /**
     * 构造方法
     * @param path 字段
     * @param min 最小值
     * @param max 最大值
     */
    public Between(String path, @NonNull Y min, @NonNull Y max) {
        super(path);
        this.min = min;
        this.max = max;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Path<Y> path = this.path(root);
        return cb.between(path, this.min, this.max);
    }
}
