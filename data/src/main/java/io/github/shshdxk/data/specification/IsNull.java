package io.github.shshdxk.data.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * IsNotNull specification
 * @param <T> beanType
 */
public class IsNull<T> extends PathSpecification<T> {

    /**
     * constructor
     * @param path field name
     */
    public IsNull(String path) {
        super(path);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.isNull(this.path(root));
    }
}
