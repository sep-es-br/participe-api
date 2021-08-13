package br.gov.es.participe.repository;

import br.gov.es.participe.model.*;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.repository.*;

import java.util.*;

public interface IsLinkedByRepository  extends Neo4jRepository<IsLinkedBy, Long> {

    @Query("MATCH (e: ExternalContent)-[isLinked: IS_LINKED_BY]->(c:Conference) " +
            " WHERE e.url = {0} AND id(c) = {1}" +
            " RETURN isLinked")
    IsLinkedBy findByExternaContentUrlAndConferenceId(String url, Long idConference);

    @Query("MATCH (e: ExternalContent)-[isLinked: IS_LINKED_BY]->(c:Conference) " +
            " WHERE id(c) = {0}" +
            " RETURN isLinked, e, c")
    List<IsLinkedBy> findByConferenceId(Long id);

    @Query("MATCH (e: ExternalContent)-[isLinked: IS_LINKED_BY]->(c:Conference) " +
            " WHERE id(c)={0} " +
            " DELETE isLinked")
    Long deleteAllByConference(Long id);

}
