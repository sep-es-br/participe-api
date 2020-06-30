package br.gov.es.participe.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.AuthService;

public interface AuthServiceRepository  extends Neo4jRepository<AuthService, Long> {

}
