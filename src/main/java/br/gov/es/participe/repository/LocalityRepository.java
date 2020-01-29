package br.gov.es.participe.repository;

import br.gov.es.participe.model.Locality;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface LocalityRepository extends Neo4jRepository<Locality, Long> {

    @Query("MATCH (l:Locality) WHERE l.name =~ ('(?i)' + {0} + '.*') RETURN l ORDER BY l.name")
    List<Locality> search(String query);

    Locality findByNameIgnoreCase(String name);

    @Query("MATCH (domain:Domain)<-[:BELONGS_TO]-(child:Locality)-[:BELONGS_TO]->(parent:Locality) WHERE id(domain) = {0} AND ID(parent) = {1} RETURN child")
    List<Locality> findChildren(Long idDomain, Long idParent);

    @Query("MATCH (d:Domain)<-[:BELONGS_TO]-(l:Locality)<-[bt:BELONGS_TO]-(lo:Locality)-[:BELONGS_TO]->(do:Domain) WHERE id(d) = {0} AND id(do) = {0} RETURN l, bt, lo")
    List<Locality> findByDomain(Long idDomain);
}
