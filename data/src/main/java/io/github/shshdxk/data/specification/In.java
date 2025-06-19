package io.github.shshdxk.data.specification;


import jakarta.persistence.criteria.*;

/**
 * In specification
 * @param <T> beanType
 */
public class In<T> extends PathSpecification<T> {
    /**
     * allowed values
     */
    private final Object[] allowedValues;

    /**
     * constructor
     * @param path field name
     * @param allowedValues allowed values
     */
    public In(String path, Object[] allowedValues) {
        super(path);
        this.allowedValues = allowedValues;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Path<?> path = this.path(root);
        return path.in(this.allowedValues);
    }
}