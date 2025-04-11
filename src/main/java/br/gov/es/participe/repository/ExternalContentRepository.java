package br.gov.es.participe.repository;

import br.gov.es.participe.model.*;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ExternalContentRepository extends Neo4jRepository<ExternalContent, Long> {

    ExternalContent findByUrl( @Param("url") String url);

    @Query("MATCH (e:ExternalContent) " +
            " WHERE e.url IN $urls " +
            " RETURN e")
    List<ExternalContent> findExternalContentsByUrls( @Param("urls") List<String> urls);
}
