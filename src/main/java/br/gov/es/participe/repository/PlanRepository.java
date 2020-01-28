package br.gov.es.participe.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.Domain;
import br.gov.es.participe.model.Plan;

public interface PlanRepository extends Neo4jRepository<Plan, Long> {

    @Query(" MATCH (p:Plan) OPTIONAL MATCH (p)<-[bt:BELONGS_TO]-(i:PlanItem) RETURN p, bt, i, [ " +
               " [ (p)-[r_a1:APPLIES_TO]->(d1:Domain) | [ r_a1, d1 ] ], " +
               " [ (p)-[r_o1:OBEYS]->(s1:Structure) | [ r_o1, s1 ] ], " +
               " [ (i)-[bts:BELONGS_TO]->(st:StructureItem)| [bts, st] ], " +
               " [ (i)<-[bti:BELONGS_TO*]-(pi:PlanItem)-[bts:BELONGS_TO]-(st:StructureItem)| [bti, pi,bts, st] ] " +
               "]")
    Collection<Plan> findAll();

    @Query(" MATCH (p:Plan) WHERE LOWER(p.name) CONTAINS LOWER($name) OPTIONAL MATCH (p)<-[bt:BELONGS_TO]-(i:PlanItem) RETURN p, bt, i, [ " +
               " [ (p)-[r_a1:APPLIES_TO]->(d1:Domain) | [ r_a1, d1 ] ], " +
               " [ (p)-[r_o1:OBEYS]->(s1:Structure) | [ r_o1, s1 ] ], " +
               " [ (i)-[bts:BELONGS_TO]->(st:StructureItem)| [bts, st] ], " +
               " [ (i)<-[bti:BELONGS_TO*]-(pi:PlanItem)-[bts:BELONGS_TO]-(st:StructureItem)| [bti, pi,bts, st] ] " +
               "] ORDER BY p.name")
    List<Plan> findByName(String name);
}
