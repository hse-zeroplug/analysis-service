package com.aaalace.analysisservice.application.dto;

import com.aaalace.analysisservice.domain.model.TextStatistics;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnalysisResponse {

    private String fileId;

    private TextStatistics statistics;
}
