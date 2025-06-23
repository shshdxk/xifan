package io.github.shshdxk.data.filter;

import com.google.common.collect.Lists;
import io.github.shshdxk.data.specification.Specifications;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 */
@Getter
@Setter
public class CompositeFilter<T> implements SearchFilter<T> {

    /**
     * 逻辑条件
     */
    private List<SearchFilter<T>> filters = Lists.newArrayList();
    /**
     * 逻辑运算符
     */
    private final Logic logic;

    public CompositeFilter(Logic logic) {
        this.logic = logic;
    }

    @Override
    public String getName() {
        return "composite-filter";
    }

    public void addFilter(SearchFilter<T> filter) {
        if (filter == null) {
            return;
        }

        filters.add(filter);
    }

    public void removeFilter(SearchFilter<T> filter) {
        if (filter == null) {
            return;
        }
        filters.remove(filter);
    }

    public boolean isEmpty() {
        return filters.isEmpty();
    }

    @Override
    public Specification<T> toSpecification() {
        if (filters.isEmpty()) {
            return null;
        }

        if (logic == Logic.AND) {
            return Specifications.and(filters.stream()
                    .map(SearchFilter::toSpecification)
                    .collect(Collectors.toList()));
        } else {
            return Specifications.or(filters.stream()
                    .map(SearchFilter::toSpecification)
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public String toExpression() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < filters.size(); i++) {
            SearchFilter<T> filter = filters.get(i);
            sb.append(filter.toExpression());
            if (i < filters.size() - 1) {
                sb.append(" ").append(logic.name());
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isValid() {
        for (SearchFilter<T> each : filters) {
            if (!each.isValid()) {
                return false;
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object rhs) {
        if (this == rhs) {
            return true;
        }

        if (!(rhs instanceof CompositeFilter)) {
            return false;
        }

        CompositeFilter<T> other = (CompositeFilter<T>) rhs;
        return Objects.equals(logic, other.logic)
                && Objects.equals(filters, other.filters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logic, filters);
    }
}
