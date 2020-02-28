package br.gov.es.participe.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.LocalityType;

public interface LocalityTypeRepository extends Neo4jRepository<LocalityType, Long> {

    @Query("MATCH (l:LocalityType) DETACH DELETE l")
    void deleteAll();

}
