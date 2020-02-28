package br.gov.es.participe.repository;

import br.gov.es.participe.model.Plan;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;
import java.util.List;

public interface PlanRepository extends Neo4jRepository<Plan, Long> {

    @Query(" MATCH (p:Plan) OPTIONAL MATCH (p)<-[bt:COMPOSES]-(i:PlanItem) RETURN p, bt, i, [ " +
               " [ (p)-[r_a1:APPLIES_TO]->(d1:Domain) | [ r_a1, d1 ] ], " +
               " [ (p)-[r_o1:OBEYS]->(s1:Structure) | [ r_o1, s1 ] ], " +
               " [ (i)-[bts:OBEYS]->(st:StructureItem)| [bts, st] ], " +
               " [ (i)<-[bti:COMPOSES*]-(pi:PlanItem)-[bts:OBEYS]-(st:StructureItem)| [bti, pi,bts, st] ] " +
               "]")
    Collection<Plan> findAll();

    @Query("MATCH () "
            + " OPTIONAL MATCH (plan:Plan) WHERE toLower(plan.name) CONTAINS toLower($name) "
            + " OPTIONAL MATCH (planItem:PlanItem)-[c:COMPOSES*]->(parentPlan:Plan) WHERE toLower(planItem.name) CONTAINS toLower($name) "
            + " OPTIONAL MATCH (planItem)-[c2:COMPOSES]->(parentPlan:Structure) "
            + " OPTIONAL MATCH (planItem)<-[c3:COMPOSES*]-(child:PlanItem) "
            + " RETURN plan, planItem, c, parentPlan, c2, c3, child  "
            + ", [ "
            + "      [ (plan)-[o:OBEYS]->(s:Structure) | [ o, s ] ] "
            + "     ,[ (plan)-[a:APPLIES_TO]->(d:Domain) | [ a, d ] ] "
            + "     ,[ (plan)<-[c4:COMPOSES*]-(pi:PlanItem) | [c4, pi] ] "
            + "     ,[ (pi)-[o:OBEYS]->(si:StructureItem) | [ o, si ] ] "
            + "     ,[ (planItem)-[o2:OBEYS]->(si2:StructureItem)| [ o2, si2 ] ] "
            + "     ,[ (planItem)-[c5:COMPOSES*]->(pi2:PlanItem)-[c6:COMPOSES*]->(parentPlan) | [ c5, pi2, c6 ] ] "
            + "     ,[ (pi2)-[o3:OBEYS*]->(si3:StructureItem) | [ o3, si3 ] ] "
            + "     ,[ (parentPlan)-[a2:APPLIES_TO]->(d2:Domain) | [ a2, d2 ] ] "
            + "     ,[ (parentPlan)-[o4:OBEYS]->(s2:Structure) | [ o4, s2 ] ] "
            + " ] "
    )
    List<Plan> findByName(String name);

    @Query("MATCH (p:Plan) DETACH DELETE p")
    void deleteAll();
}
