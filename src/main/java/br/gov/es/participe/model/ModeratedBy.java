package br.gov.es.participe.model;

import java.util.Date;

import org.springframework.data.neo4j.core.support.DateString;

import br.gov.es.participe.util.interfaces.EndNode;
import br.gov.es.participe.util.interfaces.RelationshipEntity;
import br.gov.es.participe.util.interfaces.StartNode;

@RelationshipEntity(type = "MODERATED_BY")
public class ModeratedBy extends Entity{

	private Boolean finish;
	
	@DateString
	private Date time;
	
	@StartNode
	private Comment comment;
	
	@EndNode
	private Person person;
	
	public ModeratedBy() {
	}

	public ModeratedBy(Boolean finish, Date time, Comment comment, Person person) {
		this.finish = finish;
		this.time = time;
		this.comment = comment;
		this.person = person;
	}

	public Boolean getFinish() {
		return finish;
	}

	public void setFinish(Boolean finish) {
		this.finish = finish;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Comment getComment() {
		return comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
