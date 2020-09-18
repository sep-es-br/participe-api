package br.gov.es.participe.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.Meeting;

public interface MeetingRepository extends Neo4jRepository<Meeting,Long>{
	
	@Query(  " MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) "
			+" WHERE id(c) = {0} "
			+" RETURN m, oc, c"
			+" , ["
			+" 		[(m)-[tp:TAKES_PLACE_AT]->(lp:Locality) | [tp, lp] ],"
			+"	 	[(m)-[co:COVERS]-(lc:Locality) | [co, lc] ],"
			+"		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]"
			+" ] "
			+"ORDER BY m.beginDate")
	Collection<Meeting> findAll(Long idConference);
	
	@Query(  value = "MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) "
			+" , (m)-[tp:TAKES_PLACE_AT]->(lp:Locality)"
			+" WHERE id(c) = {0} "
			+" AND ext.translate(m.name) CONTAINS ext.translate({1}) "
			+" AND (id(lp) IN {4} OR NOT {4})"
			+" AND ( (datetime(m.beginDate) >= datetime({2}) OR {2} IS NULL) OR (datetime(m.endDate) >= datetime({2}) OR {2} IS NULL) )"
			+" AND ( (datetime(m.beginDate) <= datetime({3}) OR {3} IS NULL) OR (datetime(m.endDate) <= datetime({3}) OR {3} IS NULL) )"
			+" RETURN m, oc, c, tp, lp"
			+" , ["
			+"	 	[(m)-[co:COVERS]-(lc:Locality) | [co, lc] ],"
			+"		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]"
			+" ] "
			+"ORDER BY m.beginDate"
	, countQuery = "MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) "
			+" , (m)-[tp:TAKES_PLACE_AT]->(lp:Locality)"
			+" WHERE id(c) = {0} "
			+" AND ext.translate(m.name) CONTAINS ext.translate({1}) "
			+" AND (id(lp) IN {4} OR NOT {4})"
			+" AND ( (datetime(m.beginDate) >= datetime({2}) OR {2} IS NULL) OR (datetime(m.endDate) >= datetime({2}) OR {2} IS NULL) )"
			+" AND ( (datetime(m.beginDate) <= datetime({3}) OR {3} IS NULL) OR (datetime(m.endDate) <= datetime({3}) OR {3} IS NULL) )"
			+" WITH m AS meeting, oc AS occurs, c AS conference, tp AS takesPlaces, lp AS locality"
			+" , ["
			+"	 	[(m)-[co:COVERS]-(lc:Locality) | [co, lc] ],"
			+"		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]"
			+" ] AS relationships "
			+"RETURN COUNT(*)")
	Page<Meeting> findAll(Long idConference, String name, Date beginDate, Date endDate, List<Long> localities, Pageable pageable);

	@Query(  " MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) "
			+" WHERE id(c) = {0} "
			+" RETURN m "
			+" , ["
			+" 		[(m)-[tp:TAKES_PLACE_AT]->(lp:Locality) | [tp, lp] ],"
			+"	 	[(m)-[co:COVERS]-(lc:Locality) | [co, lc] ]"
			+" ] "
			+"ORDER BY m.beginDate")
	Collection<Meeting> findAllDashboard(Long idConference);

	@Query(	"MATCH (m:Meeting) "
			+" WHERE id(m) = {0} "
			+" RETURN m "
			+" , ["
			+"		[(m)-[tp:TAKES_PLACE_AT]-(lp:Locality) | [tp,lp]],"
			+"		[(m)-[co:COVERS]-(lc:Locality) | [co, lc]],"
			+"		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]"
			+" ]")
	Optional<Meeting> findMeetingWithoutConference(Long id);

	@Query(	"MATCH (m:Meeting)-[oc:OCCURS_IN]->(c:Conference) "
			+" WHERE id(m) = {0} "
			+" RETURN m, oc, c "
			+" , ["
			+"		[(m)-[tp:TAKES_PLACE_AT]-(lp:Locality) | [tp,lp]],"
			+"		[(m)-[co:COVERS]-(lc:Locality) | [co, lc]],"
			+"		[(m)<-[ir:IS_RECEPTIONIST_OF]-(recep:Person) | [ir, recep] ]"
			+" ]")
	Optional<Meeting> findMeetingWithRelationshipsById(Long id);
}
