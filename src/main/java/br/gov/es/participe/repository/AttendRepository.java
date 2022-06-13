package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.ParticipationDto;
import br.gov.es.participe.model.Attend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface AttendRepository extends Neo4jRepository<Attend, Long> {

  @Query("MATCH(a:Attend)-[m:MADE_BY]->(p:Person) "
      + "WHERE id(p)={0} "
      + "RETURN m, a, p")
  List<Attend> findAllAttendByIdPerson(Long id);

 // @Query(" MATCH (c:Conference)<-[t:TO]-(self:SelfDeclaration)<-[m:MADE]-(p:Person) "
  //    + " WHERE id(c)={0} RETURN count(DISTINCT p)")
  //Integer countParticipationByConference(Long idConference);

  @Query(" match (p:Person)-[:MADE]->(lo:Login)-[:TO]->(co:Conference),(p)-[:MADE]->(s:SelfDeclaration)-[:TO]->(co) "
       + " WHERE id(co)={0} RETURN count(DISTINCT p)")
  Integer countParticipationByConference(Long idConference);


  @Query("MATCH (conf:Conference)<-[:TO]-(s:SelfDeclaration)<-[:MADE]-(p:Person) " +
      "OPTIONAL MATCH (conf)<-[:ABOUT]-(c:Comment)-[:MADE_BY]->(p) " +
      "OPTIONAL MATCH (conf)<-[:ABOUT]-(h:Highlight)-[:MADE_BY]->(p) " +
      "OPTIONAL MATCH (conf)<-[:OCCURS_IN]-(m:Meeting)<-[:CHECKED_IN_AT]-(p) " +
      "WITH conf, p, c, h, m " +
      "WHERE id(conf)={0} " +
      "AND ( " +
      " (c IS NOT NULL AND c.status='pub') " +
      " OR h IS NOT NULL " +
      " OR m IS NOT NULL " +
      ") " +
      "AND (({1} IS NULL) OR (c IS NULL OR c.type={1})) " +
      "AND ( " +
      " (({1} IS NULL OR {1}='pre') AND (m IS NULL OR (m.typeMeetingEnum='PRESENCIAL' OR m.typeMeetingEnum='PRESENCIAL_VIRTUAL'))) OR " +
      " (({1} IS NULL OR {1}<>'pre') AND (m IS NULL OR (m.typeMeetingEnum<>'PRESENCIAL' AND m.typeMeetingEnum<>'PRESENCIAL_VIRTUAL'))) " +
      ") " +
      "AND ({1}<>'pre' OR m IS NULL OR {2} IS NULL OR id(m) IN {2}) " +
      "RETURN count(DISTINCT p)")
  Integer countParticipationByConferenceAndType(Long idConference, String type, List<Long> meetings);

  @Query("MATCH (conf:Conference)<-[:TO]-(s:SelfDeclaration)<-[:MADE]-(p:Person) " +
      "OPTIONAL MATCH (conf)<-[:ABOUT]-(c:Comment)-[:MADE_BY]->(p) " +
      "OPTIONAL MATCH (conf)<-[:ABOUT]-(h:Highlight)-[:MADE_BY]->(p) " +
      "OPTIONAL MATCH (conf)<-[:OCCURS_IN]-(m:Meeting)<-[:CHECKED_IN_AT]-(p) " +
      "WITH conf, p, c, h, m " +
      "WHERE id(conf)={0} " +
      "AND ( " +
      " (c IS NOT NULL AND c.status='pub') " +
      " OR h IS NOT NULL " +
      " OR m IS NOT NULL " +
      ") " +
      "AND (({1} IS NULL) OR (c IS NULL OR c.type={1})) " +
      "AND ( " +
      " (({1} IS NULL OR {1}='pre') AND (m IS NULL OR (m.typeMeetingEnum='PRESENCIAL' OR m.typeMeetingEnum='PRESENCIAL_VIRTUAL'))) OR " +
      " (({1} IS NULL OR {1}<>'pre') AND (m IS NULL OR (m.typeMeetingEnum<>'PRESENCIAL' AND m.typeMeetingEnum<>'PRESENCIAL_VIRTUAL'))) " +
      ") " +
      "AND ({1}<>'pre' OR m IS NULL OR {2} IS NULL OR id(m) IN {2}) " +
      "RETURN count(DISTINCT c)")
  Integer countCommentByConferenceAndType(Long idConference, String type, List<Long> meetings);

  @Query("MATCH (l:Locality)<-[:AS_BEING_FROM]-(s:SelfDeclaration)-[:TO]->(conf:Conference), (s)<-[:MADE]-(p:Person) " +
      "OPTIONAL MATCH (conf)<-[:ABOUT]-(c:Comment)-[:MADE_BY]->(p) " +
      "OPTIONAL MATCH (conf)<-[:ABOUT]-(h:Highlight)-[:MADE_BY]->(p) " +
      "OPTIONAL MATCH (conf)<-[:OCCURS_IN]-(m:Meeting)<-[:CHECKED_IN_AT]-(p) " +
      "WITH l, s, conf, p, c, h, m " +
      "WHERE id(conf)={0} " +
      "AND ( " +
      " (c IS NOT NULL AND c.status='pub') " +
      " OR h IS NOT NULL " +
      " OR m IS NOT NULL " +
      ") " +
      "AND (({1} IS NULL) OR (c IS NULL OR c.type={1})) " +
      "AND ( " +
      " (({1} IS NULL OR {1}='pre') AND (m IS NULL OR (m.typeMeetingEnum='PRESENCIAL' OR m.typeMeetingEnum='PRESENCIAL_VIRTUAL'))) OR " +
      " (({1} IS NULL OR {1}<>'pre') AND (m IS NULL OR (m.typeMeetingEnum<>'PRESENCIAL' AND m.typeMeetingEnum<>'PRESENCIAL_VIRTUAL'))) " +
      ") " +
      "AND ({1}<>'pre' OR m IS NULL OR {2} IS NULL OR id(m) IN {2}) " +
      "RETURN count(DISTINCT l)")
  Integer countLocalityByConferenceAndType(Long idConference, String type, List<Long> meetings);

  @Query(value = "MATCH (p:Person)<-[a:MADE_BY]-(at:Attend)-[ab:ABOUT]->(conf:Conference)"
      + " MATCH (at)-[ap:ABOUT]->(pi:PlanItem) "
      + " OPTIONAL MATCH (at)-[al:ABOUT]->(loc:Locality)-[ot:OF_TYPE]->(lt:LocalityType) "
      + " WITH at, ap, al, loc, ot, lt, pi, p, conf "
      + " WHERE id(p)={0} AND id(conf) = {1} "
      + " AND (at.status IS NULL OR NOT at.status IN ['rem', 'arq']) "
      + " OPTIONAL MATCH (pi)-[comp:COMPOSES*]->(pi2:PlanItem) "
      + " WITH p,at,conf,pi,loc,lt,pi2 "
      + " WHERE ({2} IS NULL OR (at.text IS NOT NULL AND ext.translate(at.text) CONTAINS ext.translate({2})) "
      + " OR (at.text IS NULL AND ext.translate(pi.name) CONTAINS ext.translate({2})) "
      + " OR (at.text IS NULL AND ext.translate(loc.name) CONTAINS ext.translate({2})) "
      + " OR (at.text IS NULL AND pi2 IS NOT NULL AND ext.translate(pi2.name) CONTAINS ext.translate({2}))) "
      + " OPTIONAL MATCH (at)-[mb:MODERATED_BY]->(m:Person) "
      + " RETURN DISTINCT id(at) AS id, at.text AS text, at.status AS status, at.time AS time, "
      + " at.classification AS classification, m.name AS moderatorName, mb.time AS moderateTime, mb.finish AS moderated, "
      + " pi AS planItem, loc AS locality, lt AS localityType, [(at)-[lk:LIKED_BY]->(plk:Person) | [plk]] AS personLiked  "
      + " ORDER BY at.time DESC ",
      countQuery = "MATCH (p:Person)<-[a:MADE_BY]-(at:Attend)-[ab:ABOUT]->(conf:Conference)"
          + " MATCH (at)-[ap:ABOUT]->(pi:PlanItem) "
          + " OPTIONAL MATCH (at)-[al:ABOUT]->(loc:Locality)-[ot:OF_TYPE]->(lt:LocalityType) "
          + " WITH at, ap, al, loc, ot, lt, pi, p, conf "
          + " WHERE id(p)={0} AND id(conf) = {1} "
          + " AND (at.status IS NULL OR NOT at.status IN ['rem', 'arq']) "

          + " OPTIONAL MATCH (pi)-[comp:COMPOSES*]->(pi2:PlanItem) "
          + " WITH p,at,conf,pi,loc,lt,pi2 "
          + " WHERE ({2} IS NULL OR (at.text IS NOT NULL AND ext.translate(at.text) CONTAINS ext.translate({2})) "
          + " OR (at.text IS NULL AND ext.translate(pi.name) CONTAINS ext.translate({2})) "
          + " OR (at.text IS NULL AND ext.translate(loc.name) CONTAINS ext.translate({2})) "
          + " OR (at.text IS NULL AND pi2 IS NOT NULL AND ext.translate(pi2.name) CONTAINS ext.translate({2}))) "

          + " OPTIONAL MATCH (at)-[mb:MODERATED_BY]->(m:Person) "
          + " RETURN count(DISTINCT id(at)) ")
  Page<ParticipationDto> findByIdConferenceAndIdPersonAndText(Long idPerson, Long idConference, String text, Pageable pageable);
  
  
//participantes 
  
  @Query(" match (p:Person)-[:MADE]->(lo:Login)-[:TO]->(co:Conference),(p)-[:MADE]->(s:SelfDeclaration)-[:TO]->(co) "
	      + " WHERE id(co)={0} RETURN count(DISTINCT p)")
  Integer countParticipationAllOriginsByConference(Long idConference);
  
  
  
  @Query(" match (lo:Login)<-[:MADE]-(p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " + 
  		" where id(co) = {0} " + 
  		" AND lo.time > m.beginDate " + 
  		" AND lo.time < m.endDate " + 
  		" with collect(lo) as prLogin " + 
  		" match (p:Person)-[:MADE]->(l:Login)-[:TO]->(co:Conference),(p)-[:MADE]->(s:SelfDeclaration)-[:TO]->(co) " + 
  		" where id(co) = {0} " + 
  		" AND NOT l IN prLogin " + 
  		" return count (distinct p) ")
  Integer countParticipationRemoteOriginByConference(Long idConference);
  
  
  @Query(" match (p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " + 
  		"  where id(co) = {0} AND (({1} IS NULL) OR (id(m) IN {1})) " + 
  		"  return count(distinct p) ")
	  Integer countParticipationPresentialOriginByConference(Long idConference, List<Long> meetings );
	  
  
  //destaques
  

  @Query("  MATCH (co:Conference)<-[a:ABOUT]-(h:Highlight) " + 
  		"  WHERE id(co)={0} " + 
  		"  RETURN count(h) ")
  Integer countHighlightAllOriginsByConference(Long idConference);
  
  
  
  @Query(" match (h:Highlight)-[:ABOUT]->(co:Conference) " + 
  		"  where id(co) = {0} AND h.from='rem' " + 
  		"  return count (distinct h) ")
	  Integer countHighlightRemoteOriginByConference(Long idConference);
  
  
 
  @Query(" match (m:Meeting)<-[:DURING]-(h:Highlight)-[:ABOUT]->(co:Conference) " + 
	  		"  where id(co) = {0} AND h.from='pres' AND (({1} IS NULL) OR (id(m) IN {1}))  " + 
	  		"  return count (distinct h) ")
		  Integer countHighlightPresentialOriginByConference(Long idConference, List<Long> meetings);
  
  
 //propostas
  

  @Query(" MATCH (co:Conference)-[a:ABOUT]-(c:Comment) " + 
  		"  WHERE id(co)={0} AND c.status IN ['pub', 'arq'] " + 
  		"  RETURN count(c) ")
  Integer countCommentAllOriginsByConference(Long idConference);
  
  
  
  @Query(" match (c:Comment)-[:ABOUT]->(co:Conference) " + 
  		"  where id(co) = {0} AND c.from='rem' and c.status IN ['pub', 'arq'] " + 
  		"  return count (distinct c) ")
	  Integer countCommentRemoteOriginByConference(Long idConference);
  
  
 
  @Query(" match (m:Meeting)<-[:DURING]-(c:Comment)-[:ABOUT]->(co:Conference) " + 
	  		"  where id(co) = {0} AND c.from='pres' and c.status IN ['pub', 'arq'] AND (({1} IS NULL) OR (id(m) IN {1}))  " + 
	  		"  return count (distinct c) ")
		  Integer countCommentPresentialOriginByConference(Long idConference, List<Long> meetings);
  

  
//locality 
  
  @Query(" match (p:Person)-[:MADE]->(lo:Login)-[:TO]->(co:Conference)<-[:TO]-(s:SelfDeclaration)<-[m:MADE]-(p), (s)-[:AS_BEING_FROM]->(loc:Locality) " + 
  		  " WHERE id(co)={0} RETURN count(DISTINCT loc) ")
  Integer countLocalityAllOriginsByConference(Long idConference);
  
	 		 	 		 		
  @Query("match (lo:Login)<-[:MADE]-(p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " + 
  		" where id(co) = {0} AND lo.time > m.beginDate AND lo.time < m.endDate " + 
  		" with collect(lo) as prLogin " + 
  		" match (p:Person)-[:MADE]->(l:Login)-[:TO]->(co:Conference)<-[:TO]-(s:SelfDeclaration)<-[m:MADE]-(p), (s)-[:AS_BEING_FROM]->(loc:Locality) " + 
  		" where id(co) = {0} AND NOT l IN prLogin " + 
  		"return count (distinct loc) ")
  Integer countLocalityRemoteOriginByConference(Long idConference);
  
  
  @Query(" match (l:Locality)<-[:AS_BEING_FROM]-(s:SelfDeclaration)<-[:MADE]-(p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " + 
  		"  where id(co) = {0} AND (({1} IS NULL) OR (id(m) IN {1})) " + 
  		"  return count(distinct l) ")
	  Integer countLocalityPresentialOriginByConference(Long idConference, List<Long> meetings );
	  
  
  
  
  
}
