package br.gov.es.participe.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.model.Evaluation;

public interface  EvaluationRepository extends Neo4jRepository<Evaluation, Long> {

    @Query("MATCH (c:Conference)<-[:APPLIES_TO]-(e:Evaluation) " +
            " WHERE id(c)=$id " +
            " RETURN e")
    Evaluation findByConferenceId( @Param("id") Long id);
    
}
