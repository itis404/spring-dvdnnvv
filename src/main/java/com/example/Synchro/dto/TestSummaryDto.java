package com.example.Synchro.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestSummaryDto {
    private Long id;
    private String name;
    private String description;
    private boolean required;
}