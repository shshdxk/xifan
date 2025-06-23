package io.github.shshdxk.data.filter;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import io.github.shshdxk.data.specification.Specifications;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 */
@Getter
public class ContainsFilter<T> implements SearchFilter<T> {

    /**
     * 字段
     */
    private final Set<String> fields;
    /**
     * 模式
     */
    private final Set<String> pattern;

    public ContainsFilter(Set<String> fields, Set<String> pattern) {
        this.fields = fields;
        this.pattern = pattern;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public Specification<T> toSpecification() {
        List<Specification<T>> list = Lists.newArrayList();
        fields.forEach(field -> {
            for (String p : pattern) {
                list.add(Specifications.likeIgnoreCase(field, p));
            }
        });

        return Specifications.or(list);
    }

    @Override
    public String toExpression() {
        return Joiner.on(" ").join(pattern);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ContainsFilter)) {
            return false;
        }

        ContainsFilter rhs = (ContainsFilter) obj;
        return Objects.equals(fields, rhs.fields)
            && Objects.equals(pattern, rhs.pattern);
    }

    @Override
    public boolean isValid() {
        return !fields.isEmpty() && !pattern.isEmpty();
    }

}
