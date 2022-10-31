package br.gov.es.participe.repository;

import br.gov.es.participe.model.AuthService;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuthServiceRepository extends Neo4jRepository<AuthService, Long> {

  @Query("MATCH (authService:AuthService)<-[isAuthenticatedBy:IS_AUTHENTICATED_BY]-(person:Person) " +
  "WHERE id(person)=$personId " +
  "RETURN authService, isAuthenticatedBy, person"
)
List<AuthService> findAllByIdPerson(@Param("personId")Long personId);

}
