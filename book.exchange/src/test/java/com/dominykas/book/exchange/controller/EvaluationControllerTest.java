package com.dominykas.book.exchange.controller;

import com.dominykas.book.exchange.dto.evaluationDTO.EvaluationResponseDTO;
import com.dominykas.book.exchange.dto.evaluationDTO.ModelMetricsDTO;
import com.dominykas.book.exchange.service.EvaluationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluationControllerTest {

    @Mock
    private EvaluationService evaluationService;

    @InjectMocks
    private EvaluationController controller;

    @Test
    void run_ShouldReturnEvaluationResponse() {
        List<ModelMetricsDTO> metrics = List.of(
                new ModelMetricsDTO("model", 0.9, 0.8, 0.85, 100)
        );

        when(evaluationService.runEvaluation()).thenReturn(metrics);

        EvaluationResponseDTO result = controller.run();

        assertThat(result).isNotNull();
        verify(evaluationService).runEvaluation();
    }
}