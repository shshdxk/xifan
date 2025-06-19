package io.github.shshdxk.data.specification;


import com.google.common.base.Strings;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/**
 * Not like specification
 * @param <T> beanType
 */
public class NotLike<T> extends PathSpecification<T> {
    /**
     * pattern
     */
    protected String pattern;
    /**
     * ignore case
     */
    private final boolean ignoreCase;

    /**
     * constructor
     * @param path field name
     * @param arg pattern
     */
    public NotLike(String path, String arg) {
        this(path, arg, MatchMode.ANYWHERE);
    }

    /**
     * constructor
     * @param path field name
     * @param arg pattern
     * @param mode like mode
     */
    public NotLike(String path, String arg, MatchMode mode) {
        this(path, arg, mode, true);
    }

    /**
     * constructor
     * @param path field name
     * @param arg pattern
     * @param mode like mode
     * @param ignoreCase ignore case
     */
    public NotLike(String path, String arg, MatchMode mode, boolean ignoreCase) {
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
            return this.ignoreCase ? builder.notLike(builder.upper(this.path(root)), this.pattern.toUpperCase()) : builder.notLike(this.path(root), this.pattern);
        }
    }
}
