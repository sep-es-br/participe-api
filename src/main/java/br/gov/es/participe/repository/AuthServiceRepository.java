package br.gov.es.participe.repository;

import br.gov.es.participe.model.AuthService;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface AuthServiceRepository extends Neo4jRepository<AuthService, Long> {

  @Query("MATCH (authService:AuthService)<-[isAuthenticatedBy:IS_AUTHENTICATED_BY]-(person:Person) " +
         "WHERE id(person)={0} " +
         "RETURN authService, isAuthenticatedBy, person"
  )
  List<AuthService> findAllByIdPerson(Long personId);

}
