package io.github.shshdxk.data.filter;

import org.springframework.data.jpa.domain.Specification;

import java.io.Serializable;

/**
 */
public interface SearchFilter<T> extends Serializable {

    String getName();

    Specification<T> toSpecification();

    String toExpression();

    boolean isValid();
}
