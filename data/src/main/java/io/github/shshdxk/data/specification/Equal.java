package io.github.shshdxk.data.specification;

import com.google.common.base.Preconditions;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Equal specification
 * @param <T> beanType
 */
public class Equal<T> extends PathSpecification<T> {
    private final Object value;

    /**
     * constructor
     * @param path field name
     * @param value field value
     */
    public Equal(String path, Object value) {
        super(path);
        Preconditions.checkArgument(value != null, "Use IsNull or IsNotNull for null object");
        this.value = value;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(this.path(root), this.value);
    }
}
