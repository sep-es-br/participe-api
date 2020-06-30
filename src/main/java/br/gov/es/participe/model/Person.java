package br.gov.es.participe.model;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.gov.es.participe.controller.dto.PersonDto;
import br.gov.es.participe.controller.dto.PersonParamDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NodeEntity
public class Person extends Entity implements UserDetails {

    private String name;

    private String contactEmail;

    private String cpf;

    private String telephone;

    private String accessToken;

    @Relationship(type = "IS_AUTHENTICATED_BY")
    private Set<AuthService> authServices;

    @Relationship(type = "MADE")
    private Set<SelfDeclaration> selfDeclaretions;

    @Relationship(type = "LIKED_BY", direction = Relationship.INCOMING)
    private Set<Comment> comments;

    @Relationship(type = "MADE_BY", direction = Relationship.INCOMING)
    private Set<Attend> attends;

    @Relationship(type = "MODERATED_BY")
    private Set<Comment> moderatedComments;
    
    private Set<String> roles;


    public Person() {
    }

    public Person(PersonDto person) {
        setId(person.getId());
        this.name = person.getName();
        this.cpf = person.getCpf();
        this.telephone = person.getTelephone();

        if (person.getCpf() != null)
            this.contactEmail = person.getCpf() + "@cpf";
        else
            this.contactEmail = person.getContactEmail();
    }

    public Person(PersonParamDto person) {
        setId(person.getId());
        this.name = person.getName().trim().replaceAll(" +", " ");
        this.cpf = person.getCpf();
        this.telephone = person.getTelephone();

        if (person.getCpf() != null)
            this.contactEmail = person.getCpf() + "@cpf";
        else
            this.contactEmail = person.getContactEmail();
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

    public Set<AuthService> getAuthServices() {
        if (authServices == null) {
            return Collections.emptySet();
        }

        return Collections.unmodifiableSet(authServices);
    }

    public void addAuthService(AuthService authService) {
        if (authServices == null) {
            authServices = new HashSet<>();
        }

        authServices.add(authService);
    }

    public void addSelfDeclaration(SelfDeclaration selfDeclaration) {
        if (this.selfDeclaretions == null)
            this.selfDeclaretions = new HashSet<>();

        this.selfDeclaretions.add(selfDeclaration);
    }


    public Set<SelfDeclaration> getSelfDeclaretions() {
        return selfDeclaretions;
    }


    public void setSelfDeclaretions(Set<SelfDeclaration> selfDeclaretions) {
        this.selfDeclaretions = selfDeclaretions;
    }


    public void setAuthServices(Set<AuthService> authServices) {
        this.authServices = authServices;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public Set<Attend> getAttends() {
        return attends;
    }

	public void setAttends(Set<Attend> attends) {
		this.attends = attends;
	}

	public Set<Comment> getModeratedComments() {
		return moderatedComments;
	}

	public void setModeratedComments(Set<Comment> moderatedComments) {
		this.moderatedComments = moderatedComments;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Set<String> getRoles() {
        if (roles == null) {
            roles = new HashSet<>();
        }
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<String> personRoles = getRoles();
		List<String> roles = new ArrayList<>();
		if(personRoles != null && !personRoles.isEmpty()) {
			personRoles.forEach(r -> roles.add("ROLE_" + r));
		}
		return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return "";
	}

	@Override
	public String getUsername() {
		return getName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
