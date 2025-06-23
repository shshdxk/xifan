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

    /**
     * Equals
     *
     * @param path  the path to compare
     * @param value the value to compare
     * @param <T>   the type of the entity
     * @return the specification
     */
    public static <T> Specification<T> eq(String path, Object value) {
        return new Equal<>(path, value);
    }

    /**
     * Not Equals
     *
     * @param path  the path to compare
     * @param value the value to compare
     * @param <T>   the type of the entity
     * @return the specification
     */
    public static <T> Specification<T> ne(String path, Object value) {
        return new NotEqual<>(path, value);
    }

    /**
     * Less Than
     *
     * @param path  the path to compare
     * @param value the value to compare
     * @param <T>   the type of the entity
     * @return the specification
     * @param <Y> the type
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> lt(String path, Y value) {
        return new Compare<>(path, Operator.lt, value);
    }

    /**
     * Less Than or Equals
     *
     * @param path  the path to compare
     * @param value the value to compare
     * @param <T>   the type of the entity
     * @return the specification
     * @param <Y> the type
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> le(String path, Y value) {
        return new Compare<>(path, Operator.le, value);
    }

    /**
     * Greater Than
     *
     * @param path  the path to compare
     * @param value the value to compare
     * @param <T>   the type of the entity
     * @return the specification
     * @param <Y> the type
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> gt(String path, Y value) {
        return new Compare<>(path, Operator.gt, value);
    }

    /**
     * Greater Than or Equals
     *
     * @param path  the path to compare
     * @param value the value to compare
     * @param <T>   the type of the entity
     * @return the specification
     * @param <Y> the type
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> ge(String path, Y value) {
        return new Compare<>(path, Operator.ge, value);
    }

    /**
     * Between
     * @param path the path to compare
     * @param min the min value to compare
     * @param max the max value to compare
     * @return the specification
     * @param <T> the type
     * @param <Y> the type
     */
    public static <T, Y extends Comparable<? super Y>> Specification<T> between(String path, Y min, Y max) {
        return new Between<>(path, min, max);
    }

    /**
     * Date Before
     * @param path the path to compare
     * @param date the date to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> dateBefore(String path, Date date) {
        return lt(path, date);
    }

    /**
     * Date After
     * @param path the path to compare
     * @param date the date to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> dateAfter(String path, Date date) {
        return gt(path, date);
    }

    /**
     * Date Between
     * @param path the path to compare
     * @param since the since date to compare
     * @param until the until date to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> dateBetween(String path, Date since, Date until) {
        return between(path, since, until);
    }

    /**
     * Not
     * @param spec the specification to negate
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> not(Specification<T> spec) {
        return new Not<>(spec);
    }

    /**
     * Join
     * @param paths the paths to join
     * @param joinType the join type
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> join(String[] paths, JoinType joinType) {
        return new Join<>(paths, joinType);
    }

    /**
     * Join
     * @param path the path to join
     * @param joinType the join type
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> join(String path, JoinType joinType) {
        return join(new String[]{path}, joinType);
    }

    /**
     * Join
     * @param path the path to join
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> join(String path) {
        return join(path, JoinType.INNER);
    }

    /**
     * Like
     * @param path the path to compare
     * @param pattern the pattern to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> like(String path, String pattern) {
        return like(path, pattern, MatchMode.ANYWHERE);
    }

    /**
     * Like
     * @param path the path to compare
     * @param pattern the pattern to compare
     * @param mode the match mode
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> like(String path, String pattern, MatchMode mode) {
        return new Like<>(path, pattern, mode, false);
    }

    /**
     * Like Ignore Case
     * @param path the path to compare
     * @param pattern the pattern to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> likeIgnoreCase(String path, String pattern) {
        return likeIgnoreCase(path, pattern, MatchMode.ANYWHERE);
    }

    /**
     * Like Ignore Case
     * @param path the path to compare
     * @param pattern the pattern to compare
     * @param mode the match mode
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> likeIgnoreCase(String path, String pattern, MatchMode mode) {
        return new Like<>(path, pattern, mode, true);
    }

    /**
     * Not Like
     * @param path the path to compare
     * @param pattern the pattern to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> notLike(String path, String pattern) {
        return notLike(path, pattern, MatchMode.ANYWHERE);
    }

    /**
     * Not Like
     * @param path the path to compare
     * @param pattern the pattern to compare
     * @param mode the match mode
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> notLike(String path, String pattern, MatchMode mode) {
        return new NotLike<>(path, pattern, mode, false);
    }

    /**
     * Not Like Ignore Case
     * @param path the path to compare
     * @param pattern the pattern to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> notLikeIgnoreCase(String path, String pattern) {
        return notLikeIgnoreCase(path, pattern, MatchMode.ANYWHERE);
    }

    /**
     * Not Like Ignore Case
     * @param path the path to compare
     * @param pattern the pattern to compare
     * @param mode the match mode
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> notLikeIgnoreCase(String path, String pattern, MatchMode mode) {
        return new NotLike<>(path, pattern, mode, true);
    }

    /**
     * Is Null
     * @param path the path to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> isNull(String path) {
        return new IsNull<>(path);
    }

    /**
     * Is Not Null
     * @param path the path to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> isNotNull(String path) {
        return new IsNotNull<>(path);
    }

    /**
     * Is True
     * @param path the path to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> isTrue(String path) {
        return eq(path, true);
    }

    /**
     * Is False
     * @param path the path to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> isFalse(String path) {
        return eq(path, false);
    }

    /**
     * In
     * @param path the path to compare
     * @param values the values to compare
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> in(String path, Object[] values) {
        return new In<>(path, values);
    }

    /**
     * And
     * @param specs the specifications to combine
     * @return the specification
     * @param <T> the type
     */
    @SafeVarargs
    public static <T> Specification<T> and(Specification<T>... specs) {
        return new Conjunction<>(specs);
    }

    /**
     *  And
     * @param specs the specifications to combine
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> and(Collection<Specification<T>> specs) {
        return new Conjunction<>(specs);
    }

    /**
     * Or
     * @param specs the specifications to combine
     * @return the specification
     * @param <T> the type
     */
    @SafeVarargs
    public static <T> Specification<T> or(Specification<T>... specs) {
        return new Disjunction<>(specs);
    }

    /**
     *  Or
     * @param specs the specifications to combine
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> or(Collection<Specification<T>> specs) {
        return new Disjunction<>(specs);
    }

    /**
     * Distinct
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> distinct() {
        return new Distinct<>();
    }

    /**
     * Order By
     * @param sort the sort to order by
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> orderBy(Sort sort) {
        return new OrderBy<>(sort);
    }

    /**
     * Order By
     * @param direction the direction to order by
     * @param path the path to order by
     * @return the specification
     * @param <T> the type
     */
    public static <T> Specification<T> orderBy(Sort.Direction direction, String path) {
        return orderBy(Sort.by(new Sort.Order(direction, path)));
    }

    /**
     *  Order By
     * @param orders the orders to order by
     * @param <T> the type
     * @return the specification
     */
    public static <T> Specification<T> orderBy(Sort.Order... orders) {
        return orderBy(Sort.by(orders));
    }
}
