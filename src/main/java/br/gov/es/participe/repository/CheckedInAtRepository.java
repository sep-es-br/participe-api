package br.gov.es.participe.repository;

import br.gov.es.participe.model.CheckedInAt;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface CheckedInAtRepository extends Neo4jRepository<CheckedInAt, Long> {
        @Query("MATCH (p:Person)-[part:CHECKED_IN_AT]->(m:Meeting) "
        +"Where id(m)=$meetingId Return p, part, m")
    Set<CheckedInAt> findByMeeting(@Param("meetingId") Long meetingId);

    @Query("MATCH (p:Person)-[part:CHECKED_IN_AT]->(m:Meeting) "
            +"Where id(p)=$personId AND id(m)=$meetingId Return p, part, m, [(m)-[tp:TAKES_PLACE_AT]->(lp:Locality) | [tp, lp] ]")
    Optional<CheckedInAt> findByPersonAndMeeting( @Param("personId") Long personId, @Param("meetingId") Long meetingId);
    
    @Query("match (p:Person)-[ci:CHECKED_IN_AT]->(m:Meeting)\n" +
            "where id(ci) = $checkedAtId\n" +
            "set ci.isAnnounced = not coalesce(ci.isAnnounced, false)\n" +
            "return p, ci, m")
    Optional<CheckedInAt> toggleAnnounced(Long checkedAtId);
    
    @Query("match (p:Person)-[ci:CHECKED_IN_AT]->(m:Meeting)\n" +
            "where id(ci) = $checkedAtId\n" +
            "set ci.toAnnounce = not coalesce(ci.toAnnounce, false)\n" +
            "return p, ci, m")
    Optional<CheckedInAt> toggleToAnnounce(Long checkedAtId);
}

