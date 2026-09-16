package com.tpc.tpcgestpaie.localapp.dto.setup;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallationStatus {
    private boolean installationRequired;
    private boolean hasDefaultUser;
    private String defaultUsername;
    private String message;

    public boolean isInstallationRequired() {
        return installationRequired;
    }

    public void setInstallationRequired(boolean installationRequired) {
        this.installationRequired = installationRequired;
    }

    public boolean isHasDefaultUser() {
        return hasDefaultUser;
    }

    public void setHasDefaultUser(boolean hasDefaultUser) {
        this.hasDefaultUser = hasDefaultUser;
    }

    public String getDefaultUsername() {
        return defaultUsername;
    }

    public void setDefaultUsername(String defaultUsername) {
        this.defaultUsername = defaultUsername;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}