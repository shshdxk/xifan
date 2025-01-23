package io.github.shshdxk.data.specification;


import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public abstract class PathSpecification<T> implements Specification<T> {
    protected String path;

    public PathSpecification(String path) {
        this.path = path;
    }

    protected <F> Path<F> path(Root<T> root) {
        Path<F> expr = null;
        String[] parts = this.path.split("\\.");
        if (parts.length == 1) {
            return root.get(this.path);
        } else {
            Join<T, ?> join = null;

            for(int i = 0; i < parts.length - 1; ++i) {
                join = this.join(root, join, parts[i]);
            }

            if (join == null) {
                int var6 = parts.length;

                for (String field : parts) {
                    expr = Objects.requireNonNullElse(expr, root).get(field);
                }
            } else {
                expr = join.get(parts[parts.length - 1]);
            }

            return expr;
        }
    }

    private Join<T, ?> join(Root<T> root, Join<T, ?> join, String field) {
        Set<Join<T, ?>> joins = root.getJoins();
        Iterator<Join<T, ?>> var5 = joins.iterator();

        Join<T, ?> each;
        do {
            if (!var5.hasNext()) {
                Join<T, ?> j = join == null ? root.join(field) : join.join(field);
                j.alias(field);
                return j;
            }

            each = var5.next();
        } while(!Objects.equals(each.getAlias(), field));

        return each;
    }

    public int hashCode() {
        return Objects.hashCode(this.path);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            PathSpecification<?> other = (PathSpecification<?>)obj;
            return Objects.equals(this.path, other.path);
        }
    }
}
