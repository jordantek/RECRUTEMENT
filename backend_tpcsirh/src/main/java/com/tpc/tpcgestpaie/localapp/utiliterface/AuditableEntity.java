package com.tpc.tpcgestpaie.localapp.utiliterface;

import java.time.LocalDateTime;

public interface AuditableEntity<ID> extends SoftDeletableEntity {

    ID getId();
    void setId(ID id);

    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();
    void setUpdatedAt(LocalDateTime updatedAt);

    Long getCreatedBy();
    void setCreatedBy(Long createdBy);

    Long getUpdatedBy();
    void setUpdatedBy(Long updatedBy);

    Long getDeletedBy();
    void setDeletedBy(Long deletedBy);
}
