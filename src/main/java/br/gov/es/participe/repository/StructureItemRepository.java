package br.gov.es.participe.repository;

import br.gov.es.participe.model.StructureItem;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StructureItemRepository extends Neo4jRepository<StructureItem, Long> {

    @Query("MATCH (si:StructureItem) WHERE si.name =~ ('(?i)' + $query + '.*') RETURN si")
    List<StructureItem> search( @Param("query") String query);

    StructureItem findByNameIgnoreCase( @Param("name") String name);
    
    @Query(" MATCH (pi:PlanItem)-[o:OBEYS]-(s:StructureItem) "
    		+" WHERE id(pi)=$idPlanItem"
    		+" RETURN s "
    		+" ,[ "
    		+" 		[(s)-[c:COMPOSES]-(si:StructureItem) | [c,si]] "
    		+" ]")
    StructureItem findByIdPlanItem( @Param("idPlanItem") Long idPlanItem);
    
    @Query(" MATCH (s:StructureItem)<-[c:COMPOSES]-(si:StructureItem) "
    		+" WHERE id(s)=$idParent "
    		+" RETURN si LIMIT 1")
    StructureItem findChild( @Param("idParent") Long idParent);

    @Query("MATCH (s:StructureItem) DETACH DELETE s")
    void deleteAll();

    @Query("MATCH (s:Structure)<-[c:COMPOSES*]-(si:StructureItem) WHERE id(s)=$idStructure RETURN si")
    List<StructureItem> findByStructure( @Param("idStructure") Long idStructure);

    @Query("MATCH (si:StructureItem) WHERE id(si) IN {0} RETURN si")
    List<StructureItem> findByIds( @Param("targetedByItems") List<Long> targetedByItems);
}
