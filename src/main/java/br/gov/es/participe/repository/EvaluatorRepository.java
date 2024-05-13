package br.gov.es.participe.repository;

import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.model.Evaluator;

public interface EvaluatorRepository extends Neo4jRepository<Evaluator, Long> {


    @Query(
        "MATCH (eval:Evaluator) " +
        "WHERE eval.organization = $organizationGuid " +
        "RETURN eval"
    )
    Optional<Evaluator> findByOrganizationGuid(@Param("organizationGuid") String organizationGuid);
    
}
