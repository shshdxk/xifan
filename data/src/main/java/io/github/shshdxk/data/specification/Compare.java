package io.github.shshdxk.data.specification;


import com.google.common.base.Objects;
import jakarta.persistence.criteria.*;

public class Compare<T, Y extends Comparable<? super Y>> extends PathSpecification<T> {
    private final Operator op;
    private final Y value;

    public Compare(String path, Operator op, Y value) {
        super(path);
        this.op = op;
        this.value = value;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (this.value == null) {
            return null;
        } else {
            Path<Y> path = this.path(root);
            return switch (this.op) {
                case gt -> cb.greaterThan(path, this.value);
                case ge -> cb.greaterThanOrEqualTo(path, this.value);
                case lt -> cb.lessThan(path, this.value);
                case le -> cb.lessThanOrEqualTo(path, this.value);
                default -> null;
            };
        }
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.path, this.op, this.value});
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Compare)) {
            return false;
        } else {
            Compare<T, Y> rhs = (Compare)other;
            return Objects.equal(this.path, rhs.path) && Objects.equal(this.op, rhs.op) && Objects.equal(this.value, rhs.value);
        }
    }
}
