package br.gov.es.participe.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.controller.dto.ModerationResultDto;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.StructureItem;

public interface CommentRepository extends Neo4jRepository<Comment, Long>{
	
	@Query("MATCH  (p:Person)<-[a:MADE_BY]-(c:Comment)"
			+" OPTIONAL MATCH (conf:Conference)-[ab:ABOUT]-(c)"
			+" WHERE id(p)={0} AND (id(conf) = {1} OR {1} IS NULL )"
			+" Return c")
	List<Comment> findByIdPerson(Long idPerson, Long idConference);
	
	@Query("MATCH (co:Conference)<-[:ABOUT]-(c:Comment)-[:ABOUT]->(pi:PlanItem)-[:COMPOSES]->(pi2:PlanItem) "
			+" , (c)-[:MADE_BY]->(p:Person) "
			+" , (c)-[:ABOUT]->(l:Locality) "
			+" WHERE id(p)={0} And (id(pi)={1} OR id(pi2)={1}) AND id(co)={2} AND id(l) = {3} "
			+" RETURN c")
	List<Comment> findByIdPersonAndIdPlanItemAndIdConferenceAndIdLoclity(Long idPerson, Long idPlanItem, Long idConference, Long idLocality);
	
	@Query(value="MATCH (loc:Locality)<-[a:ABOUT]-(comment:Comment)-[:ABOUT]->(c:Conference)-[t:TARGETS]-(p:Plan)<-[comP:COMPOSES]-(parent:PlanItem)-[comC:COMPOSES*]-(child:PlanItem) "
			+" WHERE id(c)={0} AND comment.status CONTAINS {1} AND (id(parent) IN {4} OR NOT {4}) AND (id(loc) IN {3} OR NOT {3}) AND (((child)-[:ABOUT]-(comment)) OR ((parent)-[:ABOUT]-(comment))) "
			+" WITH loc,a,comment,p "
			+" MATCH (comment)-[:MADE_BY]->(person:Person) "
			+" MATCH (comment)-[:ABOUT]->(pi2:PlanItem) "
			+" OPTIONAL MATCH (pi2)-[:COMPOSES*]->(pi3:PlanItem)-[:COMPOSES]->(p) "
			+" WITH loc,a,comment,person,pi2,COLLECT(pi3) as listPi3,p "
			+" WHERE ext.translate(comment.text) CONTAINS ext.translate({2}) "
			+" OR ext.translate(person.name) CONTAINS ext.translate({2}) "
			+" OR ext.translate(loc.name) CONTAINS ext.translate({2}) "
			+" OR ext.translate(pi2.name) CONTAINS ext.translate({2}) "
			+" OR listPi3 IS NOT NULL AND any(piIte in listPi3 WHERE ext.translate(piIte.name) CONTAINS ext.translate({2})) "
			+" RETURN DISTINCT comment,a,loc",
			countQuery = "MATCH (loc:Locality)<-[a:ABOUT]-(comment:Comment)-[:ABOUT]->(c:Conference)-[t:TARGETS]-(p:Plan)<-[comP:COMPOSES]-(parent:PlanItem)-[comC:COMPOSES*]-(child:PlanItem) "
					+" WHERE id(c)={0} AND comment.status CONTAINS {1} AND (id(parent) IN {4} OR NOT {4}) AND (id(loc) IN {3} OR NOT {3}) AND (((child)-[:ABOUT]-(comment)) OR ((parent)-[:ABOUT]-(comment))) "
					+" WITH loc,a,comment,p "
					+" MATCH (comment)-[:MADE_BY]->(person:Person) "
					+" MATCH (comment)-[:ABOUT]->(pi2:PlanItem) "
					+" OPTIONAL MATCH (pi2)-[:COMPOSES*]->(pi3:PlanItem)-[:COMPOSES]->(p) "
					+" WITH loc,a,comment,person,pi2,COLLECT(pi3) as listPi3,p "
					+" WHERE ext.translate(comment.text) CONTAINS ext.translate('educ') "
					+" OR ext.translate(person.name) CONTAINS ext.translate({2}) "
					+" OR ext.translate(loc.name) CONTAINS ext.translate('educ') "
					+" OR ext.translate(pi2.name) CONTAINS ext.translate('educ') "
					+" OR listPi3 IS NOT NULL AND any(piIte in listPi3 WHERE ext.translate(piIte.name) CONTAINS ext.translate('educ')) "
					+" RETURN COUNT(DISTINCT comment)")
	Page<Comment> findAllCommentsByConference(Long idConference, String status, String text, Long[] localityIds, Long[] planItemIds, Pageable pageable);
	
	@Query(" MATCH (co:Conference)-[a:ABOUT]-(c:Comment) "
			+" WHERE id(co)={0} AND NOT c.status IN ['rem', 'pen']"
			+" RETURN COUNT(c)")
	Integer countCommentByConference(Long id);

