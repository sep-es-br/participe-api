package br.gov.es.participe.controller.dto;

public class PersonParamDto {

	private Long id;
    private String name;
    private String contactEmail;
    private String cpf;
    private String telephone;
    private String password;
    private SelfDeclarationDto selfDeclaretion;
    
    
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public SelfDeclarationDto getSelfDeclaretion() {
		return selfDeclaretion;
	}
	public void setSelfDeclaretion(SelfDeclarationDto selfDeclaretion) {
		this.selfDeclaretion = selfDeclaretion;
	}
    
    
}
