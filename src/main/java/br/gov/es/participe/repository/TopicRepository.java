package br.gov.es.participe.repository;

import br.gov.es.participe.model.*;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface TopicRepository extends Neo4jRepository<Topic, Long> {

    @Query("MATCH (c:Conference)<-[:GUIDES_HOW_TO_PARTICIPATE_IN]-(t:Topic) " +
            " WHERE id(c)=$idConference " +
            " RETURN t")
    List<Topic> findAllByConference( @Param("idConference") Long idConference);

    @Query("MATCH (c:Conference)<-[r:GUIDES_HOW_TO_PARTICIPATE_IN]-(t:Topic) " +
            " WHERE id(c)=$idConference " +
            " DELETE r, t")
    Long deleteAllByConference( @Param("idConference") Long idConference);
}
