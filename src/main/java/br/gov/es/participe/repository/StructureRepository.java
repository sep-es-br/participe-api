package br.gov.es.participe.repository;

import br.gov.es.participe.model.Structure;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Collection;

public interface StructureRepository extends Neo4jRepository<Structure, Long> {

    @Query(" MATCH (s:Structure) OPTIONAL MATCH (s)<-[bt:COMPOSES]-(si:StructureItem) RETURN s, bt, si, [ " +
            " [ (s)<-[p_o1:OBEYS]-(p:Plan) | [ p_o1, p ] ]," +
            " [ (si)<-[btl:COMPOSES*]-(sim:StructureItem) | [btl, sim] ]  " +
            "] ORDER BY s.name")
    Collection<Structure> findAll();

    @Query(" MATCH (s:Structure) WHERE LOWER(s.name) CONTAINS LOWER($name)  OPTIONAL MATCH (s)<-[bt:COMPOSES]-(si:StructureItem) RETURN s, bt, si, [ " +
               " [ (s)<-[p_o1:OBEYS]-(p:Plan) | [ p_o1, p ] ]," +
               " [ (si)<-[btl:COMPOSES*]-(sim:StructureItem) | [btl, sim] ]  " +
            "] ORDER BY s.name")
    Collection<Structure> findByName(String name);
}
