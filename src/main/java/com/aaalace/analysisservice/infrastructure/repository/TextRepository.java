package com.aaalace.analysisservice.infrastructure.repository;


import com.aaalace.analysisservice.domain.model.Text;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TextRepository extends MongoRepository<Text, String> {
    Optional<Text> findByFileId(String fileId);
}