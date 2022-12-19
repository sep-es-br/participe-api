package br.gov.es.participe.repository;

import br.gov.es.participe.model.*;
import org.springframework.data.neo4j.annotation.*;
import org.springframework.data.neo4j.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface PortalServerRepository extends Neo4jRepository<PortalServer, Long> {

        @Query(" MATCH (p:PortalServer) " +
        " WHERE p.url = $url " +
        " return p, " +
        " [" +
        "   [(p)-[h:HOSTS]->(c:Conference) | [h, c]]," +
        "   [(c)-[is:IS_DEFAULT]->(po:PortalServer) | [is, po]]," +
        "   [(p)<-[i:IS_DEFAULT]-(co:Conference) | [i, co]]" +
        " ] ")
Optional<PortalServer> findByUrl( @Param("url") String url);

@Query("MATCH (p:PortalServer)-[h:HOSTS]->(c:Conference) " +
        " WHERE id(c)=$id " +
        " RETURN h, p, c")
Optional<PortalServer> findByIdConference( @Param("id") Long id);
}
