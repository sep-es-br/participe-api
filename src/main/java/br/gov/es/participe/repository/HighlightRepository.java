package br.gov.es.participe.repository;

import br.gov.es.participe.model.Highlight;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HighlightRepository extends Neo4jRepository<Highlight, Long> {

  @Query("MATCH  (p:Person)"
      + " WHERE id(p)=$idPerson"
      + " OPTIONAL MATCH (p)<-[lb:MADE_BY]-(h:Highlight)"
      + " RETURN h")
  List<Highlight> findByIdPerson( @Param("idPerson") Long idPerson);

  @Query("MATCH (co:Conference)<-[:ABOUT]-(h:Highlight)-[:ABOUT]->(pi:PlanItem) "
      + "MATCH (h)-[:MADE_BY]->(p:Person) "
      + "OPTIONAL MATCH (h)-[:ABOUT]->(l:Locality) "
      + "WITH co, h, pi, p, l "
      + "WHERE id(p)=$idPerson "
      + "AND id(pi)=$idPlanItem "
      + "AND (id(co)=$idConference OR $idConference IS NULL) "
      + "AND (id(l)=$idLocality OR $idLocality IS NULL) "
      + "RETURN h, co, pi, p"
  )
  Highlight findByIdPersonAndIdPlanItem( @Param("idPerson") Long idPerson, @Param("idPlanItem") Long idPlanItem, @Param("idConference") Long idConference, @Param("idLocality") Long idLocality);

  @Query("MATCH (co:Conference)<-[:ABOUT]-(h:Highlight)-[:ABOUT]->(pi:PlanItem), "
      + "(h)-[:MADE_BY]->(p:Person) "
      + "OPTIONAL MATCH (pi)-[:COMPOSES]->(pi2:PlanItem) "
      + "OPTIONAL MATCH (h)-[:ABOUT]->(l:Locality) "
      + "WITH h, l, co, p, pi, pi2 "
      + "WHERE id(p)=$idPerson "
      + "AND (id(pi)=$idPlanItem OR id(pi2) = $idPlanItem) "
      + "AND id(co)=$idConference "
      + "AND (id(l) = $idLocality OR $idLocality IS NULL) "
      + "RETURN h, co, p, pi, pi2")
  List<Highlight> findAllByIdPersonAndIdPlanItemAndIdConferenceAndIdLocality( @Param("idPerson") Long idPerson, @Param("idPlanItem") Long idPlanItem, @Param("idConference") Long idConference, @Param("idLocality") Long idLocality);

  @Query(" MATCH (co:Conference)-[a:ABOUT]-(h:Highlight)"
      + " WHERE id(co)=$id  "
      + " RETURN count(h)")
  Integer countHighlightByConference( @Param("id") Long id);
  

  
  //marcos 
  @Query("  MATCH (co:Conference)<-[a:ABOUT]-(h:Highlight) " + 
	  	 "  WHERE id(co)=$idConference " + 
	  	 "  RETURN count(h) ")
  Integer countHighlightAllOriginsByConference( @Param("idConference") Long idConference);
	  
	  
	  
   @Query(" match (h:Highlight)-[:ABOUT]->(co:Conference) " + 
	  	  "  where id(co) = $idConference AND h.from='rem' " + 
	  	  "  return count (distinct h) ")
   Integer countHighlightRemoteOriginByConference( @Param("idConference") Long idConference);
	  
	  
	 
    @Query(" match (m:Meeting)<-[:DURING]-(h:Highlight)-[:ABOUT]->(co:Conference) " + 
		   "  where id(co) = $idConference AND h.from='pres' AND (($meetings IS NULL) OR (id(m) IN $meetings))  " + 
		   "  return count (distinct h) ")
	Integer countHighlightPresentialOriginByConference(@Param("idConference") Long idConference, @Param("meetings") List<Long> meetings);
  
  
  
}
