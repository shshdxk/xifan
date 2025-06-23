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

    /**
     * self
     * @return this
     */
    protected PredicateBuilder<T> self() {
        return this;
    }

    /**
     * constructor
     * @param root root
     * @param query query
     * @param cb criteriaBuilder
     */
    public PredicateBuilder(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        this.root = root;
        this.query = query;
        this.cb = cb;
    }

    /**
     * join
     * @param attribute attribute
     * @param joinType joinType
     * @return PredicateBuilder
     */
    public PredicateBuilder<T> join(String attribute, JoinType joinType) {
        this.joins.put(attribute, this.root.join(attribute, joinType));
        return this.self();
    }

    /**
     * like
     * @param attribute attribute
     * @param pattern pattern
     * @return PredicateBuilder
     */
    public PredicateBuilder<T> like(String attribute, String pattern) {
        this.predicates.add((new Like<T>(attribute, pattern)).toPredicate(this.root, this.query, this.cb));
        return this.self();
    }

    /**
     * eq
     * @param attribute attribute
     * @param value value
     * @return PredicateBuilder
     */
    public PredicateBuilder<T> eq(String attribute, Object value) {
        this.predicates.add(this.cb.equal(this.path(attribute), value));
        return this.self();
    }

    /**
     * after
     * @param attribute attribute
     * @param date date
     * @return PredicateBuilder
     */
    public PredicateBuilder<T> after(String attribute, Date date) {
        this.predicates.add(this.cb.greaterThan(this.path(attribute), date));
        return this.self();
    }

    /**
     * before
     * @param attribute attribute
     * @param date date
     * @return PredicateBuilder
     */
    public PredicateBuilder<T> before(String attribute, Date date) {
        this.predicates.add(this.cb.lessThan(this.path(attribute), date));
        return this.self();
    }

    /**
     * get path
     * @param attribute attribute
     * @param <F> field
     * @return path
     */
    protected <F> Path<F> path(String attribute) {
        Path<F> expr = null;
        String[] var3 = attribute.split("\\.");

        for (String field : var3) {
            if (expr == null) {
                expr = this.root.get(field);
            } else {
                expr = expr.get(field);
            }
        }

        return expr;
    }

    /**
     * toPredicate
     * @param root root
     * @param query query
     * @param cb criteriaBuilder
     * @return Predicate
     */
    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> noNulls = this.predicates.stream().filter(Objects::nonNull).collect(Collectors.toList());
        return noNulls.isEmpty() ? null : cb.and(Iterables.toArray(noNulls, Predicate.class));
    }
}
