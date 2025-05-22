# Analysis Service

## Overview
The Analysis Service analyzes text files from the Storage Service. It counts words, paragraphs, and symbols, compares texts to find how similar they are, and creates word clouds (pictures showing the most common words).

## Launch
Launch MongoDB server on 27073 port:
```bash
docker compose up
```
Launch app on 8091 port:
```bash
./gradlew bootRun
```
## Tests
All application-layer logic is covered by tests: [AnalysisServiceApplicationTests.java](src/test/java/com/aaalace/analysisservice/AnalysisServiceApplicationTests.java)

## Data Models

### Table Collection

| Field | Type               | Description                   |
|-------|--------------------|-------------------------------|
| id    | String             | Unique ID for the table       |
| data  | Map<String, Integer>| Words and their counts        |

### Text Collection

| Field      | Type           | Description                           |
|------------|----------------|-------------------------------------|
| id         | String         | Unique ID for the text               |
| fileId     | String         | ID of the file from Storage Service |
| tableId    | String         | ID of the word count table           |
| imagePath  | String         | Location of the word cloud image    |
| statistics | TextStatistics | Statistics about the text            |

### TextStatistics

| Field      | Type    | Description                  |
|------------|---------|------------------------------|
| percentage | Float   | How similar the text is (%)  |
| paragraphs | Integer | Number of paragraphs         |
| words      | Integer | Number of words              |
| symbols    | Integer | Number of symbols            |

## Main Interfaces

### ITextService
- `processText(fileId)` — analyzes the text file
- `getWordCloud(fileId)` — returns the word cloud image

### IAnalysisService
- `analyze(rawText)` — counts words and statistics in the raw text

## How it works
- Gets files from Storage Service
- Counts words and creates word tables
- Compares new files with old ones to find similarity
- Calculates text statistics (words, paragraphs, symbols)
- Creates word cloud images using Word Cloud API
- Saves all results in MongoDB

## Summary
This service helps to analyze texts by counting words and paragraphs, checking how similar texts are, and making word cloud pictures. It stores all data in MongoDB and connects with a Word Cloud API for visualization.