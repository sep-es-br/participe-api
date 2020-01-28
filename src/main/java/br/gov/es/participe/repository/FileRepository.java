package br.gov.es.participe.repository;

import br.gov.es.participe.model.File;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FileRepository extends Neo4jRepository<File, Long> {
}
