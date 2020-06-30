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

    @Query("MATCH (d:Domain) WHERE ID(d) = {0} OPTIONAL MATCH (d)<-[bt:IS_LOCATED_IN]-(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) RETURN d, bt, l, ot, lt, [ " +
            " [ (l)<-[btl:IS_LOCATED_IN]-(lc:Locality) | [btl, lc] ], " +
            " [ (l)-[btl:IS_LOCATED_IN]->(lc:Locality) | [btl, lc] ] " +
            " ] ORDER BY d.name")
    Domain findByIdWithLocalities(Long id);

    @Query("MATCH () "
            + " OPTIONAL MATCH (domain:Domain) WHERE  ext.translate(domain.name) CONTAINS ext.translate({0}) "
            + " OPTIONAL MATCH (locality:Locality) WHERE ext.translate(locality.name) CONTAINS ext.translate({0})  "
            + " OPTIONAL MATCH (locality)-[ili:IS_LOCATED_IN]->(parentDomain:Domain) "
            + " OPTIONAL MATCH (locality)<-[i0:IS_LOCATED_IN*]-(child:Locality)-[i2:IS_LOCATED_IN*]->(parentDomain) "
            + " RETURN domain, locality, ili, parentDomain, i0, child, i2, [ "
            + "      [ (domain)<-[ili2:IS_LOCATED_IN*]-(l:Locality)-[ot:OF_TYPE]-(lt:LocalityType) | [ ili2, l, ot, lt ] ] "
            + "     ,[ (parentDomain)<-[ili3:IS_LOCATED_IN*]-(l2:Locality)<-[ili4:IS_LOCATED_IN*]-(locality) | [ ili3, l2, ili4 ] ] "
            + "     ,[ (l2)-[ot2:OF_TYPE]-(lt2:LocalityType) | [ ot2, lt2 ] ] "
            + " ] "
    )
    Collection<Domain> findByName(String name);

    @Query("MATCH (d:Domain) DETACH DELETE d")
    void deleteAll();
}