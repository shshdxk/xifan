package io.github.shshdxk.data.specification;


import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * PredicateBuilder specification
 * @param <T> beanType
 */
public class PredicateBuilder<T> implements Specification<T> {
    /**
     * joins
     */
    private final Map<String, Join<?, ?>> joins = Maps.newHashMap();
    /**
     * root
     */
    private final Root<T> root;
    /**
     * criteriaBuilder
     */
    private final CriteriaQuery<?> query;
    /**
     * criteriaBuilder
     */
    private final CriteriaBuilder cb;
    /**
     * predicates
     */
    private final List<Predicate> predicates = Lists.newArrayList();

    protected PredicateBuilder<T> self() {
        return this;
    }

    public PredicateBuilder(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        this.root = root;
        this.query = query;
        this.cb = cb;
    }

    public PredicateBuilder<T> join(String attribute, JoinType joinType) {
        this.joins.put(attribute, this.root.join(attribute, joinType));
        return this.self();
    }

    public PredicateBuilder<T> like(String attribute, String pattern) {
        this.predicates.add((new Like<T>(attribute, pattern)).toPredicate(this.root, this.query, this.cb));
        return this.self();
    }

    public PredicateBuilder<T> eq(String attribute, Object value) {
        this.predicates.add(this.cb.equal(this.path(attribute), value));
        return this.self();
    }

    public PredicateBuilder<T> after(String attribute, Date date) {
        this.predicates.add(this.cb.greaterThan(this.path(attribute), date));
        return this.self();
    }

    public PredicateBuilder<T> before(String attribute, Date date) {
        this.predicates.add(this.cb.lessThan(this.path(attribute), date));
        return this.self();
    }

    protected <F> Path<F> path(String attribute) {
        Path<F> expr = null;
        String[] var3 = attribute.split("\\.");
        int var4 = var3.length;

        for (String field : var3) {
            if (expr == null) {
                expr = this.root.get(field);
            } else {
                expr = expr.get(field);
            }
        }

        return expr;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> noNulls = this.predicates.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return noNulls.isEmpty() ? null : cb.and(Iterables.toArray(noNulls, Predicate.class));
    }
}
