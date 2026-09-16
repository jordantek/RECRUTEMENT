package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedDocument {
    private String filePath;
    private String fileType;
    private String qrCodeBase64;
    private Integer signaturesRemoved;
    private Integer stampsRemoved;
    private Integer pagesProcessed;
    private String status;
    private Map<String, Object> metadata;
}
