package br.gov.es.participe.repository;

import br.gov.es.participe.model.Highlight;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface HighlightRepository extends Neo4jRepository<Highlight, Long> {

  @Query("MATCH  (p:Person)"
         + " WHERE id(p)={0}"
         + " OPTIONAL MATCH (p)<-[lb:MADE_BY]-(h:Highlight)"
         + " RETURN h")
  List<Highlight> findByIdPerson(Long idPerson);

  @Query("MATCH (co:Conference)<-[:ABOUT]-(h:Highlight)-[:ABOUT]->(pi:PlanItem) "
         + "MATCH (h)-[:MADE_BY]->(p:Person) "
         + "OPTIONAL MATCH (h)-[:ABOUT]->(l:Locality) "
         + "WITH co, h, pi, p, l "
         + "WHERE id(p)={0} "
         + "AND id(pi)={1} "
         + "AND (id(co)={2} OR {2} IS NULL) "
         + "AND (id(l)={3} OR {3} IS NULL) "
         + "RETURN h, co, pi, p"
  )
  Highlight findByIdPersonAndIdPlanItem(Long idPerson, Long idPlanItem, Long idConference, Long idLocality);

  @Query("MATCH (co:Conference)<-[:ABOUT]-(h:Highlight)-[:ABOUT]->(pi:PlanItem), "
         + "(h)-[:MADE_BY]->(p:Person) "
         + "OPTIONAL MATCH (pi)-[:COMPOSES]->(pi2:PlanItem) "
         + "OPTIONAL MATCH (h)-[:ABOUT]->(l:Locality) "
         + "WITH h, l, co, p, pi, pi2 "
         + "WHERE id(p)={0} "
         + "AND (id(pi)={1} OR id(pi2) = {1}) "
         + "AND id(co)={2} "
         + "AND (id(l) = {3} OR {3} IS NULL) "
         + "RETURN h, co, p, pi, pi2"
  )
  List<Highlight> findAllByIdPersonAndIdPlanItemAndIdConferenceAndIdLocality(Long idPerson, Long idPlanItem, Long idConference, Long idLocality);

  @Query(" MATCH (co:Conference)-[a:ABOUT]-(h:Highlight)"
         + " WHERE id(co)={0}  "
         + " RETURN count(h)")
  Integer countHighlightByConference(Long id);
}
