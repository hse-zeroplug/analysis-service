package com.aaalace.analysisservice.infrastructure.word_cloud;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

@Component
public class WordCloudApi {

    @Value("${wordCloud.host}")
    private String wordCloudHost;

    @Value("${wordCloud.path}")
    private String wordCloudPath;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void generateImage(String text, String outputPath) throws Exception {
        URI uri = new URI("https", wordCloudHost, wordCloudPath, null);

        RequestBody body = new RequestBody();
        body.setFormat("png");
        body.setWidth(1000);
        body.setHeight(1000);
        body.setFontFamily("sans-serif");
        body.setFontScale(15);
        body.setScale("linear");
        body.setText(text);

        byte[] jsonBytes = objectMapper.writeValueAsBytes(body);

        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream os = connection.getOutputStream()) {
            os.write(jsonBytes);
            os.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed to download image, response code: " + responseCode);
        }

        java.nio.file.Path output = java.nio.file.Paths.get(outputPath);
        java.nio.file.Path dir = output.getParent();
        if (dir != null && !java.nio.file.Files.exists(dir)) {
            java.nio.file.Files.createDirectories(dir);
        }

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(output.toFile())) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }

    @Data
    private static class RequestBody {
        private String format;
        private int width;
        private int height;
        private String fontFamily;
        private int fontScale;
        private String scale;
        private String text;
    }
}