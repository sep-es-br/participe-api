package br.gov.es.participe.controller.dto;

import java.util.Set;

import br.gov.es.participe.model.Person;

public class PersonDto {

    private Long id;
    private String name;
    private String contactEmail;
    private String cpf;
    private String telephone;
    private SelfDeclarationDto selfDeclaretion;
    private Set<String> roles;
    
    public PersonDto() {
    	 
    }
    
    public PersonDto(Person person) {
        this.id = person.getId();
        this.name = person.getName();
        this.contactEmail = person.getContactEmail();
        this.cpf = person.getCpf();
        this.telephone = person.getTelephone();
        this.roles = person.getRoles();
    }
    
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

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

	public SelfDeclarationDto getSelfDeclaretion() {
		return selfDeclaretion;
	}

	public void setSelfDeclaretion(SelfDeclarationDto selfDeclaretion) {
		this.selfDeclaretion = selfDeclaretion;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
	public String getDefaultRole() {
		if(roles != null && !roles.isEmpty()) {
			if(roles.contains("Administrator")) {
				return "Administrator";
			}
			if(roles.contains("Moderator")) {
				return "Moderator";
			}
			if(roles.contains("Recepcionist")) {
				return "Recepcionist";
			}
		}
		return null;
	}

}
