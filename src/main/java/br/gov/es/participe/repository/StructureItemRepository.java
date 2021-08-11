package br.gov.es.participe.repository;

import br.gov.es.participe.model.StructureItem;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface StructureItemRepository extends Neo4jRepository<StructureItem, Long> {

    @Query("MATCH (si:StructureItem) WHERE si.name =~ ('(?i)' + {0} + '.*') RETURN si")
    List<StructureItem> search(String query);

    StructureItem findByNameIgnoreCase(String name);
    
    @Query(" MATCH (pi:PlanItem)-[o:OBEYS]-(s:StructureItem) "
    		+" WHERE id(pi)={0}"
    		+" RETURN s "
    		+" ,[ "
    		+" 		[(s)-[c:COMPOSES]-(si:StructureItem) | [c,si]] "
    		+" ]")
    StructureItem findByIdPlanItem(Long idPlanItem);
    
    @Query(" MATCH (s:StructureItem)<-[c:COMPOSES]-(si:StructureItem) "
    		+" WHERE id(s)={0} "
    		+" RETURN si LIMIT 1")
    StructureItem findChild(Long idParent);

    @Query("MATCH (s:StructureItem) DETACH DELETE s")
    void deleteAll();

    @Query("MATCH (s:Structure)<-[c:COMPOSES*]-(si:StructureItem) WHERE id(s)={0} RETURN si")
    List<StructureItem> findByStructure(Long idStructure);

    @Query("MATCH (si:StructureItem) WHERE id(si) IN {0} RETURN si")
    List<StructureItem> findByIds(List<Long> targetedByItems);
}
