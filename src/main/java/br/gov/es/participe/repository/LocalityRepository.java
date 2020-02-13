package br.gov.es.participe.repository;

import br.gov.es.participe.model.Locality;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface LocalityRepository extends Neo4jRepository<Locality, Long> {

    @Query("MATCH (l:Locality)-[:OF_TYPE]->(t:LocalityType) "
               + " WHERE TOLOWER(l.name) CONTAINS TOLOWER($quey) "
               + " AND id(t) = $typeId "
               + " RETURN l, [(l)-[lt:OF_TYPE]->(t:LocalityType) | [ lt, t ] ] ORDER BY l.name ")
    List<Locality> search(String quey, Long typeId);

    @Query("MATCH (l:Locality)-[:OF_TYPE]->(t:LocalityType) "
               + " WHERE TOLOWER(l.name) = TOLOWER($name) "
               + " AND id(t) = $typeId "
               + " RETURN l, [(l)-[lt:OF_TYPE]->(t:LocalityType) | [ lt, t ] ] ORDER BY l.name ")
    List<Locality> findByNameAndType(String name, Long typeId);

    @Query("MATCH (domain:Domain)<-[:IS_LOCATED_IN]-(child:Locality)-[:IS_LOCATED_IN]->(parent:Locality) WHERE id(domain) = {0} AND ID(parent) = {1} RETURN child")
    List<Locality> findChildren(Long idDomain, Long idParent);

    @Query("MATCH (d:Domain)<-[:IS_LOCATED_IN]-(l:Locality) WHERE id(d) = $idDomain RETURN l, "
               + "[ (l)<-[btl:IS_LOCATED_IN]-(lc:Locality)-[:IS_LOCATED_IN]->(d) | [btl, lc] ]")
    List<Locality> findByDomain(Long idDomain);
}
