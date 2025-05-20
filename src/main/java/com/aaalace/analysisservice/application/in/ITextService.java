package com.aaalace.analysisservice.application.in;

import com.aaalace.analysisservice.application.dto.AnalysisResponse;

public interface ITextService {

    AnalysisResponse processText(String fileId);
}
