package br.gov.es.participe.repository;

import br.gov.es.participe.model.Plan;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;


public interface PlanRepository extends Neo4jRepository<Plan, Long> {

    @Query(" MATCH (p:Plan) OPTIONAL MATCH (p)<-[bt:COMPOSES]-(i:PlanItem) "+
    		   " OPTIONAL MATCH (p)-[reg:REGIONALIZABLE]->(lt:LocalityType) "+
    		   " RETURN p, bt, i, reg, lt, [ " +
               " [ (p)-[r_a1:APPLIES_TO]->(d1:Domain) | [ r_a1, d1 ] ], " +
               " [ (p)-[r_o1:OBEYS]->(s1:Structure) | [ r_o1, s1 ] ], " +
               " [ (i)-[bts:OBEYS]->(st:StructureItem)| [bts, st] ], " +
               " [ (i)<-[bti:COMPOSES*]-(pi:PlanItem)-[bts:OBEYS]-(st:StructureItem)| [bti, pi,bts, st] ] " +
               "]")
    Collection<Plan> findAll();
    
    @Query(" MATCH (p:Plan)-[t:TARGETS]-(c:Conference) "
    		+" WHERE id(c)=$id "
    		+" RETURN p"
    		+" ,[ "
    		+" 		[(p)<-[bti:COMPOSES]-(pi:PlanItem) | [bti, pi]],"
    		+" 		[(p)-[r:REGIONALIZABLE]->(lt:LocalityType) | [r, lt]],"
    		+" 		[(pi)-[bts:OBEYS]->(st:StructureItem)| [bts, st]], "
    		+" 		[(pi)-[a:ABOUT]->(at:Attend)| [a, at]], "
    		+" 		[(pi)<-[com:COMPOSES]-(pli:PlanItem) | [com,pli]]"
    		+" ]")
    Plan findByConference( @Param("id") Long id);
    
    @Query(" MATCH (p:Plan)-[t:TARGETS]-(c:Conference) "
    		+" WHERE id(c)=$id "
    		+" RETURN p"
    		+" ,[ "
    		+" 		[(p)<-[bti:COMPOSES]-(pi:PlanItem) | [bti, pi]],"
    		+" 		[(pi)-[bts:OBEYS]->(st:StructureItem)| [bts, st]], "
    		+" 		[(pi)<-[com:COMPOSES]-(pli:PlanItem) | [com,pli]]"
    		+" ]")
    Plan findByConferenceWithPlanItem( @Param("id")Long id);

	@Query(" MATCH (p:Plan)-[a:APPLIES_TO]->(d:Domain) "
			+" WHERE id(d)=$id "
			+" RETURN p"
			+" ,[ "
			+" 		[(p)<-[bti:COMPOSES]-(pi:PlanItem) | [bti, pi]],"
			+" 		[(p)-[r:REGIONALIZABLE]->(lt:LocalityType) | [r, lt]],"
			+" 		[(pi)-[bts:OBEYS]->(st:StructureItem)| [bts, st]], "
			+" 		[(pi)-[a:ABOUT]->(at:Attend)| [a, at]], "
			+" 		[(pi)<-[com:COMPOSES]-(pli:PlanItem) | [com,pli]]"
			+" ]")
	Collection<Plan> findByDomain( @Param("id") Long id);
    
    @Query(" MATCH (p:Plan)<-[c:COMPOSES*]-(pi1:PlanItem) "
            +" WHERE id(pi1)=$id "
            +" RETURN p, c, pi1 "
            +" ,[ "
            +"         [(p)<-[bti:COMPOSES*]-(pi2:PlanItem) | [bti, pi2]],"
            +"         [(p)-[r:REGIONALIZABLE]->(lt:LocalityType) | [r, lt]],"
            +"         [(pi2)-[bts:OBEYS]->(st:StructureItem)| [bts, st]], "
            +"         [(pi2)<-[com:COMPOSES*]-(pli:PlanItem) | [com,pli]]"
            +" ]")
    Plan findByPlanItem( @Param("id") Long id);

    @Query("OPTIONAL MATCH (plan:Plan) WHERE ($name IS NULL OR apoc.text.clean(plan.name) CONTAINS apoc.text.clean($name)) "
            + " OPTIONAL MATCH (planItem:PlanItem)-[c:COMPOSES*]->(parentPlan:Plan) WHERE ($name IS NULL OR apoc.text.clean(planItem.name) CONTAINS apoc.text.clean($name)) "
            + " OPTIONAL MATCH (planItem)-[c2:COMPOSES]->(parentPlan:Structure) "
            + " OPTIONAL MATCH (planItem)<-[c3:COMPOSES*]-(child:PlanItem) "
			+ " OPTIONAL MATCH (plan)-[reg:REGIONALIZABLE]->(lt:LocalityType) "
			+ " OPTIONAL MATCH (parentPlan)-[reg2:REGIONALIZABLE]->(lt2:LocalityType) "
            + " RETURN plan, planItem, c, parentPlan, c2, c3, child, reg, lt, reg2, lt2 "
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
    Collection<Plan> findByName( @Param("name") String name);
    
    @Query(" MATCH (p:Plan)-[c:COMPOSES*]-(pi:PlanItem) "
    		+" WHERE id(p)=$id"
    		+" RETURN p, c, pi "
    		+" ,[ "
    		+" 		[(pi)-[fe:FEATURES]-(f:File) | [fe,f]], "
    		+" 		[(pi)-[o:OBEYS]-(s:StructureItem) | [o,s]], "
    		+" 		[(pi)-[a:APPLIES_TO]-(l:Locality) | [a,l]] "
    		+"]")
    Plan findFilesById( @Param("id") Long id);

    @Query(" MATCH (p:Plan)-[c:COMPOSES*]-(pi:PlanItem) "
    +" WHERE id(p)=$id AND (EXISTS {MATCH (p:Plan)-[c:COMPOSES*]-(pi)-[fe:FEATURES]-(f:File) WHERE id(p)=$id })"
    +" RETURN p"
    +" ,[  [(p:Plan)-[c:COMPOSES*]-(pi:PlanItem) | [c, pi]], " 
    +" 		[(pi)-[fe:FEATURES]-(f:File) | [fe,f]], "
    +" 		[(pi)-[o:OBEYS]-(s:StructureItem) | [o,s]], "
    +" 		[(pi)-[a:APPLIES_TO]-(l:Locality) | [a,l]] "
    +"]")
Plan findParticipationFilesById( @Param("id") Long id);

    @Query("MATCH (p:Plan) DETACH DELETE p")
    void deleteAll();
}
