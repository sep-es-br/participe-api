package br.gov.es.participe.repository;

import br.gov.es.participe.model.File;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.*;

public interface FileRepository extends Neo4jRepository<File, Long> {

    @Query("MATCH (f:File) DETACH DELETE f")
    void deleteAll();

    @Query("MATCH (c:Conference)<-[:IS_BACKGROUND_IMAGE_OF]-(f:File)" +
            " WHERE id(c)={0} " +
            " RETURN f")
    List<File> findAllBackGroundImageFromIdConference(Long idConference);

    @Query("MATCH (c:Conference)<-[:IS_BACKGROUND_IMAGE_OF]-(f:File) WHERE id(c)={0} with f, rand() AS r ORDER BY r RETURN f LIMIT 1")
    File findRandomackGroundImage(Long idConference);
}
