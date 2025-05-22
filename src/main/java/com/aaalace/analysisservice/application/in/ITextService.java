package com.aaalace.analysisservice.application.in;

import com.aaalace.analysisservice.application.dto.AnalysisResponse;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface ITextService {

    AnalysisResponse processText(String fileId);

    ResponseEntity<Resource> getWordCloud(String fileId);
}
