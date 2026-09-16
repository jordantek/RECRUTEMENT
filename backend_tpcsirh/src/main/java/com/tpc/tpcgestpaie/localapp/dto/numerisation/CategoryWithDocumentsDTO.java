package com.tpc.tpcgestpaie.localapp.dto.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CategoryWithDocumentsDTO {
    private Long categoryId;
    private String categoryName;
    private List<SubCategoryWithDocumentsDTO> subCategories;
}