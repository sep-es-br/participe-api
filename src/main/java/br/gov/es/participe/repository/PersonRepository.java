package br.gov.es.participe.repository;

import br.gov.es.participe.model.Person;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

    @Query("MATCH (p:Person)-[IS_AUTHENTICADED_BY]->(as:AuthService) "
            + " WHERE as.server = {0} "
            + "AND as.serverId = {1} "
            + "RETURN p, as;"
    )
    Optional<Person> findByServerAndServerId(String server, String serverId);

	@Query("MATCH (p:Person)-[IS_AUTHENTICADED_BY]->(as:AuthService) "
			+" WHERE (as.server={0}) AND (p.contactEmail={1})"
			+" RETURN p, as;")
	Optional<Person> findByServerAndContactEmail(String server, String email);
	
	@Query("MATCH (p:Person)-[i:IS_AUTHENTICADED_BY]->(as:AuthService) "
			+" WHERE p.contactEmail={0}"
			+" RETURN p, i, as ")
    List<Person> findAllPersonByContactEmail(String email);

    @Query("MATCH (p:Person) "
    		+" WHERE id(p) = {0} "
    		+" OPTIONAL MATCH (p)-[m:MADE]->(s:SelfDeclaration)"
    		+" OPTIONAL MATCH (p)-[i:IS_AUTHENTICATED_BY]->(at:AuthService)"
    		+" RETURN p, m, s, i, at "
    		+" , ["
    		+" 		[(s)-[a:AS_BEING_FROM]-(l:Locality) | [a,l] ],"
    		+" 		[(s)-[t:TO]-(c:Conference) | [t,c] ]"
    		+" ]")
    Person findRelatioships(Long id);
    
    @Query("MATCH (p:Person) "
    		+" WHERE (p.contactEmail={0} AND p.cpf = {1}) "
    		+ "OR (p.contactEmail={0} AND  p.cpf IS NULL) "
    		+ "OR (p.cpf = {1} AND p.contactEmail IS NULL) "
    		+" RETURN p")
    Person findByEmailOrCpf(String email, String cpf);

    
    @Query("MATCH (p:Person) "
    		+" WHERE p.contactEmail={0} "
    		+" RETURN p ")
    Optional<Person> findByContactEmail(String email);
    
    @Query(" MATCH (p:Person) "
    		+" WHERE id(p)={0} "
    		+" RETURN p "
    		+" ,[ "
    		+" 		[(p)<-[lk:LIKED_BY]-(c:Comment) | [lk,c]] "
    		+" ] ")
    Person likescomments(Long idPerson);
    
    Person findByContactEmailIgnoreCase(String email);
    
    Person findByCpfIgnoreCase(String cpf);

    @Query(
		"MATCH (com:Comment)-[:MADE_BY]->(p:Person) " +
		"WHERE id(com)={0} " +
		"RETURN p"
	)
    Person findByCommentId(Long commentId);
    
    @Query(" MATCH (c:Comment)-[m:MADE_BY]->(p:Person) "
    		+" WHERE id(c)={0} "
    		+" RETURN p ")
    Person findPersonMadeByIdComment(Long idComment);
    
    @Query(" MATCH (c:Comment)-[m:LIKED_BY]->(p:Person) "
    		+" WHERE id(c)={0} "
    		+" RETURN p ")
    List<Person> findPersonLikedByIdComment(Long idComment);
    
    @Query("MATCH (p:Person)-[i:IS_AUTHENTICATED_BY]->(a:AuthService) WHERE p.contactEmail={0} AND a.server={1} RETURN p")
    Person validate(String email, String server);
}
