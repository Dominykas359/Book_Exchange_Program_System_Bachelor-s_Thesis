package com.dominykas.book.exchange.dto.evaluationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModelMetricsDTO {
    private String modelKey;
    private double recallAt5;
    private double recallAt10;
    private double mrrAt10;
    private int queries;
}