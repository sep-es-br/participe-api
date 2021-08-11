package br.gov.es.participe.repository;

import br.gov.es.participe.model.*;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.repository.*;

import java.util.*;

public interface TopicRepository extends Neo4jRepository<Topic, Long> {

    @Query("MATCH (c:Conference)<-[:GUIDES_HOW_TO_PARTICIPATE_IN]-(t:Topic) " +
            " WHERE id(c)={0} " +
            " RETURN t")
    List<Topic> findAllByConference(Long idConference);

    @Query("MATCH (c:Conference)<-[r:GUIDES_HOW_TO_PARTICIPATE_IN]-(t:Topic) " +
            " WHERE id(c)={0} " +
            " DELETE r, t")
    Long deleteAllByConference(Long idConference);
}
