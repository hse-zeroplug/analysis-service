package com.aaalace.analysisservice;

import com.aaalace.analysisservice.application.dto.AnalysisResponse;
import com.aaalace.analysisservice.application.in.IAnalysisService;
import com.aaalace.analysisservice.application.service.TextService;
import com.aaalace.analysisservice.domain.model.Text;
import com.aaalace.analysisservice.domain.model.TextStatistics;
import com.aaalace.analysisservice.infrastructure.repository.TextRepository;
import com.aaalace.analysisservice.infrastructure.storage.StorageApi;
import com.aaalace.analysisservice.infrastructure.word_cloud.WordCloudApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceApplicationTests {

    @InjectMocks
    private TextService textService;

    @Mock
    private TextRepository textRepository;

    @Mock
    private StorageApi storageApi;

    @Mock
    private IAnalysisService analysisService;

    @Mock
    private WordCloudApi wordCloudApi;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(textService, "uploadDir", tempDir.toString());
    }

    @Test
    void processText_ShouldAnalyzeAndSaveText_WhenNotExists() throws Exception {
        String fileId = "abc123";
        String content = "hello world hello";
        String imageName = fileId + ".png";

        when(textRepository.findByFileId(fileId)).thenReturn(Optional.empty());
        when(storageApi.downloadTextFile(fileId)).thenReturn(content);
        when(analysisService.analyze(content)).thenReturn(Pair.of("tableId", new TextStatistics(12.5f, 1, 3, 15)));

        doAnswer(inv -> {
            Path path = tempDir.resolve(imageName);
            Files.writeString(path, "image content");
            return null;
        }).when(wordCloudApi).generateImage(eq(content), anyString());

        when(textRepository.save(any(Text.class))).thenAnswer(inv -> inv.getArgument(0));

        AnalysisResponse response = textService.processText(fileId);

        assertEquals(fileId, response.getFileId());
    }

    @Test
    void getWordCloud_ShouldReturnImage_WhenExists() throws Exception {
        String fileId = "abc123";
        String imageName = "abc123.png";
        Path path = tempDir.resolve(imageName);
        Files.writeString(path, "fake image");

        Text text = Text.builder().fileId(fileId).imagePath(imageName).build();
        when(textRepository.findByFileId(fileId)).thenReturn(Optional.of(text));

        ResponseEntity<Resource> response = textService.getWordCloud(fileId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().exists());
    }
}