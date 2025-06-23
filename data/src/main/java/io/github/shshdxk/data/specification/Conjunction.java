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
 * Conjunction specification
 * @param <T> beanType
 */
public class Conjunction<T> implements Specification<T> {
    /**
     * Specifications
     */
    private final Collection<Specification<T>> specs;

    /**
     * and
     * @param innerSpecs Specifications
     */
    @SafeVarargs
    public Conjunction(Specification<T>... innerSpecs) {
        this(Arrays.asList(innerSpecs));
    }

    /**
     * and
     * @param innerSpecs Specifications
     */
    public Conjunction(Collection<Specification<T>> innerSpecs) {
        this.specs = innerSpecs;
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
                            combinedSpecs = spec;
                        } else {
                            combinedSpecs = combinedSpecs.and(spec);
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
            Conjunction<?> other = (Conjunction<?>)obj;
            return Objects.equals(this.specs, other.specs);
        }
    }
}
