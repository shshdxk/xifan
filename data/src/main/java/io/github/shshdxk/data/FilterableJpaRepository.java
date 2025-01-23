package io.github.shshdxk.data;

import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.io.Serializable;

public interface FilterableJpaRepository<T extends Persistable<ID>, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

//    @NotNull
//    default T getReferenceById(@NotNull ID id) {
//        return this.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found: " + id));
//    }
}
