package io.github.shshdxk.data.filter;

import io.github.shshdxk.data.specification.Specifications;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 */
public class OrFilter<T> implements SearchFilter<T> {

    /**
     * 条件
     */
    private final List<SearchFilter<T>> filters;

    public OrFilter(List<SearchFilter<T>> filters) {
        this.filters = filters;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public Specification<T> toSpecification() {
        if (filters == null) {
            return null;
        }

        return Specifications.or(filters.stream().map(SearchFilter::toSpecification).collect(Collectors.toList()));
    }

    @Override
    public String toExpression() {
        if (filters == null) {
            return "";
        } else {
            return "-" + filters.stream().map(item -> item.toSpecification().toString()).collect(Collectors.joining("-"));

        }
    }

    @Override
    public boolean isValid() {
        for (SearchFilter<T> searchFilter : filters) {
            if (!searchFilter.isValid()) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof OrFilter)) {
            return false;
        }

        OrFilter<T> rhs = (OrFilter<T>) obj;
        return Objects.equals(filters, rhs.filters);
    }
}
