package br.gov.es.participe.repository;


import br.gov.es.participe.model.Conference;
import br.gov.es.participe.model.ConferenceColor;
import br.gov.es.participe.model.PreRegistration;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ConferenceColorRepository extends Neo4jRepository<ConferenceColor, Long> {
    @Query("MATCH (color:ConferenceColor)-[r:CONFERENCE_COLOR]->(c:Conference)WHERE id(c) = $id RETURN color")
    Optional<ConferenceColor> findByConferenceColor(@Param("id") Long id);

}
