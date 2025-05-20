package com.aaalace.analysisservice.infrastructure.repository;


import com.aaalace.analysisservice.domain.model.Table;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TableRepository extends MongoRepository<Table, String> {
}