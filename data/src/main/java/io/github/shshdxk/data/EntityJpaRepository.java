package io.github.shshdxk.data;


import org.springframework.data.domain.Persistable;

public interface EntityJpaRepository<T extends Persistable<Long>> extends FilterableJpaRepository<T, Long> {
}
