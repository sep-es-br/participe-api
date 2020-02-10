package br.gov.es.participe.repository;

import java.util.Collection;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.Conference;

public interface ConferenceRepository extends Neo4jRepository<Conference, Long> {
    @Query("MATCH (n:Conference)-[cp:TARGETS]->(p:Plan) "
               + " WHERE LOWER(n.name) CONTAINS LOWER($name) "
               + " AND (ID(p) = $plan OR $plan IS NULL) "
               + " AND ((datetime(n.beginDate).year = $year OR $year IS NULL) OR (datetime(n.endDate).year = $year OR $year IS NULL))"
               + " AND ((datetime(n.beginDate).month = $month OR $month IS NULL) OR (datetime(n.endDate).month = $month OR $month IS NULL))"
               + " RETURN n,cp,p ORDER BY n.beginDate")
    Collection<Conference> findAllByQuery(String name, Long plan, Integer month, Integer year);

    Conference findByNameIgnoreCase(String name);
}
