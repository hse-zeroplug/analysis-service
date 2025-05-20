package com.aaalace.analysisservice.application.service;

import com.aaalace.analysisservice.application.in.IAnalysisService;
import com.aaalace.analysisservice.domain.model.Table;
import com.aaalace.analysisservice.domain.model.TextStatistics;
import com.aaalace.analysisservice.infrastructure.repository.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService implements IAnalysisService {

    private final TableRepository tableRepository;

    @Override
    public Pair<String, TextStatistics> analyze(String raw) {
        Map<String, Integer> wordFreq = computeWordFrequencies(raw);

        float maxPercentage = computeMaxSimilarityPercentage(wordFreq);
        TextStatistics stats = computeStatistics(raw, maxPercentage);

        Table table = Table.builder().data(wordFreq).build();
        Table saved = tableRepository.save(table);

        return Pair.of(saved.getId(), stats);
    }

    private Map<String, Integer> computeWordFrequencies(String raw) {
        return Arrays.stream(raw.toLowerCase().split("\\W+"))
                .filter(word -> !word.isBlank())
                .collect(Collectors.toMap(
                        word -> word,
                        word -> 1,
                        Integer::sum,
                        LinkedHashMap::new
                ));
    }

    private float computeMaxSimilarityPercentage(Map<String, Integer> current) {
        List<Table> existingTables = tableRepository.findAll();

        return existingTables.stream()
                .map(t -> computeSimilarityPercentage(current, t.getData()))
                .max(Float::compare)
                .orElse(0f);
    }

    private float computeSimilarityPercentage(Map<String, Integer> a, Map<String, Integer> b) {
        Set<String> commonWords = new HashSet<>(a.keySet());
        commonWords.retainAll(b.keySet());

        int commonCount = commonWords.stream()
                .mapToInt(word -> Math.min(a.get(word), b.get(word)))
                .sum();

        int total = a.values().stream().mapToInt(Integer::intValue).sum();
        BigDecimal result = BigDecimal.valueOf(100f * commonCount / total)
                .setScale(2, RoundingMode.HALF_UP);
        return result.floatValue();
    }

    private TextStatistics computeStatistics(String raw, float similarityPercentage) {
        int paragraphs = raw.split("\n\n+").length;
        int words = (int) Arrays.stream(raw.split("\\W+"))
                .filter(word -> !word.isBlank())
                .count();
        int symbols = raw.replaceAll("\\s", "").length();

        return new TextStatistics(similarityPercentage, paragraphs, words, symbols);
    }
}