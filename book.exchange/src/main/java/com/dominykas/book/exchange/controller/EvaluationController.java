package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.evaluationDTO.EvaluationResponseDTO;
import com.dominykas.book.exchange.dto.evaluationDTO.ModelMetricsDTO;
import com.dominykas.book.exchange.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @GetMapping("/run")
    public EvaluationResponseDTO run() {
        List<ModelMetricsDTO> results = evaluationService.runEvaluation();
        return new EvaluationResponseDTO(results);
    }
}