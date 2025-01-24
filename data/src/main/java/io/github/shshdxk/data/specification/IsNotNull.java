package io.github.shshdxk.data.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * IsNotNull specification
 * @param <T> beanType
 */
public class IsNotNull<T> extends PathSpecification<T> {

    /**
     * constructor
     * @param path field name
     */
    public IsNotNull(String path) {
        super(path);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.isNotNull(this.path(root));
    }
}
