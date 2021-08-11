package br.gov.es.participe.repository;

import br.gov.es.participe.model.*;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.repository.*;

import java.util.*;

public interface ExternalContentRepository extends Neo4jRepository<ExternalContent, Long> {

    ExternalContent findByUrl(String url);

    @Query("MATCH (e:ExternalContent) " +
            " WHERE e.url IN {0} " +
            " RETURN e")
    List<ExternalContent> findExternalContentsByUrls(List<String> urls);
}
