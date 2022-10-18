package br.gov.es.participe.repository;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.ModeratedBy;

public interface ModeratedByRepository extends Neo4jRepository<ModeratedBy, Long> {
	@Query("MATCH (c:Comment)-[m:MODERATED_BY]->(p:Person) "
			+"Where id(c)=$comment Return c, m, p")
	ModeratedBy findByComment( @Param("comment") Comment comment);
}
