package com.aaalace.analysisservice.application.in;

import com.aaalace.analysisservice.domain.model.TextStatistics;
import org.springframework.data.util.Pair;

public interface IAnalysisService {
    Pair<String, TextStatistics> analyze(String raw);
}
