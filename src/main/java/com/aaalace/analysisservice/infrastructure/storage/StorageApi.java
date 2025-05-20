package com.aaalace.analysisservice.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class StorageApi {

    @Value("${storageService.baseUrl}")
    private String storageServiceBaseUrl;

    private final RestTemplate restTemplate;

    public byte[] downloadFile(String fileId) {
        String url = storageServiceBaseUrl + "/v1/file/download/" + fileId;
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                byte[].class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException();
        }
    }

    public String downloadTextFile(String fileId) {
        byte[] data = downloadFile(fileId);
        return new String(data, StandardCharsets.UTF_8);
    }
}
