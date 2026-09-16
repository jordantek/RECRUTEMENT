package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class QRValidationRequest {
    @NotBlank(message = "QR data is required")
    private String qrData;
    private String deviceId;
    private String appVersion;
}