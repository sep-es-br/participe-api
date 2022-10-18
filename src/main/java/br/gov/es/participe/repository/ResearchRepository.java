package br.gov.es.participe.repository;

import br.gov.es.participe.model.*;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ResearchRepository extends Neo4jRepository<Research, Long> {

    @Query("MATCH (c:Conference)<-[:APPLIES_TO]-(r:Research) " +
            " WHERE id(c)=$id " +
            " RETURN r")
    Research findByConferenceId( @Param("id") Long id);

    @Query("MATCH (c:Conference)<-[ato:APPLIES_TO]-(r:Research) WHERE r.displayMode STARTS WITH 'AUTOMATIC' RETURN r")
    List<Research> findAllAutomatic();
}
