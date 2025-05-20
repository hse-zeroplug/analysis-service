package com.aaalace.analysisservice.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "text")
public class Text {

    @Id
    private String id;

    private String fileId;

    private String tableId;

    private String imagePath;

    private TextStatistics statistics;
}
