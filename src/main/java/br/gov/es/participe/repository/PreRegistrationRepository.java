package br.gov.es.participe.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.model.PreRegistration;

public interface PreRegistrationRepository extends Neo4jRepository<PreRegistration, Long> {

    @Query("MATCH (pr:PreRegistration)-[r_1:PRE_REGISTRATION]->(p:Person),(pr)-[r_2:PRE_REGISTRATION]->(m:Meeting)"+
    "WHERE id(p)=$personId AND id(m)=$meetingId " +
    "WITH pr RETURN pr, [" + //
    "[(pr)-[r_1:PRE_REGISTRATION]->(p) | [r_1,p]]," + //
    "[(pr)-[r_2:PRE_REGISTRATION]->(m) | [r_2,m]]" + //
    "]")
    PreRegistration findByMeetingAndPerson( @Param("meetingId") Long meetingId, @Param("personId") Long personID);
    
}
