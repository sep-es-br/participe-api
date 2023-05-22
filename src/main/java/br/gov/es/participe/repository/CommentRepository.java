package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.ModerationResultDto;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.StructureItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends Neo4jRepository<Comment, Long> {

    String WHERE_FILTER = "WHERE "
    + "id(conference)=$idConference "
    + "AND comment.status CONTAINS $status "
    + "AND (id(parent) IN $planItemIds OR id(child) IN $planItemIds OR NOT $planItemIds) "
    + "AND (id(locality) IN $localityIds OR NOT $localityIds) "
    + "AND (((child)-[:ABOUT]-(comment)) OR ((parent)-[:ABOUT]-(comment))) ";
String WHERE_TEXT_FILTER = "WHERE apoc.text.clean(comment.text) CONTAINS apoc.text.clean($text) "
    + "OR apoc.text.clean(person.name) CONTAINS apoc.text.clean($text) "
    + "OR apoc.text.clean(locality.name) CONTAINS apoc.text.clean($text) "
    + "OR apoc.text.clean(planItem.name) CONTAINS apoc.text.clean($text) "
    + "OR listPi3 IS NOT NULL "
    + "AND any(piIte IN listPi3 WHERE apoc.text.clean(piIte.name) CONTAINS apoc.text.clean($text)) ";

@Query("MATCH (p:Person)<-[a:MADE_BY]-(c:Comment)"
    + " MATCH (conf:Conference)-[ab:ABOUT]-(c)"
    + " WHERE id(p)=$idPerson AND (id(conf) = $idConference OR $idConference IS NULL )"
    + " RETURN c")
List<Comment> findByIdPerson( @Param("idPerson") Long idPerson, @Param("idConference") Long idConference);

@Query("MATCH (co:Conference)<-[:ABOUT]-(c:Comment)-[:ABOUT]->(pi:PlanItem), " +
    "(c)-[:MADE_BY]->(p:Person) " +
    "OPTIONAL MATCH (c)-[:ABOUT]->(l:Locality) " +
    "OPTIONAL MATCH (pi)-[:COMPOSES]->(pi2:PlanItem) " +
    "WITH c, co, pi, pi2, p, l " +
    "WHERE (id(p)=$idPerson OR $idPerson IS NULL) " +
    "AND (id(pi)=$idPlanItem OR id(pi2)=$idPlanItem) " +
    "AND id(co)=$idConference " +
    "AND (id(l) = $idLocality OR $idLocality IS NULL) " +
    "AND (c.status <> 'rem')" +
    "RETURN c, co, pi, pi2, p, l")
List<Comment> findByIdPersonAndIdPlanItemAndIdConferenceAndIdLocality( @Param("idPerson") Long idPerson, @Param("idPlanItem") Long idPlanItem, @Param("idConference") Long idConference, @Param("idLocality") Long idLocality);

@Query(value =
    "MATCH (comment:Comment)-[:ABOUT]->(conference:Conference), "
        + "(conference)-[target:TARGETS]-(plan:Plan), "
        + "(comment)-[ab1:ABOUT]->(child:PlanItem)-[composesChild:COMPOSES*0..]->(parent:PlanItem) "
        + "OPTIONAL MATCH (comment)-[aboutLocality:ABOUT]->(locality:Locality)-[ofType:OF_TYPE]->(localityType) "
        + "WITH locality, aboutLocality, comment, plan, parent, composesChild, child, ofType, localityType, conference "
        + WHERE_FILTER
        + "MATCH (comment)-[madeBy:MADE_BY]->(person:Person)-[made:MADE]->(selfDeclaration:SelfDeclaration) "
        + "MATCH (comment)-[aboutPlanItem:ABOUT]->(planItem:PlanItem) "
        + "optional MATCH (localitySD:Locality)<-[asBeingFrom:AS_BEING_FROM]-(selfDeclaration:SelfDeclaration)-[to:TO]->(conference) "
        + "OPTIONAL MATCH (planItem:PlanItem)-[obeys:OBEYS]->(structureItem:StructureItem) "
        + "OPTIONAL MATCH (comment)-[likedBy:LIKED_BY]->(personLikedBy:Person) "
        + "OPTIONAL MATCH (planItem)-[:COMPOSES*]->(pi3:PlanItem)-[:COMPOSES]->(plan) "
        + "WITH likedBy, personLikedBy, locality, aboutLocality, comment, madeBy, person, planItem, collect(pi3) AS listPi3, "
        + "aboutPlanItem, plan, obeys, structureItem, ofType, localityType, made, localitySD, asBeingFrom, selfDeclaration, to, conference "
        + WHERE_TEXT_FILTER
        + "RETURN comment, aboutLocality, locality, person, madeBy, ofType, localityType, "
        + "aboutPlanItem, planItem, obeys, structureItem, likedBy, personLikedBy, made,  localitySD, asBeingFrom, selfDeclaration, to, conference ",
    countQuery =
        "MATCH (comment:Comment)-[:ABOUT]->(conference:Conference), "
            + "(conference)-[target:TARGETS]-(plan:Plan), "
            + "(comment)-[ab1:ABOUT]->(child:PlanItem)-[composesChild:COMPOSES*]->(parent:PlanItem) "
            + "OPTIONAL MATCH (comment)-[aboutLocality:ABOUT]->(locality:Locality)-[ofType:OF_TYPE]->(localityType) "
            + "WITH locality, aboutLocality, comment, plan, parent, composesChild, child, ofType, localityType, conference "
            + WHERE_FILTER
            + "MATCH (comment)-[madeBy:MADE_BY]->(person:Person)-[made:MADE]->(selfDeclaration:SelfDeclaration) "
            + "MATCH (comment)-[aboutPlanItem:ABOUT]->(planItem:PlanItem) "
            + "optional MATCH (localitySD:Locality)<-[asBeingFrom:AS_BEING_FROM]-(selfDeclaration:SelfDeclaration)-[to:TO]->(conference) "
            + "OPTIONAL MATCH (planItem:PlanItem)-[obeys:OBEYS]->(structureItem:StructureItem) "
            + "OPTIONAL MATCH (comment)-[likedBy:LIKED_BY]->(personLikedBy:Person) "
            + "OPTIONAL MATCH (planItem)-[:COMPOSES*]->(pi3:PlanItem)-[:COMPOSES]->(plan) "
            + "WITH likedBy, personLikedBy, locality, aboutLocality, comment, madeBy, person, planItem, collect(pi3) AS listPi3, "
            + "aboutPlanItem, plan, obeys, structureItem, ofType, localityType, made,  localitySD, asBeingFrom, selfDeclaration, to, conference "
            + WHERE_TEXT_FILTER
            + "RETURN count(DISTINCT comment)"
)
Page<Comment> findAllCommentsByConference( @Param("idConference") Long idConference, @Param("status") String status, @Param("text") String text, @Param("localityIds") Long[] localityIds, @Param("planItemIds") Long[] planItemIds, @Param("pageable") Pageable pageable);

@Query(" MATCH (co:Conference)-[a:ABOUT]-(c:Comment) "
    + " WHERE id(co)=$id AND NOT c.status IN ['rem', 'pen']"
    + " RETURN count(c)")
Integer countCommentByConference( @Param("id")Long id);

@Query("MATCH (con:Conference)<-[:ABOUT]-(c:Comment)-[:MADE_BY]->(p:Person)" +
    ", (c)-[:ABOUT]->(pi:PlanItem)  " +
    ", (con)-[trg:TARGETS]->(plan:Plan)  " +
    ", (pi)-[:OBEYS]->(si:StructureItem) " +
    "OPTIONAL MATCH (pi)<-[]->(pi2:PlanItem) " +
    "OPTIONAL MATCH (c)-[:ABOUT]->(loc:Locality) " +
    "WITH con, c, p, pi, plan, si, pi2, loc " +
    "WHERE (c.status IN $status OR $status = [] OR $status is NULL) AND (c.from = $from OR $from IS NULL) AND id(con)=$conferenceId " +
    "AND (id(loc) IN $localityIds OR $localityIds = [] OR $localityIds is NULL) " +
    "AND ((id(pi) IN $planItemIds OR id(pi2) IN $planItemIds) OR $planItemIds = [] OR $planItemIds is NULL) " +
    "AND (id(si) IN $structureItemIds OR $structureItemIds = [] OR $structureItemIds is NULL) " +
    "OPTIONAL MATCH (c)-[mb:MODERATED_BY]->(m:Person) " +
    "OPTIONAL MATCH (plan)<-[:COMPOSES]-(piArea:PlanItem)-[]-(pi) " +
    "RETURN DISTINCT id(con) AS conferenceId, id(c) AS commentId, c.status AS status, c.text AS text, c.time AS time, " +
    "c.type AS type, p.name AS citizenName, m.name AS moderatorName, mb.time AS moderateTime, mb.finish AS moderated, " +
    "id(m) AS moderatorId, id(loc) AS localityId, loc.name AS localityName, c.from as from, " +
    "id(pi) AS planItemId, pi.name AS planItemName, id(si) AS structureItemId, si.name AS structureItemName, " +
    "id(piArea) AS areaEstrategicaId, piArea.name AS nameAreaEstrategica "+
    "ORDER BY c.time")
List<ModerationResultDto> findAllByStatus(
  @Param("status") String[] status,
  @Param("from") String from,
  @Param("localityIds") Long[] localityIds,
  @Param("planItemIds") Long[] planItemIds,
  @Param("conferenceId") Long conferenceId,
  @Param("structureItemIds") Long[] structureItemIds);

@Query("MATCH (com:Comment)-[ab:ABOUT]->(pi1:PlanItem), " +
    "(com)-[:ABOUT]->(conf:Conference) " +
    "WHERE id(com)=$idComment AND id(conf)=$idConference " +
    "OPTIONAL MATCH (com)-[:ABOUT]->(loc:Locality) " +
    "OPTIONAL MATCH (com)-[:MADE_BY]->(p:Person) " +
    "OPTIONAL MATCH (com)-[:MODERATED_BY]->(m:Person) " +
    "OPTIONAL MATCH (loc)<-[locin:IS_LOCATED_IN*]-(loc2:Locality)-[oftp:OF_TYPE]->(locType:LocalityType) " +
    "RETURN DISTINCT id(com) AS commentId, com.status AS status, com.text AS text, com.time AS time, com.type AS type, " +
    "p.name AS citizenName, m.name AS moderatorName, loc.name AS localityName, locType.name AS localityType, " +
    "com.from as from ,id(loc) AS localityId")
ModerationResultDto findModerationResultById( @Param("idComment") Long idComment, @Param("idConference") Long idConference);

@Query("MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
    "WHERE id(comm)=$idComment " +
    "RETURN pi1 AS planItems " +
    "UNION MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
    "WHERE id(comm)=$idComment " +
    "OPTIONAL MATCH (pi1)-[:COMPOSES*]->(pi2:PlanItem) " +
    "RETURN pi2 AS planItems")
Collection<PlanItem> findModerationPlanItemsByCommentId( @Param("idComment") Long idComment);

@Query("MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
    "WHERE id(comm)=$idComment " +
    "MATCH (pi1)-[:OBEYS]->(si1:StructureItem) " +
    "RETURN si1 AS structureItems " +
    "UNION MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
    "WHERE id(comm)=$idComment " +
    "MATCH (pi1)-[:OBEYS]->(si1:StructureItem) " +
    "OPTIONAL MATCH (pi1)-[:COMPOSES*]->(pi2:PlanItem) " +
    "OPTIONAL MATCH (pi2)-[:OBEYS]->(si2:StructureItem) " +
    "RETURN si2 AS structureItems")
Collection<StructureItem> findModerationStructureItemsByCommentId( @Param("idComment") Long idComment);

@Query(" MATCH (c:Comment) "
    + " WHERE id(c)=$idComment "
    + " RETURN c "
    + " ,[ "
    + "		[(c)-[lk:LIKED_BY]->(p:Person) | [lk,p]],"
    + "		[(c)-[mb:MADE_BY]->(pe:Person) | [mb,pe]],"
    + "  	[(c)-[about:ABOUT]-(planI:PlanItem) | [about,planI]], "
    + "		[(l:Locality)<-[a:ABOUT]-(c) | [a,l]]"
    + " ]")
Comment findPersonLiked( @Param("idComment") Long idComment);

@Query("MATCH (p:Person)<-[a:MADE_BY]-(c:Comment) "
    + "WHERE id(p)=$personId "
    + "RETURN c, p, a"
)
List<Comment> findAllCommentsMadeByPerson( @Param("personId") Long personId);

@Query("MATCH (p:Person)<-[a:LIKED_BY]-(c:Comment) "
    + "WHERE id(p)=$personId "
    + "RETURN c, a, p"
)
List<Comment> findAllCommentsLikedByPerson( @Param("personId") Long personId);

}
