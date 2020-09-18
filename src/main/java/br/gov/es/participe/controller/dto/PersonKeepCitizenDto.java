package br.gov.es.participe.controller.dto;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class PersonKeepCitizenDto {
	
	private Long id;
	
	private String name;
	
	private String email;

	private List<LoginAccessDto> autentication;

	private Long localityId;

	private String localityName;

	private Long numberOfAcesses;

	private String telephone;

	private String typeAuthentication;

	private String cpf;

	private String password;

	private Boolean active;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<LoginAccessDto> getAutentication() {
		return autentication;
	}

	public void setAutentication(List<LoginAccessDto> autentication) {
		this.autentication = autentication;
	}

	public Long getLocalityId() {
		return localityId;
	}

	public void setLocalityId(Long localityId) {
		this.localityId = localityId;
	}

	public String getLocalityName() {
		return localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

	public Long getNumberOfAcesses() {
		return numberOfAcesses;
	}

	public void setNumberOfAcesses(Long numberOfAcesses) {
		this.numberOfAcesses = numberOfAcesses;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getTypeAuthentication() {
		return typeAuthentication;
	}

	public void setTypeAuthentication(String typeAuthentication) {
		this.typeAuthentication = typeAuthentication;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
