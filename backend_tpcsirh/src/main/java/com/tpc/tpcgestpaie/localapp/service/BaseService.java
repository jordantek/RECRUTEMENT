package com.tpc.tpcgestpaie.localapp.service;
import com.tpc.tpcgestpaie.localapp.utiliterface.AuditableEntity;
import com.tpc.tpcgestpaie.localapp.utiliterface.SoftDeletableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public abstract class BaseService<T extends AuditableEntity<ID> & SoftDeletableEntity, ID> {

    protected final JpaRepository<T, ID> repository;

    // À injecter selon ton système d'authentification
    protected abstract Long getCurrentUserId(); // À surcharger dans les services concrets

    public BaseService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    public List<T> getAll() {
        return repository.findAll().stream()
                .filter(entity -> entity.getDeletedAt() == null)
                .toList();
    }

    public T getById(ID id) {
        Optional<T> optional = repository.findById(id);

        if (optional.isEmpty() || optional.get().getDeletedAt() != null) {
            throw new RuntimeException("Entité non trouvée ou supprimée");
        }

        return optional.get();
    }

    public T create(T entity) {
        LocalDateTime now = LocalDateTime.now();
        Long userId = getCurrentUserId();

        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        entity.setCreatedBy(userId);
        entity.setUpdatedBy(userId);

        return repository.save(entity);
    }

    public T update(ID id, T updatedEntity) {
        T existing = getById(id);
        Long userId = getCurrentUserId();

        updatedEntity.setId(existing.getId()); // On conserve l'ID
        updatedEntity.setCreatedAt(existing.getCreatedAt());
        updatedEntity.setCreatedBy(existing.getCreatedBy());
        updatedEntity.setUpdatedAt(LocalDateTime.now());
        updatedEntity.setUpdatedBy(userId);

        return repository.save(updatedEntity);
    }

    public void delete(ID id) {
        T entity = getById(id);

        if (entity.getDeletedAt() != null) {
            throw new RuntimeException("Déjà supprimé");
        }

        entity.setDeletedAt(LocalDateTime.now());
        entity.setDeletedBy(getCurrentUserId());

        repository.save(entity);
    }
}
