package br.gov.es.participe.repository;

import br.gov.es.participe.model.Domain;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;

public interface DomainRepository extends Neo4jRepository<Domain, Long> {

    @Query("MATCH (d:Domain) OPTIONAL MATCH (d)<-[bt:IS_LOCATED_IN]-(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) RETURN d, bt, l, ot, lt, [ " +
            " [ (l)<-[btl:IS_LOCATED_IN]-(lc:Locality) | [btl, lc] ], " +
            " [ (l)-[btl:IS_LOCATED_IN]->(lc:Locality) | [btl, lc] ] " +
            " ] ORDER BY d.name")
    Collection<Domain> findAll();

    @Query("MATCH (d:Domain) WHERE ID(d) = $id OPTIONAL MATCH (d)<-[bt:IS_LOCATED_IN]-(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) RETURN d, bt, l, ot, lt, [ " +
            " [ (l)<-[btl:IS_LOCATED_IN]-(lc:Locality) | [btl, lc] ], " +
            " [ (l)-[btl:IS_LOCATED_IN]->(lc:Locality) | [btl, lc] ] " +
            " ] ORDER BY d.name")
    Domain findByIdWithLocalities(Long id);

    Collection<Domain> findByNameContainingIgnoreCase(String name);
}