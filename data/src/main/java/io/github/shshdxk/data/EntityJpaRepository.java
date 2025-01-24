package io.github.shshdxk.data;


import org.springframework.data.domain.Persistable;

/**
 * the interface of the repository for entity
 * @param <T> the type of the entity to handle
 */
public interface EntityJpaRepository<T extends Persistable<Long>> extends FilterableJpaRepository<T, Long> {
}
