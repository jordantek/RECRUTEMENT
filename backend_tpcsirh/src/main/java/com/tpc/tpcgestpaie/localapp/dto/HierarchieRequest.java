package com.tpc.tpcgestpaie.localapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HierarchieRequest {

    private Long companyId;
    private Long contratEmployeId;
    private List<SuperieurDTO> superieurs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuperieurDTO {

        private Long contratSuperieurId;
//        private Long employeSuperieurId;
        private Integer ordre;
    }
}