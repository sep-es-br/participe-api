package br.gov.es.participe.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.Channel;

public interface ChannelRepository extends Neo4jRepository<Channel, Long> {

}
