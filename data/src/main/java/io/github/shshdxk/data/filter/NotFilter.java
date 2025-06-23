package io.github.shshdxk.data.filter;

import io.github.shshdxk.data.specification.Specifications;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

/**
 *
 */
public class NotFilter<T> implements SearchFilter<T> {

    /**
     * 逻辑NOT
     */
    private final SearchFilter<T> filter;

    public NotFilter(SearchFilter<T> filter) {
        this.filter = filter;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public Specification<T> toSpecification() {
        if (filter == null) {
            return null;
        }

        return Specifications.not(filter.toSpecification());
    }

    @Override
    public String toExpression() {
        if (filter == null) {
            return "";
        } else {
            return "-" + filter.toExpression();
        }
    }

    @Override
    public boolean isValid() {
        return filter.isValid();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof NotFilter)) {
            return false;
        }

        NotFilter<T> rhs = (NotFilter<T>) obj;
        return Objects.equals(filter, rhs.filter);
    }
}
