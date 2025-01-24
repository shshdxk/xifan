package io.github.shshdxk.data.specification;


import com.google.common.collect.Iterables;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Disjunction specification
 * @param <T> beanType
 */
public class Disjunction<T> implements Specification<T> {
    private final Collection<Specification<T>> specs;

    /**
     * or
     * @param specs Specifications
     */
    @SafeVarargs
    public Disjunction(Specification<T>... specs) {
        this(Arrays.asList(specs));
    }

    /**
     * or
     * @param specs Specifications
     */
    public Disjunction(Collection<Specification<T>> specs) {
        this.specs = specs;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (this.specs.isEmpty()) {
            return null;
        } else {
            Specification<T> combinedSpecs;
            if (this.specs.size() == 1) {
                combinedSpecs = Iterables.getFirst(this.specs, null);
                return combinedSpecs == null ? null : combinedSpecs.toPredicate(root, query, cb);
            } else {
                combinedSpecs = null;
                Iterator<Specification<T>> var5 = this.specs.iterator();

                Specification<T> spec;
                while(var5.hasNext()) {
                    spec = var5.next();
                    if (spec instanceof Join) {
                        spec.toPredicate(root, query, cb);
                    }
                }

                var5 = this.specs.iterator();

                while(var5.hasNext()) {
                    spec = var5.next();
                    if (!(spec instanceof Join)) {
                        if (combinedSpecs == null) {
                            combinedSpecs = Specification.where(spec);
                        } else {
                            combinedSpecs = combinedSpecs.or(spec);
                        }
                    }
                }

                return combinedSpecs == null ? null : combinedSpecs.toPredicate(root, query, cb);
            }
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.specs);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            Disjunction<?> other = (Disjunction<?>)obj;
            return Objects.equals(this.specs, other.specs);
        }
    }
}
