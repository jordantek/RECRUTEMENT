package com.tpc.tpcgestpaie.localapp.dto.rh;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class JournalRhDto {

    private Long id;
    private LocalDate date;
    private String contenu;
    private CategorieEvenementDto categorieEvenement;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public JournalRhDto() {}

    // constructeur complet (optionnel)
    public JournalRhDto(Long id, LocalDate date, String contenu, CategorieEvenementDto categorieEvenement,
                        LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.date = date;
        this.contenu = contenu;
        this.categorieEvenement = categorieEvenement;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    // getters et setters
    // ...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public CategorieEvenementDto getCategorieEvenement() {
        return categorieEvenement;
    }

    public void setCategorieEvenement(CategorieEvenementDto categorieEvenement) {
        this.categorieEvenement = categorieEvenement;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
