package io.github.shshdxk.data.specification;


import jakarta.persistence.criteria.*;
import org.springframework.lang.NonNull;

public class Between<T, Y extends Comparable<? super Y>> extends PathSpecification<T> {
    private final Y min;
    private final Y max;

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
