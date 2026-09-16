package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

// DocumentProcessResponse.java
@Data
public class DocumentProcessResponse {
    private Long documentId;
    private String digitalId;
    private String processedFileUrl;
    private String qrCodeImage;
    private String originalFileName;
    private Integer signaturesRemoved;
    private Integer stampsRemoved;
    private LocalDateTime processedAt;
    private Map<String, Object> digitalData;
}