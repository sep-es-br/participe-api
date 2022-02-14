package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.ModerationResultDto;
import br.gov.es.participe.model.Comment;
import br.gov.es.participe.model.PlanItem;
import br.gov.es.participe.model.StructureItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;
import java.util.List;

public interface CommentRepository extends Neo4jRepository<Comment, Long> {

  String WHERE_FILTER = "WHERE "
      + "id(conference)={0} "
      + "AND comment.status CONTAINS {1} "
      + "AND (id(parent) IN {4} OR id(child) IN {4} OR NOT {4}) "
      + "AND (id(locality) IN {3} OR NOT {3}) "
      + "AND (((child)-[:ABOUT]-(comment)) OR ((parent)-[:ABOUT]-(comment))) ";
  String WHERE_TEXT_FILTER = "WHERE ext.translate(comment.text) CONTAINS ext.translate({2}) "
      + "OR ext.translate(person.name) CONTAINS ext.translate({2}) "
      + "OR ext.translate(locality.name) CONTAINS ext.translate({2}) "
      + "OR ext.translate(planItem.name) CONTAINS ext.translate({2}) "
      + "OR listPi3 IS NOT NULL "
      + "AND any(piIte IN listPi3 WHERE ext.translate(piIte.name) CONTAINS ext.translate({2})) ";

  @Query("MATCH  (p:Person)<-[a:MADE_BY]-(c:Comment)"
      + " OPTIONAL MATCH (conf:Conference)-[ab:ABOUT]-(c)"
      + " WHERE id(p)={0} AND (id(conf) = {1} OR {1} IS NULL )"
      + " RETURN c")
  List<Comment> findByIdPerson(Long idPerson, Long idConference);

  @Query("MATCH (co:Conference)<-[:ABOUT]-(c:Comment)-[:ABOUT]->(pi:PlanItem), " +
      "(c)-[:MADE_BY]->(p:Person) " +
      "OPTIONAL MATCH (c)-[:ABOUT]->(l:Locality) " +
      "OPTIONAL MATCH (pi)-[:COMPOSES]->(pi2:PlanItem) " +
      "WITH c, co, pi, pi2, p, l " +
      "WHERE (id(p)={0} OR {0} IS NULL) " +
      "AND (id(pi)={1} OR id(pi2)={1}) " +
      "AND id(co)={2} " +
      "AND (id(l) = {3} OR {3} IS NULL) " +
      "AND (c.status <> 'rem')" +
      "RETURN c, co, pi, pi2, p, l")
  List<Comment> findByIdPersonAndIdPlanItemAndIdConferenceAndIdLocality(Long idPerson, Long idPlanItem, Long idConference, Long idLocality);