	@Query(
			"MATCH (con:Conference)<-[:ABOUT]-(c:Comment)-[:MADE_BY]->(p:Person)" +
			", (c)-[:ABOUT]->(loc:Locality) " +
			", (c)-[:ABOUT]->(pi:PlanItem)  " +
			", (con)-[trg:TARGETS]->(plan:Plan)  " +
			", (pi)-[:OBEYS]->(si:StructureItem) " +
			", (pi)<-[]->(pi2:PlanItem) " +
			"WHERE (c.status IN {0} OR NOT {0}) AND (c.type CONTAINS {1} OR {1} IS NULL)  AND id(con)={4} " +
			"AND (id(loc) IN {2} OR NOT {2}) " +
			"AND ((id(pi) IN {3} OR id(pi2) IN {3}) OR NOT {3}) " +
			"AND (id(si) IN {5} OR NOT {5}) " +
			"OPTIONAL MATCH (c)-[mb:MODERATED_BY]->(m:Person) " +
			"OPTIONAL MATCH (plan)<-[:COMPOSES]-(piArea:PlanItem)-[]-(pi) " +
			"RETURN DISTINCT id(con) AS conferenceId, id(c) AS commentId, c.status AS status, c.text AS text, c.time AS time, " +
			"c.type AS type, p.name AS citizenName, m.name AS moderatorName, mb.time as moderateTime, mb.finish as moderated, " +
			"id(m) as moderatorId, id(loc) as localityId, loc.name as localityName, c.classification AS classification, " +
			"id(pi) as planItemId, pi.name as planItemName, id(si) AS structureItemId, si.name AS structureItemName, " +
			"id(piArea) AS areaEstrategicaId, piArea.name AS nameAreaEstrategica ")
	List<ModerationResultDto> findAllByStatus(String[] status, String type, Long[] localityIds, Long[] planItemIds,
												  Long conferenceId, Long[] structureItemIds);

	@Query(
		"MATCH (com:Comment)-[ab:ABOUT]->(pi1:PlanItem), " +
		"(com)-[:ABOUT]->(conf:Conference) " +
		"WHERE id(com)={0} AND id(conf)={1} " +
		"MATCH (com)-[:ABOUT]->(loc:Locality) " +
		"OPTIONAL MATCH (com)-[:MADE_BY]->(p:Person) " +
		"OPTIONAL MATCH (com)-[:MODERATED_BY]->(m:Person) " +
		"OPTIONAL MATCH (loc)<-[locin:IS_LOCATED_IN*]-(loc2:Locality) " +
		"-[oftp:OF_TYPE]->(locType:LocalityType) " +
		"RETURN DISTINCT id(com) AS commentId, com.status as status, com.text as text, com.time as time, com.type as type, " +
		"p.name as citizenName, m.name as moderatorName, loc.name as localityName, locType.name as localityType, " +
		"com.classification as classification, id(loc) as localityId"
	)
	ModerationResultDto findModerationResultById(Long idComment, Long idConference);

	@Query(
		"MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
		"WHERE id(comm)={0} " +
		"RETURN pi1 AS planItems " +
		"UNION MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
		"WHERE id(comm)={0} " +
		"OPTIONAL MATCH (pi1)-[:COMPOSES*]->(pi2:PlanItem) " +
		"RETURN pi2 AS planItems"
	)
	Collection<PlanItem> findModerationPlanItemsByCommentId(Long idComment);

	@Query(
		"MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
		"WHERE id(comm)={0} " +
		"MATCH (pi1)-[:OBEYS]->(si1:StructureItem) " +
		"RETURN si1 as structureItems " +
		"UNION MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
		"WHERE id(comm)={0} " +
		"MATCH (pi1)-[:OBEYS]->(si1:StructureItem) " +
		"OPTIONAL MATCH (pi1)-[:COMPOSES*]->(pi2:PlanItem) " +
		"OPTIONAL MATCH (pi2)-[:OBEYS]->(si2:StructureItem) " +
		"RETURN si2 AS structureItems"
	)
	Collection<StructureItem> findModerationStructureItemsByCommentId(Long idComment);
	
	@Query(" MATCH (c:Comment) "
			+" WHERE id(c)={0} "
			+" RETURN c "
			+" ,[ "
			+"		[(c)-[lk:LIKED_BY]->(p:Person) | [lk,p]],"
			+"		[(c)-[mb:MADE_BY]->(pe:Person) | [mb,pe]],"
			+"  	[(c)-[about:ABOUT]-(planI:PlanItem) | [about,planI]], "
			+"		[(l:Locality)<-[a:ABOUT]-(c) | [a,l]]"
			+" ]")
	Comment findPersonLiked(Long idComment);
}
