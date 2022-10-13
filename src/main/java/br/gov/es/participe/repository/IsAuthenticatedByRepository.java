package br.gov.es.participe.repository;

import br.gov.es.participe.model.IsAuthenticatedBy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IsAuthenticatedByRepository extends Neo4jRepository<IsAuthenticatedBy, Long> {

  @Query("MATCH (p:Person) "
      + "OPTIONAL MATCH(a:AuthService)-[i:IS_AUTHENTICATED_BY]-(p:Person) "
      + "WHERE id(p)=$id RETURN i, a, p")
  List<IsAuthenticatedBy> findAllByIdPerson( @Param("id") Long id);

  @Query("MATCH (person:Person)-[is_authenticated_by:IS_AUTHENTICATED_BY]->(authService:AuthService), " +
      "(person)-[made:MADE]->(login:Login)-[using:USING]->(authService), " +
      "(login)-[to:TO]->(conference:Conference) " +
      "WHERE id(person)=$personId " +
      "AND id(conference)=$conferenceId " +
      "RETURN is_authenticated_by ")
  List<IsAuthenticatedBy> findByIdPersonAndConference( @Param("personId") Long personId, @Param("conferenceId") Long conferenceId);

  @Query("MATCH (person:Person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
      "WHERE id(person)=$personId AND id (authService)=$authServiceId " +
      "RETURN isAuthenticatedBy")
  Optional<IsAuthenticatedBy> findAuthenticatedByWithPersonAndAuthService( @Param("personId") Long personId, @Param("authServiceId") Long authServiceId);
}
