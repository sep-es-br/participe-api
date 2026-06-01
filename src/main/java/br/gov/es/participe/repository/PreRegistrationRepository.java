package br.gov.es.participe.repository;

import br.gov.es.participe.model.PreRegistration;
import java.util.Optional;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface PreRegistrationRepository extends Neo4jRepository<PreRegistration, Long> {

    @Query("MATCH (pr:PreRegistration)-[r_1:PRE_REGISTRATION]->(p:Person),(pr)-[r_2:PRE_REGISTRATION]->(m:Meeting)"+
    "WHERE id(p)=$personId AND id(m)=$meetingId " +
    "WITH pr RETURN pr, [" + //
    "[(pr)-[r_1:PRE_REGISTRATION]->(p) | [r_1,p]]," + //
    "[(pr)-[r_2:PRE_REGISTRATION]->(m) | [r_2,m]]" + //
    "]")
    PreRegistration findByMeetingAndPerson( @Param("meetingId") Long meetingId, @Param("personId") Long personID);

    @Query("MATCH (pr:c)-[r_1:PRE_REGISTRATION]->(p:Person),(pr)-[r_2:PRE_REGISTRATION]->(m:Meeting)" + //
                "WHERE id(pr)=$preRegistrationId " + //
                "WITH pr RETURN pr, [" + //
                "[(pr)-[r_1:PRE_REGISTRATION]->(p) | [r_1,p]]," + //
                "[(pr)-[r_2:PRE_REGISTRATION]->(m) | [r_2,m]]" + //
                "]")
    Optional<PreRegistration> findPreRegistrationWithRelationshipsById(@Param("preRegistrationId") Long preRegistrationId);
    
    // Deleta o relacionamento direto pelo ID dele no Neo4j
    @Query("MATCH (r:PreRegistration) WHERE  id(r) = $id DETACH DELETE r")
    void deletePreRegistrationById(Long id);
    
}
