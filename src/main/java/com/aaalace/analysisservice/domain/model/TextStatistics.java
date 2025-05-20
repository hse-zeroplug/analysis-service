package com.aaalace.analysisservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TextStatistics {

    public Float percentage;

    public Integer paragraphs;

    public Integer words;

    public Integer symbols;
}
