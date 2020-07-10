package br.gov.es.participe.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.Login;

public interface LoginRepository extends Neo4jRepository<Login, Long>{
	
	@Query("MATCH (l:Login)-[:USING]->(p:Person) "
			+" WHERE id(p)={0} "
			+" RETURN l")
	List<Login> findAllByPerson(Long idPerson);
}
