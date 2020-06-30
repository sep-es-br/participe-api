package br.gov.es.participe.repository;

import br.gov.es.participe.model.Highlight;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface HighlightRepository extends Neo4jRepository<Highlight,Long>{

	@Query("MATCH  (p:Person)"
			+" WHERE id(p)={0}"
			+" OPTIONAL MATCH (p)<-[lb:MADE_BY]-(h:Highlight)"
			+" RETURN h")
	List<Highlight> findByIdPerson(Long idPerson);
	
	@Query("MATCH (co:Conference)<-[:ABOUT]-(h:Highlight)-[:ABOUT]->(pi:PlanItem) "
			+" MATCH (h)-[:MADE_BY]->(p:Person) "
			+" WHERE id(p)={0} And id(pi)={1} AND (id(co)={2} OR {2} IS NULL)"
			+" RETURN h")
	Highlight findByIdPersonAndIdPlanItem(Long idPerson, Long idPlanItem, Long idConference);
	
	@Query("MATCH (co:Conference)<-[:ABOUT]-(h:Highlight)-[:ABOUT]->(pi:PlanItem)-[:COMPOSES]->(pi2:PlanItem) "
			+" , (h)-[:MADE_BY]->(p:Person) "
			+" , (h)-[:ABOUT]->(l:Locality) "
			+" WHERE id(p)={0} And (id(pi)={1} OR id(pi2) = {1}) AND id(co)={2} AND id(l) = {3} "
			+" RETURN h")
	List<Highlight> findAllByIdPersonAndIdPlanItemAndIdConferenceAndIdLoclity(Long idPerson, Long idPlanItem, Long idConference, Long idLocality);
	
	@Query(" MATCH (pi:PlanItem)-[a:ABOUT]-(h:Highlight) "
			+" WHERE id(pi)={0}  "
			+" RETURN COUNT(h)")
	Integer countHighlightByPlanItem(Long id);
	
	@Query(" MATCH (co:Conference)-[a:ABOUT]-(h:Highlight) "
			+" WHERE id(co)={0}  "
			+" RETURN COUNT(h)")
	Integer countHighlightByConference(Long id);
}
