package io.github.shshdxk.data.specification;


import com.google.common.base.Objects;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.List;

public class Join<T> implements Specification<T> {
    private final List<String> pathsToFetch;
    private final JoinType joinType;

    public Join(String path) {
        this(new String[]{path}, JoinType.INNER);
    }

    public Join(String[] pathsToFetch, JoinType joinType) {
        this.pathsToFetch = Arrays.asList(pathsToFetch);
        this.joinType = joinType;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

        for (String path : this.pathsToFetch) {
            boolean found = false;

            for (jakarta.persistence.criteria.Join<T, ?> join : root.getJoins()) {
                if (join.getAlias().equalsIgnoreCase(path)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                root.join(path, this.joinType).alias(path);
            }
        }

        return null;
    }

    public int hashCode() {
        return Objects.hashCode(this.pathsToFetch, this.joinType);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Join other = (Join)obj;
            return Objects.equal(this.pathsToFetch, other.pathsToFetch) && Objects.equal(this.joinType, other.joinType);
        }
    }
}
