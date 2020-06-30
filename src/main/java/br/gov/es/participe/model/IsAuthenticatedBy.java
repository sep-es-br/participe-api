package br.gov.es.participe.model;

import java.util.Date;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

@RelationshipEntity(type = "IS_AUTHENTICATED_BY")
public class IsAuthenticatedBy extends Entity{
	
	private String idByAuth;
	private String name;
	private String authType;
	private String email;
	private String password;
	private Boolean temporary_password;
	
	@DateLong
	private Date password_time;
	
	@StartNode
	private Person person;
	
	@EndNode
	private AuthService authService;
	
	public IsAuthenticatedBy() {
	}
	
	public IsAuthenticatedBy(String idByAuth, String name, String authType, String email, String password,
			Boolean temporary_password, Date password_time, Person person, AuthService authService) {

		this.idByAuth = idByAuth;
		this.name = name;
		this.authType = authType;
		this.email = email;
		this.password = password;
		this.temporary_password = temporary_password;
		this.password_time = password_time;
		this.person = person;
		this.authService = authService;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthType() {
		return authType;
	}
	public void setAuthType(String authType) {
		this.authType = authType;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getTemporary_password() {
		return temporary_password;
	}
	public void setTemporary_password(Boolean temporary_password) {
		this.temporary_password = temporary_password;
	}
	public Date getPassword_time() {
		return password_time;
	}
	public void setPassword_time(Date password_time) {
		this.password_time = password_time;
	}
	public String getIdByAuth() {
		return idByAuth;
	}
	public void setIdByAuth(String idByAuth) {
		this.idByAuth = idByAuth;
	}
	public Person getPerson() {
		return person;
	}
	public void setPerson(Person person) {
		this.person = person;
	}
	public AuthService getAuthService() {
		return authService;
	}
	public void setAuthService(AuthService authService) {
		this.authService = authService;
	}
}
