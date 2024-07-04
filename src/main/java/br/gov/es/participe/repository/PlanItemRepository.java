package br.gov.es.participe.repository;

import br.gov.es.participe.controller.dto.LeanPlanItemResultDto;
import br.gov.es.participe.model.PlanItem;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlanItemRepository extends Neo4jRepository<PlanItem, Long> {

       @Query("MATCH (pi:PlanItem) WHERE pi.name CONTAINS ($name) RETURN pi")
       List<PlanItem> search( @Param("name") String name);
     
       @Query("MATCH (con:Conference)-[trg:TARGETS]->(plan:Plan)-[ob:OBEYS]->(stt:Structure)<-[comp:COMPOSES]-(si2:StructureItem)<-[comp2:COMPOSES]-(si:StructureItem) "
              + "WHERE id(con) =$id "
              + "OPTIONAL MATCH (plan)<-[comp3:COMPOSES]-(pi:PlanItem) "
              + "OPTIONAL MATCH (pi)<-[comp4:COMPOSES]-(pi2:PlanItem) "
              + "WITH con, trg, ob, stt, comp, si, si2, plan, pi, collect({planItemId: id(pi2), planItemName: pi2.name}) AS planitemsChildren "
              + "ORDER BY pi.name ASC "
              + "RETURN id(con) AS conferenceId, "
              + "con.description AS conferenceDescription, "
              + "id(si2) AS structureItemId, "
              + "si2.name AS structureItemName, "
              + "id(si) AS structureItemChildrenId, "
              + "si.name AS structureItemChildrenName, "
              + "collect({planItemId: id(pi), planItemName: pi.name, planitemsChildren: planitemsChildren}) AS planItems"
       )
       LeanPlanItemResultDto findByConferenceIdWithDto( @Param("id") Long id);

       @Query("MATCH(con:Conference)-[is:IS_SEGMENTABLE_BY]->(si:StructureItem)<-[ob:OBEYS]-(pi:PlanItem) WHERE id(con) = $idConference RETURN pi")
       List<PlanItem> findByIdConference( @Param("idConference") Long idConference);
     
       @Query("MATCH (pi:PlanItem) WHERE id(pi) = $id " + " RETURN pi, [ "
              + " [(pi)-[o1:OBEYS]->(s1:StructureItem) | [ o1, s1 ]],"
              + " [(pi)<-[apt1:APPLIES_TO]-(l1:Locality) | [apt1, l1]],"
              + " [(pi)<-[com:COMPOSES*]-(child:PlanItem) | [com, child]],"
              + " [(child)<-[apt2:APPLIES_TO]-(l2:Locality) | [apt2, l2]]" + "]")
       Optional<PlanItem> findByIdWithLocalities( @Param("id") Long id);

       @Query("MATCH (p:PlanItem)<-[com:COMPOSES*]-(child:PlanItem) " + 
              " WHERE id(p) = $idPlanItem " + 
              " AND ( NOT EXISTS {" + 
              "  MATCH (child)<-[apt:APPLIES_TO]-(l:Locality)" + 
              " } OR  EXISTS { MATCH (child)<-[apt]-(l) WHERE id(l)=$idLocality  }   )" + 
              " RETURN p, [ child, com, [(pi)-[o1:OBEYS]->(s1:StructureItem) where id(pi)=$idPlanItem | [ o1, s1 ]] ]")
       Optional<PlanItem> findByPlanItemChildren(@Param("idPlanItem") Long idPlanItem, @Param("idLocality") Long idLocality);
     
       @Query("MATCH(p:Plan)<-[co:COMPOSES]-(pi:PlanItem) " +
              "WHERE id(p)=$idPlan " +
              "RETURN co, pi, [ " +
              "  [ (pi)<-[ab:ABOUT]-(a:Attend) | [ab,a] ] " +
              "] "
       )
       List<PlanItem> findAllByIdPlan( @Param("idPlan") Long idPlan);
     
       @Query(" MATCH(p:PlanItem)<-[c:COMPOSES]-(pi:PlanItem) " + " WHERE id(p) = $idPlanItem" + " RETURN pi")
       List<PlanItem> findChildren( @Param("idPlanItem") Long idPlanItem);
     
       @Query("MATCH (p:PlanItem) DETACH DELETE p")
       void deleteAll();
     
       @Query("MATCH (node:PlanItem)<-[:COMPOSES]-(node2:PlanItem)" + "WHERE id(node2)=$idPlanItem" + "RETURN node")
       Optional<PlanItem> findFatherPlanItem( @Param("idPlanItem") Long idPlanItem);
     
       @Query("MATCH (c:Comment)-[about:ABOUT]->(planI:PlanItem)-[o:OBEYS]->(si:StructureItem) " + " WHERE id(c)=$idComment"
              + " RETURN planI, o, si" + " ,[ " + " 		[(planI)-[com:COMPOSES*]->(parent:PlanItem) | [com,parent]],"
              + " 		[(parent)-[ob:OBEYS]->(siP:StructureItem) | [ob,siP]]" + "]")
       PlanItem findParentsByCommentId( @Param("idComment") Long idComment);
}
