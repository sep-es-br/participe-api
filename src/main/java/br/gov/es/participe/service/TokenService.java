package br.gov.es.participe.service;

import br.gov.es.participe.model.Person;
import br.gov.es.participe.util.domain.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenService {

    private static final String ISSUER = "SEP-PI Participe";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expiration;

    @Value("${jwt.refresh-secret}")
    private String refreshSecret;

    @Value("${jwt.refresh-expiration}")
    private String refreshExpiration;
    
    @Autowired
    private PersonService personService;

    public String generateToken(Person person, TokenType tokenType) {
        String expirationValue = TokenType.AUTHENTICATION.equals(tokenType) ? expiration : refreshExpiration;
        String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? secret : refreshSecret;

        Date today = new Date();
        Date expirationDate = new Date(today.getTime() + Long.parseLong(expirationValue));
        Claims claims = Jwts.claims().setSubject(person.getId().toString());
        claims.put("roles", person.getRoles());
        return Jwts.builder()
                .setIssuer(ISSUER)
                .setIssuedAt(today)
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretValue)
                .compact();
    }

    public boolean isValidToken(String token, TokenType tokenType) {
        String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? secret : refreshSecret;

        if (token != null) {
            try {
                Jwts.parser().setSigningKey(secretValue).parseClaimsJws(token);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }

    public Long getPersonId(String token, TokenType tokenType) {
        String secretValue = TokenType.AUTHENTICATION.equals(tokenType) ? secret : refreshSecret;

        Claims claims = Jwts.parser()
                .setSigningKey(secretValue)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }
    
    public Authentication getAuthentication(String token) {
    	Person person = personService.find(getId(token));
    	return new UsernamePasswordAuthenticationToken(person, "", person.getAuthorities());
    }
    
    public Long getId(String token) {
    	String sub = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        return Long.valueOf(sub);
    }
}
