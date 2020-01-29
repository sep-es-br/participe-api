package br.gov.es.participe.repository;

import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.StructureItem;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface PlanItemRepository extends Neo4jRepository<PlanItem, Long> {

    @Query("MATCH (pi:PlanItem) WHERE LOWER(pi.name) CONTAINS($name) RETURN pi")
    List<PlanItem> search(String name);
}
