package br.gov.es.participe.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.IsAuthenticatedBy;

public interface IsAuthenticatedByRepository extends Neo4jRepository<IsAuthenticatedBy, Long> {

	@Query("Match (p:Person) "
			+"OPTIONAL Match(a:AuthService)-[i:IS_AUTHENTICATED_BY]-(p:Person) "
			+"Where id(p)={0} Return i, a, p")
	List<IsAuthenticatedBy> findByIdPerson(Long id);
	
	@Query(" MATCH (p:Person)-[i:IS_AUTHENTICATED_BY]-(a:AuthService) WHERE id(p)={0} AND ext.translate(a.server) CONTAINS ext.translate({1}) RETURN i, a, p")
	IsAuthenticatedBy findByPersonAndServer(Long idPerson, String server);
}
