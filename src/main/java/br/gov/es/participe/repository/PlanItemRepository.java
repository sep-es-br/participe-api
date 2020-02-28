package br.gov.es.participe.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.PlanItem;

public interface PlanItemRepository extends Neo4jRepository<PlanItem, Long> {

    @Query("MATCH (pi:PlanItem) WHERE LOWER(pi.name) CONTAINS($name) RETURN pi")
    List<PlanItem> search(String name);

    @Query("MATCH (p:PlanItem) DETACH DELETE p")
    void deleteAll();
}
