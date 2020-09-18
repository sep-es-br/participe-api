package br.gov.es.participe.repository;

import br.gov.es.participe.model.CheckedInAt;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;
import java.util.Set;

public interface CheckedInAtRepository extends Neo4jRepository<CheckedInAt, Long> {
    @Query("MATCH (p:Person)-[part:CHECKED_IN_AT]->(m:Meeting) "
            +"Where id(m)={0} Return p, part, m")
    Set<CheckedInAt> findByMeeting(Long meetingId);

    @Query("MATCH (p:Person)-[part:CHECKED_IN_AT]->(m:Meeting) "
            +"Where id(p)={0} AND id(m)={1} Return p, part, m")
    Optional<CheckedInAt> findByPersonAndMeeting(Long personId, Long meetingId);
}

