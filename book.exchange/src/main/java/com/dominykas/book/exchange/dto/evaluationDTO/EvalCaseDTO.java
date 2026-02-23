package com.dominykas.book.exchange.dto.evaluationDTO;

import lombok.Data;

import java.util.List;

@Data
public class EvalCaseDTO {
    private String query;
    private List<Long> relevant;
}