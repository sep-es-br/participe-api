package br.gov.es.participe.repository;

import br.gov.es.participe.model.StructureItem;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface StructureItemRepository extends Neo4jRepository<StructureItem, Long> {

    @Query("MATCH (si:StructureItem) WHERE si.name =~ ('(?i)' + {0} + '.*') RETURN si")
    List<StructureItem> search(String query);

    StructureItem findByNameIgnoreCase(String name);
}
