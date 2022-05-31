package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.LeanPlanItemResultDto;
import br.gov.es.participe.model.PlanItem;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;

public interface PlanItemRepository extends Neo4jRepository<PlanItem, Long> {

  @Query("MATCH (pi:PlanItem) WHERE ext.translate(pi.name) CONTAINS ext.translate({0}) RETURN pi")
  List<PlanItem> search(String name);

  @Query("MATCH (con:Conference)-[trg:TARGETS]->(plan:Plan)-[ob:OBEYS]->(stt:Structure)<-[comp:COMPOSES]-(si:StructureItem)"
         + "WHERE id(con)={0} "
         + "OPTIONAL MATCH list=(plan)<-[comp2:COMPOSES]-(pi2:PlanItem) "
         + "WITH con, trg, ob, stt, comp, si, plan, comp2, pi2 "
         + "order by pi2.name asc "
         + "RETURN id(con) AS conferenceId, con.description AS conferenceDescription, "
         + "id(si) AS structureItemId, si.name AS structureItemName, "
         + "collect({planItemId: id(pi2), planItemName: pi2.name}) AS planItems"
  )
  LeanPlanItemResultDto findByConferenceIdWithDto(Long id);

  @Query("MATCH(con:Conference)-[is:IS_SEGMENTABLE_BY]->(si:StructureItem)<-[ob:OBEYS]-(pi:PlanItem) WHERE id(con) = {0} RETURN pi")
  List<PlanItem> findByIdConference(Long idConference);

  @Query("MATCH (pi:PlanItem) WHERE id(pi) = {0} " + " RETURN pi, [ "
         + " [(pi)-[o1:OBEYS]->(s1:StructureItem) | [ o1, s1 ]],"
         + " [(pi)<-[apt1:APPLIES_TO]-(l1:Locality) | [apt1, l1]],"
         + " [(pi)<-[com:COMPOSES*]-(child:PlanItem) | [com, child]],"
         + " [(child)<-[apt2:APPLIES_TO]-(l2:Locality) | [apt2, l2]]" + "]")
  Optional<PlanItem> findByIdWithLocalities(Long id);

  @Query("MATCH(p:Plan)<-[co:COMPOSES]-(pi:PlanItem) " +
         "WHERE id(p)={0} " +
         "RETURN co, pi, [ " +
         "  [ (pi)<-[ab:ABOUT]-(a:Attend) | [ab,a] ] " +
         "] "
  )
  List<PlanItem> findAllByIdPlan(Long idPlan);

  @Query(" MATCH(p:PlanItem)<-[c:COMPOSES]-(pi:PlanItem) " + " WHERE id(p) = {0}" + " RETURN pi")
  List<PlanItem> findChildren(Long idPlanItem);

  @Query("MATCH (p:PlanItem) DETACH DELETE p")
  void deleteAll();

  @Query("MATCH (node:PlanItem)<-[:COMPOSES]-(node2:PlanItem)" + "WHERE id(node2)={0}" + "RETURN node")
  Optional<PlanItem> findFatherPlanItem(Long idPlanItem);

  @Query("MATCH (c:Comment)-[about:ABOUT]->(planI:PlanItem)-[o:OBEYS]->(si:StructureItem) " + " WHERE id(c)={0}"
         + " RETURN planI, o, si" + " ,[ " + " 		[(planI)-[com:COMPOSES*]->(parent:PlanItem) | [com,parent]],"
         + " 		[(parent)-[ob:OBEYS]->(siP:StructureItem) | [ob,siP]]" + "]")
  PlanItem findParentsByCommentId(Long idComment);
}
