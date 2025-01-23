package io.github.shshdxk.data.specification;


import jakarta.persistence.criteria.*;

public class In<T> extends PathSpecification<T> {
    private final Object[] allowedValues;

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