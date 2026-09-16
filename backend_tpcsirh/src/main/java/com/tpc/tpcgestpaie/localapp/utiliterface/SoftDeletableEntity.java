package com.tpc.tpcgestpaie.localapp.utiliterface;

import java.time.LocalDateTime;

public interface SoftDeletableEntity {
    LocalDateTime getDeletedAt();
    void setDeletedAt(LocalDateTime deletedAt);
}
