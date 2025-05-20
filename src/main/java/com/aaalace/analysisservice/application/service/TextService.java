package com.aaalace.analysisservice.application.service;

import com.aaalace.analysisservice.application.dto.AnalysisResponse;
import com.aaalace.analysisservice.application.in.IAnalysisService;
import com.aaalace.analysisservice.application.in.ITextService;
import com.aaalace.analysisservice.application.mapper.AnalysisResponseMapper;
import com.aaalace.analysisservice.domain.exception.InternalServerError;
import com.aaalace.analysisservice.domain.model.Text;
import com.aaalace.analysisservice.domain.model.TextStatistics;
import com.aaalace.analysisservice.infrastructure.repository.TextRepository;
import com.aaalace.analysisservice.infrastructure.storage.StorageApi;
import com.aaalace.analysisservice.infrastructure.word_cloud.WordCloudApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextService implements ITextService {

    @Value("${internal.dir}")
    private String uploadDir;

    private final TextRepository textRepository;
    private final StorageApi storageApi;
    private final IAnalysisService analysisService;
    private final WordCloudApi wordCloudApi;

    public AnalysisResponse processText(String fileId) {
        Text exists = textRepository.findByFileId(fileId).orElse(null);
        if (exists != null) {
            return AnalysisResponseMapper.fromText(exists);
        }

        String rawText = downloadRawText(fileId);

        CompletableFuture<Pair<String, TextStatistics>> analysisFuture = runAnalysisAsync(rawText);
        CompletableFuture<String> imageFuture = generateWordCloudImageAsync(fileId, rawText);

        Pair<String, TextStatistics> data;
        String imagePath;
        try {
            data = analysisFuture.get();
            imagePath = imageFuture.get();
        } catch (Exception e) {
            throw new InternalServerError("Text processing failed");
        }

        Text text = Text.builder()
                .fileId(fileId)
                .tableId(data.getFirst())
                .imagePath(imagePath)
                .statistics(data.getSecond())
                .build();
        textRepository.save(text);
        log.info("Successfully proceeded text for fileId={}", text.getFileId());

        return AnalysisResponseMapper.fromText(text);
    }

    private String downloadRawText(String fileId) {
        try {
            return storageApi.downloadTextFile(fileId);
        } catch (Exception e) {
            log.error("Error downloading raw text", e);
            throw new InternalServerError("File not exists");
        }
    }

    private CompletableFuture<Pair<String, TextStatistics>> runAnalysisAsync(String rawText) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return analysisService.analyze(rawText);
            } catch (Exception e) {
                log.error("Error analyzing text", e);
                throw new RuntimeException();
            }
        });
    }

    private CompletableFuture<String> generateWordCloudImageAsync(String fileId, String rawText) {
        return CompletableFuture.supplyAsync(() -> {
            String imagePath = String.valueOf(Path.of(uploadDir, String.format("%s.png", fileId)));
            try {
                wordCloudApi.generateImage(rawText, imagePath);
                return imagePath;
            } catch (Exception e) {
                log.error("Error generating word cloud image", e);
                return null;
            }
        });
    }
}
