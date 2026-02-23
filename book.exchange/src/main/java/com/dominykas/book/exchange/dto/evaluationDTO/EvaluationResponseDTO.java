package com.dominykas.book.exchange.dto.evaluationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EvaluationResponseDTO {
    private List<ModelMetricsDTO> results;
}