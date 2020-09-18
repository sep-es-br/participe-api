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
}
