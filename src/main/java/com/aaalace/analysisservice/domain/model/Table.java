package com.aaalace.analysisservice.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@Builder
@Document(collection = "table")
public class Table {

    @Id
    private String id;

    private Map<String, Integer> data;
}
