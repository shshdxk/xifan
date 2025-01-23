package io.github.shshdxk.data.specification;

import com.google.common.base.Preconditions;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class NotEqual<T> extends PathSpecification<T> {
    private final Object value;

    public NotEqual(String path, Object value) {
        super(path);
        Preconditions.checkArgument(value != null, "Use IsNull or IsNotNull for null object");
        this.value = value;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.notEqual(this.path(root), this.value);
    }
}