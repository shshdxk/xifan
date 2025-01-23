package io.github.shshdxk.data.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class Not<T> implements Specification<T> {
    private final Specification<T> original;

    public Not(Specification<T> original) {
        this.original = original;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Predicate p = this.original.toPredicate(root, query, cb);
        return cb.not(p);
    }
}