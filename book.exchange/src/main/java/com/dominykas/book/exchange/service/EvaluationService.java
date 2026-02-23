package com.dominykas.book.exchange.service;

import com.dominykas.book.exchange.dto.evaluationDTO.EvalCaseDTO;
import com.dominykas.book.exchange.dto.evaluationDTO.ModelMetricsDTO;
import com.dominykas.book.exchange.repository.PublicationSearchRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EmbeddingService embeddingService;
    private final PublicationSearchRepository publicationSearchRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // default models to test
    private static final List<String> DEFAULT_MODELS = List.of("bert", "distilbert", "roberta");

    public List<ModelMetricsDTO> runEvaluation() {
        return runEvaluation(DEFAULT_MODELS);
    }

    public List<ModelMetricsDTO> runEvaluation(List<String> modelKeys) {
        List<EvalCaseDTO> cases = loadGroundTruth();

        List<ModelMetricsDTO> out = new ArrayList<>();
        for (String modelKey : modelKeys) {
            out.add(evaluateModel(modelKey, cases));
        }
        return out;
    }

    private ModelMetricsDTO evaluateModel(String modelKey, List<EvalCaseDTO> cases) {
        double recall5Sum = 0.0;
        double recall10Sum = 0.0;
        double mrr10Sum = 0.0;

        for (EvalCaseDTO c : cases) {
            String query = c.getQuery().trim();
            Set<Long> relevant = new HashSet<>(c.getRelevant());

            // embed query for THIS model
            List<Double> qVec = embeddingService.embed(query, modelKey);

            // get ranked results (no threshold) top 10
            List<Long> top10Ids = publicationSearchRepository.searchTopK(qVec, modelKey, 10)
                    .stream()
                    .map(row -> row.publication().getId())
                    .collect(Collectors.toList());

            // recall@5, recall@10
            recall5Sum += recallAtK(relevant, top10Ids, 5);
            recall10Sum += recallAtK(relevant, top10Ids, 10);

            // mrr@10
            mrr10Sum += reciprocalRank(relevant, top10Ids, 10);
        }

        int n = cases.size();
        return new ModelMetricsDTO(
                modelKey,
                round4(recall5Sum / n),
                round4(recall10Sum / n),
                round4(mrr10Sum / n),
                n
        );
    }

    private double recallAtK(Set<Long> relevant, List<Long> ranked, int k) {
        if (relevant.isEmpty()) return 0.0;
        int end = Math.min(k, ranked.size());
        Set<Long> topK = new HashSet<>(ranked.subList(0, end));
        int hits = 0;
        for (Long r : relevant) {
            if (topK.contains(r)) hits++;
        }
        return (double) hits / (double) relevant.size();
    }

    private double reciprocalRank(Set<Long> relevant, List<Long> ranked, int k) {
        int end = Math.min(k, ranked.size());
        for (int i = 0; i < end; i++) {
            if (relevant.contains(ranked.get(i))) {
                return 1.0 / (i + 1);
            }
        }
        return 0.0;
    }

    private List<EvalCaseDTO> loadGroundTruth() {
        try {
            ClassPathResource res = new ClassPathResource("eval/ground_truth.json");
            try (InputStream is = res.getInputStream()) {
                return objectMapper.readValue(is, new TypeReference<List<EvalCaseDTO>>() {});
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load eval/ground_truth.json", e);
        }
    }

    private static double round4(double x) {
        return Math.round(x * 10000.0) / 10000.0;
    }
}