package io.github.shshdxk.data.filter;

import io.github.shshdxk.data.specification.Operator;
import io.github.shshdxk.data.specification.Specifications;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

/**
 */
@Getter
public class GenericFilter<T> implements SearchFilter<T> {
    private final String field;
    private final Operator operator;
    private final Object value;

    public GenericFilter(String field, Operator operator, Object value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Specification<T> toSpecification() {
        return switch (operator) {
            case eq -> Specifications.eq(field, value);
            case le -> Specifications.le(field, (Comparable) value);
            case lt -> Specifications.lt(field, (Comparable) value);
            case ge -> Specifications.ge(field, (Comparable) value);
            case gt -> Specifications.gt(field, (Comparable) value);
        };

    }

    @Override
    public String toExpression() {
        return field + operator.getOp() + value.toString();
    }

    @Override
    public boolean isValid() {
        return field != null && operator != null && value != null;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof GenericFilter)) {
            return false;
        }

        GenericFilter rhs = (GenericFilter) other;
        return Objects.equals(field, rhs.field)
                && Objects.equals(operator, rhs.operator)
                && Objects.equals(value, rhs.value);
    }

    public int hashCode() {
        return Objects.hash(field, operator, value);
    }

    public String toString() {
        return toExpression();
    }

}
