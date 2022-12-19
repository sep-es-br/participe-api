package br.gov.es.participe.repository;

import br.gov.es.participe.model.*;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface IsLinkedByRepository  extends Neo4jRepository<IsLinkedBy, Long> {

@Query("MATCH (e: ExternalContent)-[isLinked: IS_LINKED_BY]->(c:Conference) " +
" WHERE e.url = $url AND id(c) = $idConference" +
" RETURN isLinked")
IsLinkedBy findByExternaContentUrlAndConferenceId( @Param("url") String url, @Param("idConference")Long idConference);

@Query("MATCH (e: ExternalContent)-[isLinked: IS_LINKED_BY]->(c:Conference) " +
        " WHERE id(c) = $id" +
        " RETURN isLinked, e, c")
List<IsLinkedBy> findByConferenceId( @Param("id") Long id);

@Query("MATCH (e: ExternalContent)-[isLinked: IS_LINKED_BY]->(c:Conference) " +
        " WHERE id(c)=$id " +
        " DELETE isLinked")
Long deleteAllByConference( @Param("id") Long id);

}