  @Query(value =
      "MATCH (comment:Comment)-[:ABOUT]->(conference:Conference), "
          + "(conference)-[target:TARGETS]-(plan:Plan), "
          + "(comment)-[ab1:ABOUT]->(child:PlanItem)-[composesChild:COMPOSES*]->(parent:PlanItem) "
          + "OPTIONAL MATCH (comment)-[aboutLocality:ABOUT]->(locality:Locality)-[ofType:OF_TYPE]->(localityType) "
          + "WITH locality, aboutLocality, comment, plan, parent, composesChild, child, ofType, localityType, conference "
          + WHERE_FILTER
          + "MATCH (comment)-[madeBy:MADE_BY]->(person:Person)-[made:MADE]->(selfDeclaration:SelfDeclaration) "
          + "MATCH (comment)-[aboutPlanItem:ABOUT]->(planItem:PlanItem) "
          + "MATCH (localitySD:Locality)<-[asBeingFrom:AS_BEING_FROM]-(selfDeclaration:SelfDeclaration)-[to:TO]->(conference) "
          + "OPTIONAL MATCH (planItem:PlanItem)-[obeys:OBEYS]->(structureItem:StructureItem) "
          + "OPTIONAL MATCH (comment)-[likedBy:LIKED_BY]->(personLikedBy:Person) "
          + "OPTIONAL MATCH (planItem)-[:COMPOSES*]->(pi3:PlanItem)-[:COMPOSES]->(plan) "
          + "WITH likedBy, personLikedBy, locality, aboutLocality, comment, madeBy, person, planItem, collect(pi3) AS listPi3, "
          + "aboutPlanItem, plan, obeys, structureItem, ofType, localityType, made,"
          + "selfDeclaration, to, conference, localitySD, asBeingFrom "
          + WHERE_TEXT_FILTER
          + "RETURN comment, aboutLocality, locality, person, madeBy, ofType, localityType, localitySD, asBeingFrom, "
          + "aboutPlanItem, planItem, obeys, structureItem, likedBy, personLikedBy, made, selfDeclaration, to, conference",
      countQuery =
          "MATCH (comment:Comment)-[:ABOUT]->(conference:Conference), "
              + "(conference)-[target:TARGETS]-(plan:Plan), "
              + "(comment)-[ab1:ABOUT]->(child:PlanItem)-[composesChild:COMPOSES*]->(parent:PlanItem) "
              + "OPTIONAL MATCH (comment)-[aboutLocality:ABOUT]->(locality:Locality)-[ofType:OF_TYPE]->(localityType) "
              + "WITH locality, aboutLocality, comment, plan, parent, composesChild, child, ofType, localityType, conference "
              + WHERE_FILTER
              + "MATCH (comment)-[madeBy:MADE_BY]->(person:Person)-[made:MADE]->(selfDeclaration:SelfDeclaration) "
              + "MATCH (comment)-[aboutPlanItem:ABOUT]->(planItem:PlanItem) "
              + "MATCH (localitySD:Locality)<-[asBeingFrom:AS_BEING_FROM]-(selfDeclaration:SelfDeclaration)-[to:TO]->(conference) "
              + "OPTIONAL MATCH (planItem:PlanItem)-[obeys:OBEYS]->(structureItem:StructureItem) "
              + "OPTIONAL MATCH (comment)-[likedBy:LIKED_BY]->(personLikedBy:Person) "
              + "OPTIONAL MATCH (planItem)-[:COMPOSES*]->(pi3:PlanItem)-[:COMPOSES]->(plan) "
              + "WITH likedBy, personLikedBy, locality, aboutLocality, comment, madeBy, person, planItem, collect(pi3) AS listPi3, "
              + "aboutPlanItem, plan, obeys, structureItem, ofType, localityType, made,"
              + "selfDeclaration, to, conference, localitySD, asBeingFrom "
              + WHERE_TEXT_FILTER
              + "RETURN count(DISTINCT comment)"
  )
  Page<Comment> findAllCommentsByConference(Long idConference, String status, String text, Long[] localityIds, Long[] planItemIds, Pageable pageable);

  @Query(" MATCH (co:Conference)-[a:ABOUT]-(c:Comment) "
      + " WHERE id(co)={0} AND NOT c.status IN ['rem', 'pen']"
      + " RETURN count(c)")
  Integer countCommentByConference(Long id);

  @Query("MATCH (con:Conference)<-[:ABOUT]-(c:Comment)-[:MADE_BY]->(p:Person)" +
      ", (c)-[:ABOUT]->(pi:PlanItem)  " +
      ", (con)-[trg:TARGETS]->(plan:Plan)  " +
      ", (pi)-[:OBEYS]->(si:StructureItem) " +
      "OPTIONAL MATCH (pi)<-[]->(pi2:PlanItem) " +
      "OPTIONAL MATCH (c)-[:ABOUT]->(loc:Locality) " +
      "WITH con, c, p, pi, plan, si, pi2, loc " +
      "WHERE (c.status IN {0} OR {0} = [] OR {0} is NULL) AND (c.from = {1} OR {1} IS NULL) AND id(con)={4} " +
      "AND (id(loc) IN {2} OR {2} = [] OR {2} is NULL) " +
      "AND ((id(pi) IN {3} OR id(pi2) IN {3}) OR {3} = [] OR {3} is NULL) " +
      "AND (id(si) IN {5} OR {5} = [] OR {5} is NULL) " +
      "OPTIONAL MATCH (c)-[mb:MODERATED_BY]->(m:Person) " +
      "OPTIONAL MATCH (plan)<-[:COMPOSES]-(piArea:PlanItem)-[]-(pi) " +
      "RETURN DISTINCT id(con) AS conferenceId, id(c) AS commentId, c.status AS status, c.text AS text, c.time AS time, " +
      "c.type AS type, p.name AS citizenName, m.name AS moderatorName, mb.time AS moderateTime, mb.finish AS moderated, " +
      "id(m) AS moderatorId, id(loc) AS localityId, loc.name AS localityName, c.from as from, " +
      "id(pi) AS planItemId, pi.name AS planItemName, id(si) AS structureItemId, si.name AS structureItemName, " +
      "id(piArea) AS areaEstrategicaId, piArea.name AS nameAreaEstrategica "+
      "ORDER BY c.time")
  List<ModerationResultDto> findAllByStatus(
      String[] status,
      String from,
      Long[] localityIds,
      Long[] planItemIds,
      Long conferenceId,
      Long[] structureItemIds);

