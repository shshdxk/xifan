package io.github.shshdxk.data.specification;


import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.Date;

/**
 * Specifications
 */
public class Specifications {
    public Specifications() {
    }

    public static <T> Specification<T> eq(String path, Object value) {
        return new Equal<>(path, value);
    }

    public static <T> Specification<T> ne(String path, Object value) {
        return new NotEqual<>(path, value);
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> lt(String path, Y value) {
        return new Compare<>(path, Operator.lt, value);
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> le(String path, Y value) {
        return new Compare<>(path, Operator.le, value);
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> gt(String path, Y value) {
        return new Compare<>(path, Operator.gt, value);
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> ge(String path, Y value) {
        return new Compare<>(path, Operator.ge, value);
    }

    public static <T, Y extends Comparable<? super Y>> Specification<T> between(String path, Y min, Y max) {
        return new Between<>(path, min, max);
    }

    public static <T> Specification<T> dateBefore(String path, Date date) {
        return lt(path, date);
    }

    public static <T> Specification<T> dateAfter(String path, Date date) {
        return gt(path, date);
    }

    public static <T> Specification<T> dateBetween(String path, Date since, Date until) {
        return between(path, since, until);
    }

    public static <T> Specification<T> not(Specification<T> spec) {
        return new Not<>(spec);
    }

    public static <T> Specification<T> join(String[] paths, JoinType joinType) {
        return new Join<>(paths, joinType);
    }

    public static <T> Specification<T> join(String path, JoinType joinType) {
        return join(new String[]{path}, joinType);
    }

    public static <T> Specification<T> join(String path) {
        return join(path, JoinType.INNER);
    }

    public static <T> Specification<T> like(String path, String pattern) {
        return like(path, pattern, MatchMode.ANYWHERE);
    }

    public static <T> Specification<T> like(String path, String pattern, MatchMode mode) {
        return new Like<>(path, pattern, mode, false);
    }

    public static <T> Specification<T> likeIgnoreCase(String path, String pattern) {
        return likeIgnoreCase(path, pattern, MatchMode.ANYWHERE);
    }

    public static <T> Specification<T> likeIgnoreCase(String path, String pattern, MatchMode mode) {
        return new Like<>(path, pattern, mode, true);
    }

    public static <T> Specification<T> notLike(String path, String pattern) {
        return notLike(path, pattern, MatchMode.ANYWHERE);
    }

    public static <T> Specification<T> notLike(String path, String pattern, MatchMode mode) {
        return new NotLike<>(path, pattern, mode, false);
    }

    public static <T> Specification<T> notLikeIgnoreCase(String path, String pattern) {
        return notLikeIgnoreCase(path, pattern, MatchMode.ANYWHERE);
    }

    public static <T> Specification<T> notLikeIgnoreCase(String path, String pattern, MatchMode mode) {
        return new NotLike<>(path, pattern, mode, true);
    }

    public static <T> Specification<T> isNull(String path) {
        return new IsNull<>(path);
    }

    public static <T> Specification<T> isNotNull(String path) {
        return new IsNotNull<>(path);
    }

    public static <T> Specification<T> isTrue(String path) {
        return eq(path, true);
    }

    public static <T> Specification<T> isFalse(String path) {
        return eq(path, false);
    }

    public static <T> Specification<T> in(String path, Object[] values) {
        return new In<>(path, values);
    }

    @SafeVarargs
    public static <T> Specification<T> and(Specification<T>... specs) {
        return new Conjunction<>(specs);
    }

    public static <T> Specification<T> and(Collection<Specification<T>> specs) {
        return new Conjunction<>(specs);
    }

    @SafeVarargs
    public static <T> Specification<T> or(Specification<T>... specs) {
        return new Disjunction<>(specs);
    }

    public static <T> Specification<T> or(Collection<Specification<T>> specs) {
        return new Disjunction<>(specs);
    }

    public static <T> Specification<T> distinct() {
        return new Distinct<>();
    }

    public static <T> Specification<T> orderBy(Sort sort) {
        return new OrderBy<>(sort);
    }

    public static <T> Specification<T> orderBy(Sort.Direction direction, String path) {
        return orderBy(Sort.by(new Sort.Order(direction, path)));
    }

    public static <T> Specification<T> orderBy(Sort.Order... orders) {
        return orderBy(Sort.by(orders));
    }
}
