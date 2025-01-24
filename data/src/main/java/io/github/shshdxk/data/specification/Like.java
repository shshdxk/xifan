package io.github.shshdxk.data.specification;


import com.google.common.base.Strings;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.Objects;

/**
 * Like specification
 * @param <T> beanType
 */
public class Like<T> extends PathSpecification<T> {

    /**
     * pattern
     */
    protected String pattern;
    private final boolean ignoreCase;

    /**
     * constructor
     * @param path field name
     * @param arg pattern
     */
    public Like(String path, String arg) {
        this(path, arg, MatchMode.ANYWHERE, true);
    }

    /**
     * constructor
     * @param path field name
     * @param arg pattern
     * @param mode like mode
     */
    public Like(String path, String arg, MatchMode mode) {
        this(path, arg, mode, true);
    }

    /**
     * constructor
     * @param path field name
     * @param arg pattern
     * @param mode like mode
     * @param ignoreCase ignore case
     */
    public Like(String path, String arg, MatchMode mode, boolean ignoreCase) {
        super(path);
        this.ignoreCase = ignoreCase;
        if (arg != null) {
            switch (mode) {
                case EXACT:
                    this.pattern = arg;
                    break;
                case START:
                    this.pattern = arg + "%";
                    break;
                case END:
                    this.pattern = "%" + arg;
                    break;
                case ANYWHERE:
                    this.pattern = "%" + arg + "%";
            }
        }

    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        if (Strings.isNullOrEmpty(this.pattern)) {
            return null;
        } else {
            return this.ignoreCase ? builder.like(builder.upper(this.path(root)), this.pattern.toUpperCase()) : builder.like(this.path(root), this.pattern);
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.path, this.pattern, this.ignoreCase});
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!super.equals(other)) {
            return false;
        } else {
            Like<?> rhs = (Like<?>)other;
            return Objects.equals(this.pattern, rhs.pattern) && Objects.equals(this.ignoreCase, rhs.ignoreCase);
        }
    }
}
