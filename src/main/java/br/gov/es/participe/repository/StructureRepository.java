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

    @Query("MATCH () "
            + " OPTIONAL MATCH (structure:Structure) WHERE toLower(structure.name) CONTAINS toLower($name) "
            + " OPTIONAL MATCH (structureItem:StructureItem)-[c:COMPOSES*]->(parentStructure:Structure) WHERE toLower(structureItem.name) CONTAINS toLower($name) "
            + " OPTIONAL MATCH (structureItem)-[c2:COMPOSES]->(parentStructure:Structure) "
            + " OPTIONAL MATCH (structureItem)<-[c3:COMPOSES*]-(child:StructureItem) "
            + " RETURN structure, structureItem, c, parentStructure, c2, c3, child "
            + ",[ "
            + "      [ (structure)<-[c4:COMPOSES*]-(si:StructureItem) | [ c4, si ] ] "
            + "     ,[ (structure)<-[o:OBEYS]-(p:Plan) | [ o, p ] ] "
            + "     ,[ (parentStructure)<-[c5:COMPOSES*]-(si2:StructureItem)<-[c6:COMPOSES*]-(structureItem) | [ c5, si2, c6 ] ] "
            + " ] "
    )
    Collection<Structure> findByName(String name);
}
