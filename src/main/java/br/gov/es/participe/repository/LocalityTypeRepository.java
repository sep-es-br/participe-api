package br.gov.es.participe.repository;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import br.gov.es.participe.model.LocalityType;

public interface LocalityTypeRepository extends Neo4jRepository<LocalityType, Long> {
	
	@Query(	" MATCH (l:Locality)-[OF_TYPE]->(lt:LocalityType) "
			+" WHERE id(l) = $id "
			+" RETURN lt")
	LocalityType findByIdLocality( @Param("id") Long id);

    @Query("MATCH (l:LocalityType) DETACH DELETE l")
    void deleteAll();
}