  @Query("MATCH (com:Comment)-[ab:ABOUT]->(pi1:PlanItem), " +
      "(com)-[:ABOUT]->(conf:Conference) " +
      "WHERE id(com)={0} AND id(conf)={1} " +
      "OPTIONAL MATCH (com)-[:ABOUT]->(loc:Locality) " +
      "OPTIONAL MATCH (com)-[:MADE_BY]->(p:Person) " +
      "OPTIONAL MATCH (com)-[:MODERATED_BY]->(m:Person) " +
      "OPTIONAL MATCH (loc)<-[locin:IS_LOCATED_IN*]-(loc2:Locality)-[oftp:OF_TYPE]->(locType:LocalityType) " +
      "RETURN DISTINCT id(com) AS commentId, com.status AS status, com.text AS text, com.time AS time, com.type AS type, " +
      "p.name AS citizenName, m.name AS moderatorName, loc.name AS localityName, locType.name AS localityType, " +
      "com.from as from ,id(loc) AS localityId")
  ModerationResultDto findModerationResultById(Long idComment, Long idConference);

  @Query("MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
      "WHERE id(comm)={0} " +
      "RETURN pi1 AS planItems " +
      "UNION MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
      "WHERE id(comm)={0} " +
      "OPTIONAL MATCH (pi1)-[:COMPOSES*]->(pi2:PlanItem) " +
      "RETURN pi2 AS planItems")
  Collection<PlanItem> findModerationPlanItemsByCommentId(Long idComment);

  @Query("MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
      "WHERE id(comm)={0} " +
      "MATCH (pi1)-[:OBEYS]->(si1:StructureItem) " +
      "RETURN si1 AS structureItems " +
      "UNION MATCH (comm:Comment)-[ab:ABOUT]->(pi1:PlanItem) " +
      "WHERE id(comm)={0} " +
      "MATCH (pi1)-[:OBEYS]->(si1:StructureItem) " +
      "OPTIONAL MATCH (pi1)-[:COMPOSES*]->(pi2:PlanItem) " +
      "OPTIONAL MATCH (pi2)-[:OBEYS]->(si2:StructureItem) " +
      "RETURN si2 AS structureItems")
  Collection<StructureItem> findModerationStructureItemsByCommentId(Long idComment);

  @Query(" MATCH (c:Comment) "
      + " WHERE id(c)={0} "
      + " RETURN c "
      + " ,[ "
      + "		[(c)-[lk:LIKED_BY]->(p:Person) | [lk,p]],"
      + "		[(c)-[mb:MADE_BY]->(pe:Person) | [mb,pe]],"
      + "  	[(c)-[about:ABOUT]-(planI:PlanItem) | [about,planI]], "
      + "		[(l:Locality)<-[a:ABOUT]-(c) | [a,l]]"
      + " ]")
  Comment findPersonLiked(Long idComment);

  @Query("MATCH (p:Person)<-[a:MADE_BY]-(c:Comment) "
      + "WHERE id(p)={0} "
      + "RETURN c, p, a"
  )
  List<Comment> findAllCommentsMadeByPerson(Long personId);

  @Query("MATCH (p:Person)<-[a:LIKED_BY]-(c:Comment) "
      + "WHERE id(p)={0} "
      + "RETURN c, a, p"
  )
  List<Comment> findAllCommentsLikedByPerson(Long personId);
}
