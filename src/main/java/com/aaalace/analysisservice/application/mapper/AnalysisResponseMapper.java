package com.aaalace.analysisservice.application.mapper;

import com.aaalace.analysisservice.application.dto.AnalysisResponse;
import com.aaalace.analysisservice.domain.model.Text;

public class AnalysisResponseMapper {

    public static AnalysisResponse fromText(Text text) {
        return AnalysisResponse.builder()
                .fileId(text.getFileId())
                .statistics(text.getStatistics())
                .build();
    }
}
