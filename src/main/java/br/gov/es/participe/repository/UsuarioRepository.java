package br.gov.es.participe.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.Usuario;

public interface UsuarioRepository extends Neo4jRepository<Usuario, Long> {
    Usuario findUsuarioByEmail(String email);
}
