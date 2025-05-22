package com.aaalace.analysisservice.presentation.controller;

import com.aaalace.analysisservice.application.dto.AnalysisResponse;
import com.aaalace.analysisservice.application.in.ITextService;
import com.aaalace.analysisservice.domain.generic.GenericJsonResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/text")
@RequiredArgsConstructor
public class TextController {

    private final ITextService textService;

    @PostMapping("/analyze/{fileId}")
    public GenericJsonResponse<AnalysisResponse> analyze(@PathVariable() @NonNull String fileId) {
        AnalysisResponse response = textService.processText(fileId);
        return GenericJsonResponse.success(response);
    }

    @GetMapping("/cloud/{fileId}")
    public ResponseEntity<Resource> getWordCloud(@PathVariable() @NonNull String fileId) {
        return textService.getWordCloud(fileId);
    }
}
