package br.gov.es.participe.repository;

import java.util.Collection;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.Meeting;

public interface MeetingRepository extends Neo4jRepository<Meeting,Long>{
	
	@Query(  " MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) "
			+" WHERE id(c) = {0} "
			+" RETURN m, oc, c"
			+" , ["
			+" 		[(m)-[tp:TAKES_PLACE_AT]->(lp:Locality) | [tp, lp] ],"
			+"	 	[(m)-[co:COVERS]-(lc:Locality) | [co, lc] ]"
			+" ]")
	Collection<Meeting> findAll(Long idConference);
	
	@Query(	"MATCH (m:Meeting) "
			+" WHERE id(m) = {0} "
			+" RETURN m "
			+" , ["
			+"		[(m)-[tp:TAKES_PLACE_AT]-(lp:Locality) | [tp,lp]],"
			+"		[(m)-[co:COVERS]-(lc:Locality) | [co, lc]]"
			+" ]")
	Meeting findMeetingById(Long id);
}
