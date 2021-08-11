package br.gov.es.participe.repository;

import br.gov.es.participe.model.IsAuthenticatedBy;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;

public interface IsAuthenticatedByRepository extends Neo4jRepository<IsAuthenticatedBy, Long> {

  @Query("MATCH (p:Person) "
         + "OPTIONAL MATCH(a:AuthService)-[i:IS_AUTHENTICATED_BY]-(p:Person) "
         + "WHERE id(p)={0} RETURN i, a, p")
  List<IsAuthenticatedBy> findAllByIdPerson(Long id);


  @Query(
    "MATCH (person:Person)-[is_authenticated_by:IS_AUTHENTICATED_BY]->(authService:AuthService), " +
    "(person)-[made:MADE]->(login:Login)-[using:USING]->(authService), " +
    "(login)-[to:TO]->(conference:Conference) " +
    "WHERE id(person)={0} " +
    "AND id(conference)={1} " +
    "RETURN is_authenticated_by "
  )
  List<IsAuthenticatedBy> findByIdPersonAndConference(Long personId, Long conferenceId);


  @Query("MATCH (person:Person)-[isAuthenticatedBy:IS_AUTHENTICATED_BY]->(authService:AuthService) " +
         "WHERE id(person)={0} AND id (authService)={1} " +
         "RETURN isAuthenticatedBy"
  )
  Optional<IsAuthenticatedBy> findAuthenticatedByWithPersonAndAuthService(Long personId, Long authServiceId);
}
