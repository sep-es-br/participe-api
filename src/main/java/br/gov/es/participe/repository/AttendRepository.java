package br.gov.es.participe.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.controller.dto.ParticipationDto;
import br.gov.es.participe.model.Attend;

public interface AttendRepository extends Neo4jRepository<Attend, Long>{

	@Query("Match(a)-[m:MADE_BY]->(p:Person)"
			+" Where id(p)={0} "
			+" return m, a")
	List<Attend> findAllAttendByIdperson(Long id);
	
	@Query(" MATCH (c:Conference)<-[t:TO]-(self:SelfDeclaration)<-[m:MADE]-(p:Person) "
			+" WHERE id(c)={0} RETURN COUNT(DISTINCT p)")
	Integer countParticipationByConference(Long idConference);
	
	@Query(value="MATCH (p:Person)<-[a:MADE_BY]-(at:Attend)-[ab:ABOUT]->(conf:Conference)"
			+" MATCH (at)-[ap:ABOUT]->(pi:PlanItem) "
			+" MATCH (at)-[al:ABOUT]->(loc:Locality)-[ot:OF_TYPE]->(lt:LocalityType) "
			+" WHERE id(p)={0} AND id(conf) = {1} "
			+" AND (at.status IS NULL OR NOT at.status IN ['rem', 'arq']) "

			+" OPTIONAL MATCH (pi)-[comp:COMPOSES*]->(pi2:PlanItem) "
			+" WITH p,at,conf,pi,loc,lt,pi2 "
			+" WHERE ({2} IS NULL OR (at.text IS NOT NULL AND ext.translate(at.text) CONTAINS ext.translate({2})) "
			+" OR (at.text IS NULL AND ext.translate(pi.name) CONTAINS ext.translate({2})) "
			+" OR (at.text IS NULL AND ext.translate(loc.name) CONTAINS ext.translate({2})) "
			+" OR (at.text IS NULL AND pi2 IS NOT NULL AND ext.translate(pi2.name) CONTAINS ext.translate({2}))) "

			+" OPTIONAL MATCH (at)-[mb:MODERATED_BY]->(m:Person) "
			+" RETURN DISTINCT id(at) AS id, at.text AS text, at.status AS status, at.time AS time, "
			+" at.classification AS classification, m.name AS moderatorName, mb.time as moderateTime, mb.finish as moderated, "
			+" pi AS planItem, loc AS locality, lt AS localityType, [(at)-[lk:LIKED_BY]->(plk:Person) | [plk]] AS personLiked  "
			+" ORDER BY at.time DESC ",
			countQuery="MATCH (p:Person)<-[a:MADE_BY]-(at:Attend)-[ab:ABOUT]->(conf:Conference)"
					+" MATCH (at)-[ap:ABOUT]->(pi:PlanItem) "
					+" MATCH (at)-[al:ABOUT]->(loc:Locality)-[ot:OF_TYPE]->(lt:LocalityType) "
					+" WHERE id(p)={0} AND id(conf) = {1} "
					+" AND (at.status IS NULL OR NOT at.status IN ['rem', 'arq']) "

					+" OPTIONAL MATCH (pi)-[comp:COMPOSES*]->(pi2:PlanItem) "
					+" WITH p,at,conf,pi,loc,lt,pi2 "
					+" WHERE ({2} IS NULL OR (at.text IS NOT NULL AND ext.translate(at.text) CONTAINS ext.translate({2})) "
					+" OR (at.text IS NULL AND ext.translate(pi.name) CONTAINS ext.translate({2})) "
					+" OR (at.text IS NULL AND ext.translate(loc.name) CONTAINS ext.translate({2})) "
					+" OR (at.text IS NULL AND pi2 IS NOT NULL AND ext.translate(pi2.name) CONTAINS ext.translate({2}))) "

					+" OPTIONAL MATCH (at)-[mb:MODERATED_BY]->(m:Person) "
					+" RETURN COUNT(DISTINCT id(at)) "
	)
	Page<ParticipationDto> findByIdConferenceAndIdPersonAndText(Long idPerson, Long idConference, String text, Pageable pageable);
}
