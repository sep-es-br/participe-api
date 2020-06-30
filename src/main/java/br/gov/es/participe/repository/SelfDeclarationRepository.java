package br.gov.es.participe.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.SelfDeclaration;

public interface SelfDeclarationRepository extends Neo4jRepository<SelfDeclaration, Long> {
	
	@Query("Match (s:SelfDeclaration)-[m:MADE]-(p:Person) "
			+" Where id(p) = {0} "
			+"Return s")
	List<SelfDeclaration> findAllByIdPerson(Long id);
	
	@Query(" MATCH (c:Conference)-[t:TO]-(s:SelfDeclaration)-[m:MADE]-(p:Person) "
			+" WHERE id(c) = {0} AND id(p) = {1} "
			+" RETURN s "
			+" ,[ "
			+"		[(s)-[a:AS_BEING_FROM]-(l:Locality) | [a,l]] "
			+" ] ")
	SelfDeclaration findByIdConferenceAndIdPerson(Long idConference, Long idPerson);
	
	@Query("Match (s:SelfDeclaration) "
			+"Where id(s)={0} "
			+"Optional Match (s)-[a:AS_BEING_FROM]-(l:Locality) "
			+"Optional Match (s)-[t:TO]-(c:Conference) "
			+"Optional Match (s)-[m:MADE]-(p:Person) "
			+"Return s, c, p, l")
	SelfDeclaration find(Long id);
	
	
}
