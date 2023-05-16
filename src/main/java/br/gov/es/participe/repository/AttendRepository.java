package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.ParticipationDto;
import br.gov.es.participe.model.Attend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendRepository extends Neo4jRepository<Attend, Long> {

  @Query("MATCH(a:Attend)-[m:MADE_BY]->(p:Person) "
      + "WHERE id(p)=$id "
      + "RETURN m, a, p")
  List<Attend> findAllAttendByIdPerson( @Param("id") Long id);

  @Query(" match (p:Person)-[:MADE]->(lo:Login)-[:TO]->(co:Conference),(p)-[:MADE]->(s:SelfDeclaration)-[:TO]->(co) "
       + " WHERE id(co)=$idConference RETURN count(DISTINCT p)")
  Integer countParticipationByConference( @Param("idConference") Long idConference);

  @Query(" MATCH (conf:Conference)<-[:TO]-(s:SelfDeclaration)<-[:MADE]-(p:Person) " +
      " OPTIONAL MATCH (conf)<-[:ABOUT]-(c:Comment)-[:MADE_BY]->(p) " +
      " OPTIONAL MATCH (conf)<-[:ABOUT]-(h:Highlight)-[:MADE_BY]->(p) " +
      " OPTIONAL MATCH (conf)<-[:OCCURS_IN]-(m:Meeting)<-[:CHECKED_IN_AT]-(p) " +
      " WITH conf, p, c, h, m " +
      " WHERE id(conf)=$idConference " +
      " AND ( " +
      " (c IS NOT NULL AND c.status='pub') " +
      " OR h IS NOT NULL " +
      " OR m IS NOT NULL " +
      " ) " +
      " AND (($type IS NULL) OR (c IS NULL OR c.type=$type)) " +
      " AND ( " +
      " (($type IS NULL OR $type='pre') AND (m IS NULL OR (m.typeMeetingEnum='PRESENCIAL' OR m.typeMeetingEnum='PRESENCIAL_VIRTUAL'))) OR " +
      " (($type IS NULL OR $type<>'pre') AND (m IS NULL OR (m.typeMeetingEnum<>'PRESENCIAL' AND m.typeMeetingEnum<>'PRESENCIAL_VIRTUAL'))) " +
      " ) " +
      " AND ($type <>'pre' OR m IS NULL OR $meetings IS NULL OR id(m) IN $meetings) " +
      " RETURN count(DISTINCT p) ")
  Integer countParticipationByConferenceAndType(@Param("idConference")Long idConference, @Param("type")String type, @Param("meetings")List<Long> meetings);

  @Query("MATCH (conf:Conference)<-[:TO]-(s:SelfDeclaration)<-[:MADE]-(p:Person) " +
  "OPTIONAL MATCH (conf)<-[:ABOUT]-(c:Comment)-[:MADE_BY]->(p) " +
  "OPTIONAL MATCH (conf)<-[:ABOUT]-(h:Highlight)-[:MADE_BY]->(p) " +
  "OPTIONAL MATCH (conf)<-[:OCCURS_IN]-(m:Meeting)<-[:CHECKED_IN_AT]-(p) " +
  "WITH conf, p, c, h, m " +
  "WHERE id(conf)=$idConference " +
  "AND ( " +
  " (c IS NOT NULL AND c.status='pub') " +
  " OR h IS NOT NULL " +
  " OR m IS NOT NULL " +
  ") " +
  "AND (($type IS NULL) OR (c IS NULL OR c.type=$type)) " +
  "AND ( " +
  " (($type IS NULL OR $type='pre') AND (m IS NULL OR (m.typeMeetingEnum='PRESENCIAL' OR m.typeMeetingEnum='PRESENCIAL_VIRTUAL'))) OR " +
  " (($type IS NULL OR $type<>'pre') AND (m IS NULL OR (m.typeMeetingEnum<>'PRESENCIAL' AND m.typeMeetingEnum<>'PRESENCIAL_VIRTUAL'))) " +
  ") " +
  "AND ($type<>'pre' OR m IS NULL OR $meetings IS NULL OR id(m) IN $meetings) " +
  "RETURN count(DISTINCT c)")
Integer countCommentByConferenceAndType(@Param("idConference")Long idConference, @Param("type")String type, @Param("meetings") List<Long> meetings);


  @Query("MATCH (l:Locality)<-[:AS_BEING_FROM]-(s:SelfDeclaration)-[:TO]->(conf:Conference), (s)<-[:MADE]-(p:Person) " +
  "OPTIONAL MATCH (conf)<-[:ABOUT]-(c:Comment)-[:MADE_BY]->(p) " +
  "OPTIONAL MATCH (conf)<-[:ABOUT]-(h:Highlight)-[:MADE_BY]->(p) " +
  "OPTIONAL MATCH (conf)<-[:OCCURS_IN]-(m:Meeting)<-[:CHECKED_IN_AT]-(p) " +
  "WITH l, s, conf, p, c, h, m " +
  "WHERE id(conf)=$idConference " +
  "AND ( " +
  " (c IS NOT NULL AND c.status='pub') " +
  " OR h IS NOT NULL " +
  " OR m IS NOT NULL " +
  ") " +
  "AND (($type IS NULL) OR (c IS NULL OR c.type=$type)) " +
  "AND ( " +
  " (($type IS NULL OR $type='pre') AND (m IS NULL OR (m.typeMeetingEnum='PRESENCIAL' OR m.typeMeetingEnum='PRESENCIAL_VIRTUAL'))) OR " +
  " (($type IS NULL OR $type<>'pre') AND (m IS NULL OR (m.typeMeetingEnum<>'PRESENCIAL' AND m.typeMeetingEnum<>'PRESENCIAL_VIRTUAL'))) " +
  ") " +
  "AND ($type<>'pre' OR m IS NULL OR $meetings IS NULL OR id(m) IN $meetings) " +
  "RETURN count(DISTINCT l)")
Integer countLocalityByConferenceAndType(@Param("idConference")Long idConference, @Param("type")String type, @Param("meetings") List<Long> meetings);

  @Query(value = "MATCH (p:Person)<-[a:MADE_BY]-(at:Attend)-[ab:ABOUT]->(conf:Conference)"
      + " MATCH (at)-[ap:ABOUT]->(pi:PlanItem) "
      + " OPTIONAL MATCH (at)-[al:ABOUT]->(loc:Locality)-[ot:OF_TYPE]->(lt:LocalityType) "
      + " WITH at, ap, al, loc, ot, lt, pi, p, conf "
      + " WHERE id(p)=$idPerson AND id(conf) = $idConference "
      + " AND (at.status IS NULL OR NOT at.status IN ['rem', 'arq']) "
      + " OPTIONAL MATCH (pi)-[comp:COMPOSES*]->(pi2:PlanItem) "
      + " WITH p,at,conf,pi,loc,lt,pi2 "
      + " WHERE ($text IS NULL OR (at.text IS NOT NULL AND at.text CONTAINS ($text)) "
      + " OR (at.text IS NULL AND pi.name CONTAINS ($text)) "
      + " OR (at.text IS NULL AND loc.name CONTAINS ($text)) "
      + " OR (at.text IS NULL AND pi2 IS NOT NULL AND pi2.name CONTAINS ($text))) "
      + " OPTIONAL MATCH (at)-[mb:MODERATED_BY]->(m:Person) "
      + " RETURN DISTINCT id(at) AS id, at.text AS text, at.status AS status, at.time AS time, "
      + " at.classification AS classification, m.name AS moderatorName, mb.time AS moderateTime, mb.finish AS moderated, "
      + " pi AS planItem, loc AS locality, lt AS localityType, [(at)-[lk:LIKED_BY]->(plk:Person) | [plk]] AS personLiked  "
      + " ORDER BY at.time DESC ",
      countQuery = "MATCH (p:Person)<-[a:MADE_BY]-(at:Attend)-[ab:ABOUT]->(conf:Conference)"
          + " MATCH (at)-[ap:ABOUT]->(pi:PlanItem) "
          + " OPTIONAL MATCH (at)-[al:ABOUT]->(loc:Locality)-[ot:OF_TYPE]->(lt:LocalityType) "
          + " WITH at, ap, al, loc, ot, lt, pi, p, conf "
          + " WHERE id(p)=$idPerson AND id(conf) = $idConference "
          + " AND (at.status IS NULL OR NOT at.status IN ['rem', 'arq']) "

          + " OPTIONAL MATCH (pi)-[comp:COMPOSES*]->(pi2:PlanItem) "
          + " WITH p,at,conf,pi,loc,lt,pi2 "
          + " WHERE ($text IS NULL OR (at.text IS NOT NULL AND apoc.text.clean(at.text) CONTAINS apoc.text.clean($text)) "
          + " OR (at.text IS NULL AND apoc.text.clean(pi.name) CONTAINS apoc.text.clean($text)) "
          + " OR (at.text IS NULL AND apoc.text.clean(loc.name) CONTAINS apoc.text.clean($text)) "
          + " OR (at.text IS NULL AND pi2 IS NOT NULL AND apoc.text.clean(pi2.name) CONTAINS apoc.text.clean($text))) "

          + " OPTIONAL MATCH (at)-[mb:MODERATED_BY]->(m:Person) "
          + " RETURN count(DISTINCT id(at)) ")
  Page<ParticipationDto> findByIdConferenceAndIdPersonAndText( @Param("idPerson")  Long idPerson, @Param("idConference") Long idConference,
                                                               @Param("text") String text,   @Param("pageable") Pageable pageable);
  
  
//participantes 
  
/* 
  @Query(" match (p:Person)-[:MADE]->(lo:Login)-[:TO]->(co:Conference),(p)-[:MADE]->(s:SelfDeclaration)-[:TO]->(co) "
	      + " WHERE id(co)={0} RETURN count(DISTINCT p)")
  Integer countParticipationAllOriginsByConference(Long idConference);
  */
  
@Query(" match (co:Conference)<-[:TO]-(s:SelfDeclaration)<-[:MADE]-(p:Person) " +
       " where id(co) = $idConference " +
       " optional match (p)-[:MADE]->(lo:Login)-[:TO]->(co) " +
       " optional match (p)-[:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co) " +
       " with count(lo) as countlo, count(m) as countm, p as participant " +
       " where countlo > 0 or countm > 0 " +
       " return count(distinct participant) ")
Integer countParticipationAllOriginsByConference( @Param("idConference") Long idConference);

  
  @Query(" match (lo:Login)<-[:MADE]-(p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " + 
  		" where id(co) = $idConference " + 
  		" AND lo.time > m.beginDate " + 
  		" AND lo.time < m.endDate " + 
  		" with collect(lo) as prLogin " + 
  		" match (p:Person)-[:MADE]->(l:Login)-[:TO]->(co:Conference),(p)-[:MADE]->(s:SelfDeclaration)-[:TO]->(co) " + 
  		" where id(co) = $idConference " + 
  		" AND NOT l IN prLogin " + 
  		" return count (distinct p) ")
  Integer countParticipationRemoteOriginByConference( @Param("idConference") Long idConference);
  
  

   //Total de participantes
/*   @Query(" match (p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " + 
  		"  where id(co) = $idConference AND (($meetings IS NULL) OR (id(m) IN $meetings)) " + 
  		"  return count(distinct p) ")
	  Integer countParticipationPresentialOriginByConference( @Param("idConference") Long idConference, @Param("meetings") List<Long> meetings );
	 */ 
    @Query(" optional match (p:Person)-[:MADE]->(l:Login)-[:TO]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting) " +  
          " where id(co) = $idConference " + 
          " AND m.attendanceListMode = 'MANUAL'  " + 
          " AND l.time >= m.beginDate and l.time <= m.endDate " + 
          " AND (($meetings IS NULL) OR (id(m) IN $meetings)) " + 
          " with collect(p) as plogged " + 
          " optional match (co:Conference)<-[:OCCURS_IN]-(m:Meeting)<-[:CHECKED_IN_AT]-(p:Person) " + 
          " where   id(co) = $idConference and  m.attendanceListMode = 'AUTO'  " + 
          " AND (($meetings IS NULL) OR (id(m) IN $meetings)) " + 
          " with plogged + collect(p) as allp  " + 
          " unwind allp as np " + 
          " return count(DISTINCT np)  ")
  Integer countParticipationPresentialOriginByConference( @Param("idConference") Long idConference, @Param("meetings") List<Long> meetings );



  //Total de destaques
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
		  Integer countHighlightPresentialOriginByConference( @Param("idConference") Long idConference, @Param("meetings") List<Long> meetings);
  
  
 //propostas
  

  @Query(" MATCH (co:Conference)-[a:ABOUT]-(c:Comment) " + 
  		"  WHERE id(co)=$idConference AND c.status IN ['pub', 'arq'] " + 
  		"  RETURN count(c) ")
  Integer countCommentAllOriginsByConference( @Param("idConference") Long idConference);
  
  
  
  @Query(" match (c:Comment)-[:ABOUT]->(co:Conference) " + 
  		"  where id(co) = $idConference AND c.from='rem' and c.status IN ['pub', 'arq'] " + 
  		"  return count (distinct c) ")
	  Integer countCommentRemoteOriginByConference( @Param("idConference") Long idConference);
  
  
  // Total de propostas
 /*  @Query(" match (m:Meeting)<-[:DURING]-(c:Comment)-[:ABOUT]->(co:Conference) " + 
	  		"  where id(co) = $idConference AND c.from='pres' and c.status IN ['pub', 'arq'] AND (($meetings IS NULL) OR (id(m) IN $meetings))  " + 
	  		"  return count (distinct c) ")
		  Integer countCommentPresentialOriginByConference( @Param("idConference") Long idConference,  @Param("meetings") List<Long> meetings);
  */

      @Query( " optional match (p:Person)<-[:MADE_BY]-(c:Comment)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting) " + 
              " where id(co) = $idConference AND " + 
              " m.attendanceListMode = 'MANUAL'  AND " + 
              " c.time >= m.beginDate and c.time <= m.endDate AND (($meetings IS NULL) OR (id(m) IN $meetings)) " + 
              " AND c.status IN ['pub', 'arq'] " + 
              " with collect(c) as plogged  " + 
              " optional match (p:Person)<-[:MADE_BY]-(c:Comment)-[:ABOUT]->(co:Conference)<-[:OCCURS_IN]-(m:Meeting) " + 
              " where id(co) = $idConference AND m.attendanceListMode = 'AUTO' AND  c.from='pres' and (m)<-[:DURING]-(c)  " + 
              " AND c.status IN ['pub', 'arq']  " + 
              " AND (($meetings IS NULL) OR (id(m) IN $meetings)) " + 
              " with plogged + collect(c) as allp  " + 
              " unwind allp as np " + 
              " return count (distinct np) ")
    Integer countCommentPresentialOriginByConference( @Param("idConference") Long idConference,  @Param("meetings") List<Long> meetings);

  
//locality 
  
  @Query(" match (p:Person)-[:MADE]->(lo:Login)-[:TO]->(co:Conference)<-[:TO]-(s:SelfDeclaration)<-[m:MADE]-(p), (s)-[:AS_BEING_FROM]->(loc:Locality) " + 
  		  " WHERE id(co)=$idConference RETURN count(DISTINCT loc) ")
  Integer countLocalityAllOriginsByConference( @Param("idConference") Long idConference);
  
	 		 	 		 		
  @Query("match (lo:Login)<-[:MADE]-(p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " + 
  		" where id(co) = $idConference AND lo.time > m.beginDate AND lo.time < m.endDate " + 
  		" with collect(lo) as prLogin " + 
  		" match (p:Person)-[:MADE]->(l:Login)-[:TO]->(co:Conference)<-[:TO]-(s:SelfDeclaration)<-[m:MADE]-(p), (s)-[:AS_BEING_FROM]->(loc:Locality) " + 
  		" where id(co) = $idConference AND NOT l IN prLogin " + 
  		"return count (distinct loc) ")
  Integer countLocalityRemoteOriginByConference( @Param("idConference") Long idConference);
  
  // Total de municipios
 /*  @Query(" match (l:Locality)<-[:AS_BEING_FROM]-(s:SelfDeclaration)<-[:MADE]-(p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " + 
  		"  where id(co) = $idConference AND (($meetings IS NULL) OR (id(m) IN $meetings)) " + 
  		"  return count(distinct l) ")
	  Integer countLocalityPresentialOriginByConference( @Param("idConference") Long idConference, @Param("meetings") List<Long> meetings );
	  
  */
    
  @Query( " optional match (l:Locality)<-[:AS_BEING_FROM]-(s:SelfDeclaration)<-[:MADE]-(p:Person)-[c:CHECKED_IN_AT]->(m:Meeting)-[:OCCURS_IN]->(co:Conference) " + 
          " where id(co) = $idConference  AND " + 
          " (m.attendanceListMode = 'AUTO') and (($meetings IS NULL) OR (id(m) IN $meetings)) " + 
          " with collect(l) as plogged  " + 
          " optional match (co:Conference)<-[:TO]-(lo:Login)<-[:MADE]-(p:Person)-[:MADE]->(s:SelfDeclaration)-[:TO]->(co) " + 
          " ,(l:Locality)<-[:AS_BEING_FROM]-(s) " + 
          " ,(co)<-[:OCCURS_IN]-(m:Meeting) " + 
          " where id(co) = $idConference  AND " + 
          " ((m.attendanceListMode = 'MANUAL' AND lo.time >= m.beginDate and lo.time <= m.endDate)) " + 
          " AND (($meetings IS NULL) OR (id(m) IN $meetings)) " + 
          " with plogged + collect(l)  as allp  " + 
          " unwind allp as np " + 
          " return count (distinct np) ")
  Integer countLocalityPresentialOriginByConference( @Param("idConference") Long idConference, @Param("meetings") List<Long> meetings );
  
  
}
