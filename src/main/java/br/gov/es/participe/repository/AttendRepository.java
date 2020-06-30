package br.gov.es.participe.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import br.gov.es.participe.model.Attend;

public interface AttendRepository extends Neo4jRepository<Attend, Long>{

	@Query("Match(a)-[m:MADE_BY]->(p:Person)"
			+" Where id(p)={0} "
			+" return m, a")
	List<Attend> findAllAttendByIdperson(Long id);
	
	@Query(" MATCH (c:Conference)<-[t:TO]-(self:SelfDeclaration)<-[m:MADE]-(p:Person)<-[ma:MADE_BY]-(a:Attend)"
			+" WHERE id(c)={0} RETURN COUNT(DISTINCT p)")
	Integer countParticipationByConference(Long idConference);
}
