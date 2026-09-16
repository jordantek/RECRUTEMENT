package com.tpc.tpcgestpaie.localapp.dto.util;

import com.tpc.tpcgestpaie.localapp.model.util.EmailConfig;

import java.time.LocalDateTime;

public class EmailConfigDTO {

    private Long id;
    private String host;
    private String port;
    private String protocole;
    private String mailFrom;
    private String password;

    private Long addedById;   // on expose juste l'id de l'user, pas tout l'objet

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocole() {
        return protocole;
    }

    public void setProtocole(String protocole) {
        this.protocole = protocole;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAddedById() {
        return addedById;
    }

    public void setAddedById(Long addedById) {
        this.addedById = addedById;
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

    public EmailConfigDTO toDTO(EmailConfig entity) {
        EmailConfigDTO dto = new EmailConfigDTO();
        dto.setId(entity.getId());
        dto.setHost(entity.getHost());
        dto.setPort(entity.getPort());
        dto.setProtocole(entity.getProtocole());
        dto.setMailFrom(entity.getMailFrom());
        dto.setPassword(entity.getPassword());
        dto.setAddedById(entity.getAdded_by() != null ? entity.getAdded_by().getId() : null);
        dto.setCreatedAt(entity.getCreated_at());
        dto.setUpdatedAt(entity.getUpdated_at());
        dto.setDeletedAt(entity.getDeleted_at());
        return dto;
    }
}
